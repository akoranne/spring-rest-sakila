// Feature: legacy-modernization
// Property 5: Downstream Service Unavailability Returns 503
// Property 7: Correlation ID Propagation
// Property 17: Downstream Error Propagation

process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-property-tests';
process.env.PAYMENT_SERVICE_URL = 'http://localhost:19876';
process.env.RENTAL_SERVICE_URL = 'http://localhost:19877';

const http = require('http');
const fc = require('fast-check');
const jwt = require('jsonwebtoken');
const express = require('express');
const request = require('supertest');
const httpClient = require('../../src/utils/httpClient');
const correlationIdMiddleware = require('../../src/middleware/correlationId');
const errorHandler = require('../../src/middleware/errorHandler');
const { jwtAuth, requireRole } = require('../../src/middleware/auth');

const JWT_SECRET = process.env.JWT_SECRET;

const makeToken = (roles = ['ROLE_MANAGE']) =>
  jwt.sign({ sub: 'test@example.com', roles }, JWT_SECRET, { expiresIn: '1h' });

// Helper: create a minimal Express app that simulates inter-service call behavior
// This mirrors how the customer service details endpoint would work
function createTestApp() {
  const app = express();
  app.use(express.json());
  app.use(correlationIdMiddleware);

  // Endpoint that calls a downstream service and returns the result
  app.get('/test/downstream', jwtAuth, requireRole('ROLE_MANAGE'), async (req, res, next) => {
    try {
      const result = await httpClient.get(
        `${process.env.PAYMENT_SERVICE_URL}/payments?customerId=1`,
        {
          correlationId: req.correlationId,
          authToken: req.headers.authorization?.slice(7),
        }
      );
      res.status(200).json(result.data);
    } catch (err) {
      next(err);
    }
  });

  // Endpoint that calls two downstream services (like customer details)
  app.get('/test/aggregate', jwtAuth, requireRole('ROLE_MANAGE'), async (req, res, next) => {
    try {
      const [payments, rentals] = await Promise.all([
        httpClient.get(
          `${process.env.PAYMENT_SERVICE_URL}/payments?customerId=1`,
          { correlationId: req.correlationId, authToken: req.headers.authorization?.slice(7) }
        ),
        httpClient.get(
          `${process.env.RENTAL_SERVICE_URL}/rentals?customerId=1`,
          { correlationId: req.correlationId, authToken: req.headers.authorization?.slice(7) }
        ),
      ]);
      res.status(200).json({ payments: payments.data, rentals: rentals.data });
    } catch (err) {
      next(err);
    }
  });

  app.use(errorHandler);
  return app;
}


// ─── Property 5: Downstream Service Unavailability Returns 503 ───────

describe('Property 5: Downstream Service Unavailability Returns 503', () => {
  /**
   * Validates: Requirements 5.5
   *
   * When downstream service is unreachable (connection refused or timeout),
   * the calling service returns HTTP 503 with standard error format:
   * { error: { code, message, details, timestamp } }
   */

  const app = createTestApp();
  const token = makeToken();

  test('connection refused to downstream yields 503 with standard error format', async () => {
    // No server is listening on the configured PAYMENT_SERVICE_URL port,
    // so the httpClient will get ECONNREFUSED
    const res = await request(app)
      .get('/test/downstream')
      .set('Authorization', `Bearer ${token}`);

    expect(res.status).toBe(503);
    expect(res.body).toHaveProperty('error');
    expect(res.body.error).toHaveProperty('code', 'SERVICE_UNAVAILABLE');
    expect(res.body.error).toHaveProperty('message');
    expect(res.body.error).toHaveProperty('details');
    expect(Array.isArray(res.body.error.details)).toBe(true);
    expect(res.body.error).toHaveProperty('timestamp');
  });

  test('for any random port where nothing listens, httpClient rejects with 503', async () => {
    await fc.assert(
      fc.asyncProperty(
        // Random ports in the ephemeral range unlikely to have a listener
        fc.integer({ min: 40000, max: 49999 }),
        async (port) => {
          try {
            await httpClient.get(`http://127.0.0.1:${port}/anything`);
            // Should not reach here
            throw new Error('Expected rejection');
          } catch (err) {
            expect(err.statusCode).toBe(503);
            expect(err.code).toBe('SERVICE_UNAVAILABLE');
            expect(err.message).toBeDefined();
          }
        }
      ),
      { numRuns: 10 }
    );
  }, 30000);

  test('when either downstream is unavailable in aggregate call, returns 503', async () => {
    // Both payment and rental service URLs point to ports with no listener
    const res = await request(app)
      .get('/test/aggregate')
      .set('Authorization', `Bearer ${token}`);

    expect(res.status).toBe(503);
    expect(res.body.error.code).toBe('SERVICE_UNAVAILABLE');
    expect(res.body.error).toHaveProperty('timestamp');
  });
});


// ─── Property 7: Correlation ID Propagation ──────────────────────────

describe('Property 7: Correlation ID Propagation', () => {
  /**
   * Validates: Requirements 5.6, 10.5
   *
   * All outgoing inter-service calls include the same X-Correlation-ID
   * from the incoming request. If no correlation ID is provided, the
   * service generates one and propagates it.
   */

  test('provided correlation ID is forwarded to downstream service', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.uuid(),
        async (correlationId) => {
          // Spin up a fake downstream that captures received headers
          let receivedHeaders = {};
          const downstream = http.createServer((req, res) => {
            receivedHeaders = { ...req.headers };
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ data: [] }));
          });

          await new Promise((resolve) => downstream.listen(0, resolve));
          const port = downstream.address().port;

          try {
            // Override the payment service URL to point to our fake server
            const originalUrl = process.env.PAYMENT_SERVICE_URL;
            process.env.PAYMENT_SERVICE_URL = `http://127.0.0.1:${port}`;

            const app = createTestApp();
            const token = makeToken();

            const res = await request(app)
              .get('/test/downstream')
              .set('Authorization', `Bearer ${token}`)
              .set('X-Correlation-ID', correlationId);

            expect(res.status).toBe(200);
            // The downstream should have received the same correlation ID
            expect(receivedHeaders['x-correlation-id']).toBe(correlationId);
            // The response should also carry the correlation ID
            expect(res.headers['x-correlation-id']).toBe(correlationId);

            process.env.PAYMENT_SERVICE_URL = originalUrl;
          } finally {
            await new Promise((resolve) => downstream.close(resolve));
          }
        }
      ),
      { numRuns: 10 }
    );
  }, 30000);

  test('when no correlation ID is provided, one is generated and propagated', async () => {
    let receivedHeaders = {};
    const downstream = http.createServer((req, res) => {
      receivedHeaders = { ...req.headers };
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ data: [] }));
    });

    await new Promise((resolve) => downstream.listen(0, resolve));
    const port = downstream.address().port;

    try {
      const originalUrl = process.env.PAYMENT_SERVICE_URL;
      process.env.PAYMENT_SERVICE_URL = `http://127.0.0.1:${port}`;

      const app = createTestApp();
      const token = makeToken();

      const res = await request(app)
        .get('/test/downstream')
        .set('Authorization', `Bearer ${token}`);
        // No X-Correlation-ID header set

      expect(res.status).toBe(200);
      // A correlation ID should have been generated
      const generatedId = res.headers['x-correlation-id'];
      expect(generatedId).toBeDefined();
      expect(typeof generatedId).toBe('string');
      expect(generatedId.length).toBeGreaterThan(0);
      // The downstream should have received the same generated ID
      expect(receivedHeaders['x-correlation-id']).toBe(generatedId);

      process.env.PAYMENT_SERVICE_URL = originalUrl;
    } finally {
      await new Promise((resolve) => downstream.close(resolve));
    }
  });

  test('correlation ID is consistent across multiple downstream calls', async () => {
    const receivedIds = [];
    const makeDownstream = () => {
      const server = http.createServer((req, res) => {
        receivedIds.push(req.headers['x-correlation-id']);
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ data: [] }));
      });
      return server;
    };

    const paymentServer = makeDownstream();
    const rentalServer = makeDownstream();

    await Promise.all([
      new Promise((resolve) => paymentServer.listen(0, resolve)),
      new Promise((resolve) => rentalServer.listen(0, resolve)),
    ]);

    const paymentPort = paymentServer.address().port;
    const rentalPort = rentalServer.address().port;

    try {
      const origPayment = process.env.PAYMENT_SERVICE_URL;
      const origRental = process.env.RENTAL_SERVICE_URL;
      process.env.PAYMENT_SERVICE_URL = `http://127.0.0.1:${paymentPort}`;
      process.env.RENTAL_SERVICE_URL = `http://127.0.0.1:${rentalPort}`;

      const app = createTestApp();
      const token = makeToken();
      const testCorrelationId = 'test-corr-id-12345';

      const res = await request(app)
        .get('/test/aggregate')
        .set('Authorization', `Bearer ${token}`)
        .set('X-Correlation-ID', testCorrelationId);

      expect(res.status).toBe(200);
      // Both downstream services should have received the same correlation ID
      expect(receivedIds).toHaveLength(2);
      expect(receivedIds[0]).toBe(testCorrelationId);
      expect(receivedIds[1]).toBe(testCorrelationId);

      process.env.PAYMENT_SERVICE_URL = origPayment;
      process.env.RENTAL_SERVICE_URL = origRental;
    } finally {
      await Promise.all([
        new Promise((resolve) => paymentServer.close(resolve)),
        new Promise((resolve) => rentalServer.close(resolve)),
      ]);
    }
  });
});


// ─── Property 17: Downstream Error Propagation ──────────────────────

describe('Property 17: Downstream Error Propagation', () => {
  /**
   * Validates: Requirements 15.5
   *
   * When downstream returns an error, the calling service propagates
   * an appropriate HTTP error status (not swallowing the error or
   * returning 200).
   */

  test('downstream 4xx/5xx errors are propagated as appropriate HTTP errors', async () => {
    const errorStatuses = [400, 401, 403, 404, 500, 502, 503];

    await fc.assert(
      fc.asyncProperty(
        fc.constantFrom(...errorStatuses),
        fc.string({ minLength: 5, maxLength: 50 }),
        async (downstreamStatus, errorMessage) => {
          // Spin up a fake downstream that returns the given error status
          const downstream = http.createServer((req, res) => {
            res.writeHead(downstreamStatus, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
              error: { code: 'DOWNSTREAM_ERROR', message: errorMessage, details: [], timestamp: new Date().toISOString() }
            }));
          });

          await new Promise((resolve) => downstream.listen(0, resolve));
          const port = downstream.address().port;

          try {
            const originalUrl = process.env.PAYMENT_SERVICE_URL;
            process.env.PAYMENT_SERVICE_URL = `http://127.0.0.1:${port}`;

            // Create a test app with an endpoint that checks downstream status
            const testApp = express();
            testApp.use(express.json());
            testApp.use(correlationIdMiddleware);

            testApp.get('/test/propagate', jwtAuth, requireRole('ROLE_MANAGE'), async (req, res, next) => {
              try {
                const result = await httpClient.get(
                  `${process.env.PAYMENT_SERVICE_URL}/payments?customerId=1`,
                  { correlationId: req.correlationId, authToken: req.headers.authorization?.slice(7) }
                );

                // If downstream returned an error status, propagate it
                if (result.status >= 400) {
                  const err = new Error(result.data?.error?.message || 'Downstream error');
                  err.statusCode = result.status >= 500 ? 503 : result.status;
                  err.code = result.status >= 500 ? 'SERVICE_UNAVAILABLE' : (result.data?.error?.code || 'DOWNSTREAM_ERROR');
                  err.details = result.data?.error?.details || [];
                  return next(err);
                }

                res.status(200).json(result.data);
              } catch (err) {
                next(err);
              }
            });

            testApp.use(errorHandler);

            const token = makeToken();
            const res = await request(testApp)
              .get('/test/propagate')
              .set('Authorization', `Bearer ${token}`);

            // The response should NOT be 200 — the error must be propagated
            expect(res.status).not.toBe(200);
            expect(res.status).toBeGreaterThanOrEqual(400);

            // Should follow the standard error format
            expect(res.body).toHaveProperty('error');
            expect(res.body.error).toHaveProperty('code');
            expect(res.body.error).toHaveProperty('message');
            expect(res.body.error).toHaveProperty('timestamp');

            // 5xx downstream errors should become 503
            if (downstreamStatus >= 500) {
              expect(res.status).toBe(503);
              expect(res.body.error.code).toBe('SERVICE_UNAVAILABLE');
            }

            process.env.PAYMENT_SERVICE_URL = originalUrl;
          } finally {
            await new Promise((resolve) => downstream.close(resolve));
          }
        }
      ),
      { numRuns: 10 }
    );
  }, 60000);

  test('downstream timeout is treated as 503', async () => {
    // Create a server that never responds (simulates timeout)
    const downstream = http.createServer(() => {
      // Intentionally do nothing — let the request hang
    });

    await new Promise((resolve) => downstream.listen(0, resolve));
    const port = downstream.address().port;

    try {
      const originalUrl = process.env.PAYMENT_SERVICE_URL;
      process.env.PAYMENT_SERVICE_URL = `http://127.0.0.1:${port}`;

      const app = createTestApp();
      const token = makeToken();

      const res = await request(app)
        .get('/test/downstream')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(503);
      expect(res.body.error.code).toBe('SERVICE_UNAVAILABLE');
      expect(res.body.error).toHaveProperty('timestamp');

      process.env.PAYMENT_SERVICE_URL = originalUrl;
    } finally {
      downstream.closeAllConnections?.();
      await new Promise((resolve) => downstream.close(resolve));
    }
  }, 15000);
});
