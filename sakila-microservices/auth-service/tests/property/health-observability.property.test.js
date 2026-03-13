// Feature: legacy-modernization
// Property 13: Health Endpoint Response Format
// Property 14: Readiness Endpoint Reflects Database Connectivity
// Property 15: Structured Request Logging

// Set required env vars before any module imports (config exits if missing)
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-property-tests';

const fc = require('fast-check');
const request = require('supertest');

// Mock the db module before requiring app
jest.mock('../../src/db');
const db = require('../../src/db');

const app = require('../../src/app');
const logger = require('../../src/middleware/logger');

describe('Property 13: Health Endpoint Response Format', () => {
  /**
   * Validates: Requirements 10.1
   *
   * For any running microservice, GET /health should return HTTP 200
   * with a JSON body containing name (string), status (string), and
   * uptime (number) fields.
   */

  test('GET /health returns 200 with name (string), status (string), uptime (number)', async () => {
    // Property: across multiple invocations, the response always
    // conforms to the expected schema
    await fc.assert(
      fc.asyncProperty(fc.constant(null), async () => {
        const res = await request(app).get('/health');

        expect(res.status).toBe(200);
        expect(typeof res.body.name).toBe('string');
        expect(res.body.name.length).toBeGreaterThan(0);
        expect(typeof res.body.status).toBe('string');
        expect(res.body.status).toBe('ok');
        expect(typeof res.body.uptime).toBe('number');
        expect(res.body.uptime).toBeGreaterThanOrEqual(0);
      }),
      { numRuns: 20 }
    );
  });
});


describe('Property 14: Readiness Endpoint Reflects Database Connectivity', () => {
  /**
   * Validates: Requirements 10.2, 10.3
   *
   * For any microservice, GET /health/ready should return HTTP 200 when
   * the database connection is healthy, and HTTP 503 when the database
   * connection is unavailable.
   */

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('returns 200 when database is reachable', async () => {
    await fc.assert(
      fc.asyncProperty(fc.constant(null), async () => {
        db.raw.mockResolvedValue({ rows: [{ '?column?': 1 }] });

        const res = await request(app).get('/health/ready');

        expect(res.status).toBe(200);
        expect(res.body.status).toBe('ready');
      }),
      { numRuns: 10 }
    );
  });

  test('returns 503 when database is unreachable', async () => {
    // Property: for any type of database error, readiness returns 503
    const dbErrorArb = fc.oneof(
      fc.constant(new Error('connection refused')),
      fc.constant(new Error('ECONNRESET')),
      fc.constant(new Error('timeout')),
      fc.constant(new Error('FATAL: database does not exist')),
      fc.constant(new Error('too many connections'))
    );

    await fc.assert(
      fc.asyncProperty(dbErrorArb, async (dbError) => {
        db.raw.mockRejectedValue(dbError);

        const res = await request(app).get('/health/ready');

        expect(res.status).toBe(503);
        expect(res.body.status).toBe('not ready');
      }),
      { numRuns: 10 }
    );
  });
});

describe('Property 15: Structured Request Logging', () => {
  /**
   * Validates: Requirements 10.4
   *
   * For any incoming HTTP request to any microservice, the service should
   * produce a structured JSON log entry containing timestamp, method,
   * path, statusCode, and responseTime fields.
   */

  let logSpy;

  beforeEach(() => {
    logSpy = jest.spyOn(logger, 'info').mockImplementation(() => {});
    db.raw.mockResolvedValue({ rows: [{ '?column?': 1 }] });
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  // Arbitrary: generate valid HTTP methods and paths
  const methodPathArb = fc.oneof(
    fc.constant({ method: 'get', path: '/health' }),
    fc.constant({ method: 'get', path: '/health/ready' })
  );

  test('every request produces a log entry with required fields', async () => {
    await fc.assert(
      fc.asyncProperty(methodPathArb, async ({ method, path }) => {
        logSpy.mockClear();

        await request(app)[method](path);

        // Find the request log entry (requestLogger calls logger.info)
        const logCalls = logSpy.mock.calls;
        const requestLogEntry = logCalls.find(
          (call) =>
            call[0] &&
            typeof call[0] === 'object' &&
            call[0].method !== undefined &&
            call[0].path !== undefined
        );

        expect(requestLogEntry).toBeDefined();

        const logData = requestLogEntry[0];
        expect(typeof logData.timestamp).toBe('string');
        // Verify timestamp is a valid ISO string
        expect(new Date(logData.timestamp).toISOString()).toBe(logData.timestamp);
        expect(typeof logData.method).toBe('string');
        expect(logData.method).toBe(method.toUpperCase());
        expect(typeof logData.path).toBe('string');
        expect(logData.path).toBe(path);
        expect(typeof logData.statusCode).toBe('number');
        expect(typeof logData.responseTime).toBe('number');
        expect(logData.responseTime).toBeGreaterThanOrEqual(0);
      }),
      { numRuns: 20 }
    );
  });

  test('log entry statusCode matches actual response status', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.oneof(
          fc.constant({ dbOk: true, path: '/health/ready', expectedStatus: 200 }),
          fc.constant({ dbOk: false, path: '/health/ready', expectedStatus: 503 }),
          fc.constant({ dbOk: true, path: '/health', expectedStatus: 200 })
        ),
        async ({ dbOk, path, expectedStatus }) => {
          logSpy.mockClear();

          if (dbOk) {
            db.raw.mockResolvedValue({ rows: [{ '?column?': 1 }] });
          } else {
            db.raw.mockRejectedValue(new Error('connection refused'));
          }

          const res = await request(app).get(path);

          expect(res.status).toBe(expectedStatus);

          const requestLogEntry = logSpy.mock.calls.find(
            (call) =>
              call[0] &&
              typeof call[0] === 'object' &&
              call[0].statusCode !== undefined &&
              call[0].path !== undefined
          );

          expect(requestLogEntry).toBeDefined();
          expect(requestLogEntry[0].statusCode).toBe(expectedStatus);
        }
      ),
      { numRuns: 10 }
    );
  });
});
