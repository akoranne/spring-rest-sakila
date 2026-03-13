// Unit tests for Auth Service
// Validates: Requirements 6.1, 6.2, 6.3, 6.4, 6.5, 13.1, 13.4, 13.6

// Set required env vars before any module imports (config exits if missing)
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';

const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const config = require('../../src/config');

// Mock the repository
jest.mock('../../src/repositories/authorityRepository');
const authorityRepository = require('../../src/repositories/authorityRepository');

const authService = require('../../src/services/authService');
const { jwtAuth, requireRole } = require('../../src/middleware/auth');

// Helper to create mock Express res object
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

// ─── authService.login ───────────────────────────────────────────────

describe('authService.login', () => {
  const email = 'user@example.com';
  const password = 'correct-password';
  let hashedPassword;

  beforeAll(async () => {
    hashedPassword = await bcrypt.hash(password, 4);
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('returns a valid JWT on successful login', async () => {
    authorityRepository.findByEmail.mockResolvedValue({
      email,
      password: hashedPassword,
      authority: ['ROLE_READ', 'ROLE_MANAGE'],
    });

    const token = await authService.login(email, password);

    expect(typeof token).toBe('string');
    const decoded = jwt.verify(token, config.jwtSecret);
    expect(decoded.sub).toBe(email);
    expect(decoded.roles).toEqual(['ROLE_READ', 'ROLE_MANAGE']);
    expect(decoded.exp).toBeDefined();
    expect(decoded.iat).toBeDefined();
  });

  test('throws 401 UNAUTHORIZED for wrong password', async () => {
    authorityRepository.findByEmail.mockResolvedValue({
      email,
      password: hashedPassword,
      authority: ['ROLE_READ'],
    });

    await expect(authService.login(email, 'wrong-password'))
      .rejects
      .toMatchObject({ statusCode: 401, code: 'UNAUTHORIZED' });
  });

  test('throws 401 UNAUTHORIZED for non-existent email', async () => {
    authorityRepository.findByEmail.mockResolvedValue(null);

    await expect(authService.login('nobody@example.com', password))
      .rejects
      .toMatchObject({ statusCode: 401, code: 'UNAUTHORIZED' });
  });
});

// ─── jwtAuth middleware ──────────────────────────────────────────────

describe('jwtAuth middleware', () => {
  test('populates req.user and calls next() for a valid token', () => {
    const token = jwt.sign(
      { sub: 'user@example.com', roles: ['ROLE_READ'] },
      config.jwtSecret,
      { expiresIn: '1h' }
    );
    const req = { headers: { authorization: `Bearer ${token}` } };
    const res = createMockRes();
    const next = jest.fn();

    jwtAuth(req, res, next);

    expect(next).toHaveBeenCalled();
    expect(req.user).toBeDefined();
    expect(req.user.sub).toBe('user@example.com');
    expect(req.user.roles).toEqual(['ROLE_READ']);
  });

  test('returns 401 for an expired token', () => {
    const token = jwt.sign(
      { sub: 'user@example.com', roles: ['ROLE_READ'] },
      config.jwtSecret,
      { expiresIn: '0s' }
    );
    const req = { headers: { authorization: `Bearer ${token}` } };
    const res = createMockRes();
    const next = jest.fn();

    // Small delay to ensure token is expired
    jwtAuth(req, res, next);

    expect(res.statusCode).toBe(401);
    expect(res.body.error.code).toBe('UNAUTHORIZED');
    expect(next).not.toHaveBeenCalled();
  });

  test('returns 401 when Authorization header is missing', () => {
    const req = { headers: {} };
    const res = createMockRes();
    const next = jest.fn();

    jwtAuth(req, res, next);

    expect(res.statusCode).toBe(401);
    expect(res.body.error.code).toBe('UNAUTHORIZED');
    expect(next).not.toHaveBeenCalled();
  });

  test('returns 401 for a token signed with a different secret', () => {
    const token = jwt.sign(
      { sub: 'user@example.com', roles: ['ROLE_READ'] },
      'wrong-secret',
      { expiresIn: '1h' }
    );
    const req = { headers: { authorization: `Bearer ${token}` } };
    const res = createMockRes();
    const next = jest.fn();

    jwtAuth(req, res, next);

    expect(res.statusCode).toBe(401);
    expect(res.body.error.code).toBe('UNAUTHORIZED');
    expect(next).not.toHaveBeenCalled();
  });
});

// ─── requireRole middleware ──────────────────────────────────────────

describe('requireRole middleware', () => {
  test('calls next() when user has the required role', () => {
    const req = { user: { roles: ['ROLE_MANAGE'] } };
    const res = createMockRes();
    const next = jest.fn();

    requireRole('ROLE_MANAGE')(req, res, next);

    expect(next).toHaveBeenCalled();
  });

  test('returns 403 when user lacks the required role', () => {
    const req = { user: { roles: ['ROLE_READ'] } };
    const res = createMockRes();
    const next = jest.fn();

    requireRole('ROLE_ADMIN')(req, res, next);

    expect(res.statusCode).toBe(403);
    expect(res.body.error.code).toBe('FORBIDDEN');
    expect(res.body.error.message).toBe('Insufficient permissions');
    expect(next).not.toHaveBeenCalled();
  });

  test('returns 403 when req.user is not set', () => {
    const req = {};
    const res = createMockRes();
    const next = jest.fn();

    requireRole('ROLE_MANAGE')(req, res, next);

    expect(res.statusCode).toBe(403);
    expect(res.body.error.code).toBe('FORBIDDEN');
    expect(next).not.toHaveBeenCalled();
  });
});
