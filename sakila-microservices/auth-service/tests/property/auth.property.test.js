// Feature: legacy-modernization, Property 1: JWT Authentication Round Trip
// Feature: legacy-modernization, Property 2: JWT Validation Rejects Invalid Tokens

// Set required env vars before any module imports (config exits if missing)
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-property-tests';

const fc = require('fast-check');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const config = require('../../src/config');

// Mock the repository — we don't want real DB calls
jest.mock('../../src/repositories/authorityRepository');
const authorityRepository = require('../../src/repositories/authorityRepository');

const authService = require('../../src/services/authService');
const { jwtAuth } = require('../../src/middleware/auth');

const VALID_ROLES = ['ROLE_READ', 'ROLE_WRITE', 'ROLE_MANAGE', 'ROLE_ADMIN'];

// Arbitrary: generate a valid-ish email
const emailArb = fc.tuple(
  fc.stringOf(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz0123456789'), { minLength: 1, maxLength: 10 }),
  fc.stringOf(fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz'), { minLength: 1, maxLength: 6 })
).map(([local, domain]) => `${local}@${domain}.com`);

// Arbitrary: generate a non-empty subset of roles
const rolesArb = fc.subarray(VALID_ROLES, { minLength: 1 });

describe('Property 1: JWT Authentication Round Trip', () => {
  /**
   * Validates: Requirements 2.3, 6.1, 6.2
   *
   * For any valid authority record (email, roles), authenticating via
   * authService.login and then decoding the JWT should yield matching
   * email (sub) and roles.
   */
  let hashedPassword;
  const password = 'test-password';

  beforeAll(async () => {
    // Hash once with low cost to avoid per-iteration overhead
    hashedPassword = await bcrypt.hash(password, 4);
  });

  test('authenticating and decoding JWT yields matching email and roles', async () => {
    // Mock bcrypt.compare to avoid re-hashing — we trust bcrypt works,
    // we're testing the JWT round-trip logic
    jest.spyOn(bcrypt, 'compare').mockResolvedValue(true);

    await fc.assert(
      fc.asyncProperty(emailArb, rolesArb, async (email, roles) => {
        authorityRepository.findByEmail.mockResolvedValue({
          email,
          password: hashedPassword,
          authority: roles,
        });

        const token = await authService.login(email, password);

        const decoded = jwt.verify(token, config.jwtSecret);

        expect(decoded.sub).toBe(email);
        expect(decoded.roles).toEqual(roles);
        expect(decoded.exp).toBeDefined();
        expect(decoded.iat).toBeDefined();
      }),
      { numRuns: 100 }
    );

    bcrypt.compare.mockRestore();
  }, 30000);
});


describe('Property 2: JWT Validation Rejects Invalid Tokens', () => {
  /**
   * Validates: Requirements 6.3, 6.4
   *
   * For any random string that is not a valid JWT, the jwtAuth middleware
   * should reject it with 401.
   */

  const createMockRes = () => {
    const res = {
      statusCode: null,
      body: null,
      status(code) {
        res.statusCode = code;
        return res;
      },
      json(data) {
        res.body = data;
        return res;
      },
    };
    return res;
  };

  test('rejects any random string as Bearer token with 401', () => {
    fc.assert(
      fc.property(fc.string({ minLength: 0, maxLength: 200 }), (randomString) => {
        const req = {
          headers: { authorization: `Bearer ${randomString}` },
        };
        const res = createMockRes();
        const next = jest.fn();

        jwtAuth(req, res, next);

        expect(res.statusCode).toBe(401);
        expect(res.body.error.code).toBe('UNAUTHORIZED');
        expect(next).not.toHaveBeenCalled();
      }),
      { numRuns: 100 }
    );
  });

  test('rejects request with no Authorization header with 401', () => {
    const req = { headers: {} };
    const res = createMockRes();
    const next = jest.fn();

    jwtAuth(req, res, next);

    expect(res.statusCode).toBe(401);
    expect(res.body.error.code).toBe('UNAUTHORIZED');
    expect(next).not.toHaveBeenCalled();
  });

  test('rejects request with non-Bearer prefix with 401', () => {
    fc.assert(
      fc.property(fc.string({ minLength: 1, maxLength: 100 }), (randomString) => {
        const req = {
          headers: { authorization: `Basic ${randomString}` },
        };
        const res = createMockRes();
        const next = jest.fn();

        jwtAuth(req, res, next);

        expect(res.statusCode).toBe(401);
        expect(res.body.error.code).toBe('UNAUTHORIZED');
        expect(next).not.toHaveBeenCalled();
      }),
      { numRuns: 100 }
    );
  });

  test('rejects empty string as Bearer token with 401', () => {
    const req = {
      headers: { authorization: 'Bearer ' },
    };
    const res = createMockRes();
    const next = jest.fn();

    jwtAuth(req, res, next);

    expect(res.statusCode).toBe(401);
    expect(res.body.error.code).toBe('UNAUTHORIZED');
    expect(next).not.toHaveBeenCalled();
  });
});
