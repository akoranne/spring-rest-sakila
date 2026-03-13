// Feature: legacy-modernization
// Property 3: Role-Based Access Control Enforcement
// Property 16: Consistent Error Response Format

// Set required env vars before any module imports (config exits if missing)
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-property-tests';

const fc = require('fast-check');
const jwt = require('jsonwebtoken');
const request = require('supertest');

// Mock the db module before requiring app
jest.mock('../../src/db');
const db = require('../../src/db');

const config = require('../../src/config');
const app = require('../../src/app');

// Helper: generate a valid JWT with specified roles
const makeToken = (roles) =>
  jwt.sign({ sub: 'test@example.com', roles }, config.jwtSecret, { expiresIn: '1h' });

// All known roles in the system
const ALL_ROLES = ['ROLE_READ', 'ROLE_WRITE', 'ROLE_MANAGE', 'ROLE_ADMIN'];

// Reset db mock between tests to prevent cross-test contamination
afterEach(() => {
  db.mockReset();
});

// ─── Property 3: Role-Based Access Control Enforcement ───────────────

describe('Property 3: Role-Based Access Control Enforcement', () => {
  /**
   * Validates: Requirements 6.5
   *
   * For any protected endpoint and any valid JWT token whose roles do not
   * include the required role for that endpoint, the service should return
   * HTTP 403.
   */

  // Endpoints requiring ROLE_READ (GET endpoints)
  const readEndpoints = [
    { method: 'get', path: '/films' },
    { method: 'get', path: '/films/1' },
    { method: 'get', path: '/films/1/actors' },
    { method: 'get', path: '/actors' },
    { method: 'get', path: '/actors/1' },
  ];

  // Endpoints requiring ROLE_MANAGE (POST/PUT/DELETE endpoints)
  const manageEndpoints = [
    { method: 'post', path: '/films', body: { title: 'Test', language_id: 1 } },
    { method: 'put', path: '/films/1', body: { title: 'Updated' } },
    { method: 'delete', path: '/films/1', body: {} },
    { method: 'post', path: '/actors', body: { first_name: 'A', last_name: 'B' } },
    { method: 'put', path: '/actors/1', body: { first_name: 'A' } },
    { method: 'delete', path: '/actors/1', body: {} },
  ];

  // Arbitrary: generate a subset of roles that does NOT include the excluded role
  const rolesWithout = (excludedRole) =>
    fc.subarray(ALL_ROLES.filter((r) => r !== excludedRole));

  // Arbitrary: pick a random endpoint from a list
  const endpointArb = (endpoints) =>
    fc.integer({ min: 0, max: endpoints.length - 1 }).map((i) => endpoints[i]);

  test('GET endpoints return 403 when token lacks ROLE_READ', async () => {
    await fc.assert(
      fc.asyncProperty(
        endpointArb(readEndpoints),
        rolesWithout('ROLE_READ'),
        async (endpoint, roles) => {
          const token = makeToken(roles);
          const res = await request(app)
            [endpoint.method](endpoint.path)
            .set('Authorization', `Bearer ${token}`);

          expect(res.status).toBe(403);
          expect(res.body.error.code).toBe('FORBIDDEN');
          expect(res.body.error.message).toBe('Insufficient permissions');
        }
      ),
      { numRuns: 30 }
    );
  }, 30000);

  test('POST/PUT/DELETE endpoints return 403 when token lacks ROLE_MANAGE', async () => {
    await fc.assert(
      fc.asyncProperty(
        endpointArb(manageEndpoints),
        rolesWithout('ROLE_MANAGE'),
        async (endpoint, roles) => {
          const token = makeToken(roles);
          const res = await request(app)
            [endpoint.method](endpoint.path)
            .set('Authorization', `Bearer ${token}`)
            .send(endpoint.body);

          expect(res.status).toBe(403);
          expect(res.body.error.code).toBe('FORBIDDEN');
          expect(res.body.error.message).toBe('Insufficient permissions');
        }
      ),
      { numRuns: 30 }
    );
  }, 30000);

  test('empty roles array returns 403 for any endpoint', async () => {
    const allEndpoints = [
      ...readEndpoints.map((e) => ({ ...e, body: {} })),
      ...manageEndpoints,
    ];

    await fc.assert(
      fc.asyncProperty(
        endpointArb(allEndpoints),
        async (endpoint) => {
          const token = makeToken([]);
          const res = await request(app)
            [endpoint.method](endpoint.path)
            .set('Authorization', `Bearer ${token}`)
            .send(endpoint.body || {});

          expect(res.status).toBe(403);
          expect(res.body.error.code).toBe('FORBIDDEN');
        }
      ),
      { numRuns: 20 }
    );
  }, 30000);
});

// ─── Property 16: Consistent Error Response Format ───────────────────

describe('Property 16: Consistent Error Response Format', () => {
  /**
   * Validates: Requirements 15.1, 15.2, 15.3, 15.4
   *
   * All error responses (4xx/5xx) conform to
   * { error: { code: string, message: string, details: array, timestamp: string } }
   */

  // Helper to validate the standard error response shape
  const assertErrorFormat = (body) => {
    expect(body).toHaveProperty('error');
    expect(typeof body.error.code).toBe('string');
    expect(body.error.code.length).toBeGreaterThan(0);
    expect(typeof body.error.message).toBe('string');
    expect(body.error.message.length).toBeGreaterThan(0);
    expect(Array.isArray(body.error.details)).toBe(true);
    expect(typeof body.error.timestamp).toBe('string');
    // Verify timestamp is valid ISO format
    expect(new Date(body.error.timestamp).toISOString()).toBe(body.error.timestamp);
  };

  // ── 401 Responses ──

  describe('401 responses conform to error format', () => {
    const protectedEndpoints = [
      { method: 'get', path: '/films' },
      { method: 'get', path: '/actors' },
      { method: 'post', path: '/films' },
      { method: 'delete', path: '/actors/1' },
    ];

    test('missing token returns 401 with standard error format', async () => {
      await fc.assert(
        fc.asyncProperty(
          fc.integer({ min: 0, max: protectedEndpoints.length - 1 }),
          async (idx) => {
            const endpoint = protectedEndpoints[idx];
            const res = await request(app)
              [endpoint.method](endpoint.path);

            expect(res.status).toBe(401);
            assertErrorFormat(res.body);
            expect(res.body.error.code).toBe('UNAUTHORIZED');
          }
        ),
        { numRuns: 20 }
      );
    }, 30000);

    test('invalid token returns 401 with standard error format', async () => {
      // Arbitrary: generate random strings as invalid tokens
      const invalidTokenArb = fc.oneof(
        fc.string({ minLength: 1, maxLength: 50 }),
        fc.constant('not.a.jwt'),
        fc.constant('eyJhbGciOiJIUzI1NiJ9.invalid.payload'),
        fc.constant('')
      );

      await fc.assert(
        fc.asyncProperty(invalidTokenArb, async (badToken) => {
          const res = await request(app)
            .get('/films')
            .set('Authorization', `Bearer ${badToken}`);

          expect(res.status).toBe(401);
          assertErrorFormat(res.body);
          expect(res.body.error.code).toBe('UNAUTHORIZED');
        }),
        { numRuns: 20 }
      );
    }, 30000);
  });

  // ── 403 Responses ──

  describe('403 responses conform to error format', () => {
    test('insufficient role returns 403 with standard error format', async () => {
      // Token with ROLE_READ trying to POST (needs ROLE_MANAGE)
      await fc.assert(
        fc.asyncProperty(fc.constant(null), async () => {
          const token = makeToken(['ROLE_READ']);
          const res = await request(app)
            .post('/films')
            .set('Authorization', `Bearer ${token}`)
            .send({ title: 'Test', language_id: 1 });

          expect(res.status).toBe(403);
          assertErrorFormat(res.body);
          expect(res.body.error.code).toBe('FORBIDDEN');
        }),
        { numRuns: 10 }
      );
    }, 30000);
  });

  // ── 400 Responses ──

  describe('400 validation error responses conform to error format', () => {
    test('invalid request body returns 400 with standard error format and details', async () => {
      const token = makeToken(['ROLE_MANAGE']);

      // Arbitrary: generate various invalid film payloads
      const invalidPayloadArb = fc.oneof(
        fc.constant({}),                                    // missing all required fields
        fc.constant({ title: 'X'.repeat(200) }),            // title too long, missing language_id
        fc.constant({ language_id: 1, rating: 'INVALID' }), // missing title, bad rating
        fc.constant({ title: 'T', language_id: 1, rental_rate: -5 }) // negative rental_rate
      );

      await fc.assert(
        fc.asyncProperty(invalidPayloadArb, async (payload) => {
          const res = await request(app)
            .post('/films')
            .set('Authorization', `Bearer ${token}`)
            .send(payload);

          expect(res.status).toBe(400);
          assertErrorFormat(res.body);
          expect(res.body.error.code).toBe('VALIDATION_ERROR');
          expect(res.body.error.details.length).toBeGreaterThan(0);

          // Each detail should have field and message
          for (const detail of res.body.error.details) {
            expect(typeof detail.field).toBe('string');
            expect(typeof detail.message).toBe('string');
          }
        }),
        { numRuns: 20 }
      );
    }, 30000);
  });

  // ── 404 Responses ──

  describe('404 responses conform to error format', () => {
    test('non-existent resource returns 404 with standard error format', async () => {
      const token = makeToken(['ROLE_READ']);

      // Mock db to return undefined (resource not found)
      const mockChain = {
        where: jest.fn().mockReturnThis(),
        first: jest.fn().mockResolvedValue(undefined),
        join: jest.fn().mockReturnThis(),
        select: jest.fn().mockReturnThis(),
      };
      db.mockReturnValue(mockChain);

      await fc.assert(
        fc.asyncProperty(
          fc.integer({ min: 9000, max: 99999 }),
          async (nonExistentId) => {
            mockChain.first.mockResolvedValue(undefined);

            const res = await request(app)
              .get(`/films/${nonExistentId}`)
              .set('Authorization', `Bearer ${token}`);

            expect(res.status).toBe(404);
            assertErrorFormat(res.body);
            expect(res.body.error.code).toBe('RESOURCE_NOT_FOUND');
          }
        ),
        { numRuns: 20 }
      );
    }, 30000);
  });

  // ── 500 Responses ──

  describe('500 responses conform to error format and hide internals', () => {
    test('unexpected error returns 500 with generic message and no stack trace', async () => {
      const token = makeToken(['ROLE_READ']);

      // Arbitrary: generate various internal error messages
      const internalErrorArb = fc.oneof(
        fc.constant('ECONNREFUSED'),
        fc.constant('relation "catalog_schema.film" does not exist'),
        fc.constant('column "secret_column" does not exist'),
        fc.constant('null value in column "film_id" violates not-null constraint')
      );

      await fc.assert(
        fc.asyncProperty(internalErrorArb, async (errorMsg) => {
          // Mock db to throw an unexpected error
          const mockChain = {
            where: jest.fn().mockReturnThis(),
            first: jest.fn().mockRejectedValue(new Error(errorMsg)),
            join: jest.fn().mockReturnThis(),
            select: jest.fn().mockReturnThis(),
          };
          db.mockReturnValue(mockChain);

          const res = await request(app)
            .get('/films/1')
            .set('Authorization', `Bearer ${token}`);

          expect(res.status).toBe(500);
          assertErrorFormat(res.body);
          expect(res.body.error.code).toBe('INTERNAL_ERROR');
          expect(res.body.error.message).toBe('An unexpected error occurred');
          expect(res.body.error.details).toEqual([]);

          // Verify no internal details are exposed
          const responseStr = JSON.stringify(res.body);
          expect(responseStr).not.toContain(errorMsg);
          expect(responseStr).not.toContain('stack');
          expect(responseStr).not.toContain('Error:');
        }),
        { numRuns: 20 }
      );
    }, 30000);
  });
});
