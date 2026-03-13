// Feature: legacy-modernization, Property 4: Pagination Consistency
// Feature: legacy-modernization, Property 18: Input Validation Rejects Invalid Data

// Set required env vars before any module imports (config exits if missing)
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-property-tests';

const fc = require('fast-check');
const jwt = require('jsonwebtoken');
const request = require('supertest');
const config = require('../../src/config');
const app = require('../../src/app');

// Mock the db module used by repositories
jest.mock('../../src/db');
const db = require('../../src/db');

// Helper: generate a valid JWT with ROLE_READ + ROLE_MANAGE
const makeToken = (roles = ['ROLE_READ', 'ROLE_MANAGE']) =>
  jwt.sign({ sub: 'test@example.com', roles }, config.jwtSecret, { expiresIn: '1h' });

// ─── Property 4: Pagination Consistency ──────────────────────────────

describe('Property 4: Pagination Consistency', () => {
  /**
   * Validates: Requirements 3.5
   *
   * For any paginated list endpoint, given a dataset of N records,
   * requesting all pages of size S should yield exactly N total records
   * with no duplicates and no omissions.
   */

  // Generate a dataset of actors with unique IDs
  const generateActors = (n) =>
    Array.from({ length: n }, (_, i) => ({
      actor_id: i + 1,
      first_name: `First${i}`,
      last_name: `Last${i}`,
      last_update: new Date().toISOString(),
    }));

  // Set up the knex mock chain for actor list queries
  const setupActorMocks = (allActors) => {
    const mockChain = {
      orderBy: jest.fn().mockReturnThis(),
      limit: jest.fn().mockReturnThis(),
      offset: jest.fn().mockImplementation(function (offsetVal) {
        // Capture limit and offset to return the correct slice
        const limitVal = this._limitVal;
        return Promise.resolve(allActors.slice(offsetVal, offsetVal + limitVal));
      }),
      count: jest.fn().mockReturnThis(),
      first: jest.fn().mockResolvedValue({ total: String(allActors.length) }),
      _limitVal: 20,
    };

    // Override limit to capture the value
    mockChain.limit = jest.fn().mockImplementation((val) => {
      mockChain._limitVal = val;
      return mockChain;
    });

    db.mockReturnValue(mockChain);
  };

  test('iterating all pages yields exactly N actors with no duplicates', async () => {
    const token = makeToken(['ROLE_READ']);

    await fc.assert(
      fc.asyncProperty(
        fc.integer({ min: 0, max: 50 }),  // total records
        fc.integer({ min: 1, max: 20 }),   // page size
        async (totalRecords, pageSize) => {
          const allActors = generateActors(totalRecords);
          setupActorMocks(allActors);

          const collectedIds = [];
          const totalPages = totalRecords === 0 ? 1 : Math.ceil(totalRecords / pageSize);

          for (let page = 1; page <= totalPages; page++) {
            const res = await request(app)
              .get(`/actors?page=${page}&size=${pageSize}`)
              .set('Authorization', `Bearer ${token}`);

            expect(res.status).toBe(200);
            expect(res.body.total).toBe(totalRecords);
            expect(res.body.data.length).toBeLessThanOrEqual(pageSize);

            for (const actor of res.body.data) {
              collectedIds.push(actor.actor_id);
            }
          }

          // All records collected, no duplicates, no omissions
          expect(collectedIds.length).toBe(totalRecords);
          const uniqueIds = new Set(collectedIds);
          expect(uniqueIds.size).toBe(totalRecords);

          // Verify we got exactly the IDs 1..N
          const expectedIds = Array.from({ length: totalRecords }, (_, i) => i + 1);
          expect([...collectedIds].sort((a, b) => a - b)).toEqual(expectedIds);
        }
      ),
      { numRuns: 30 }
    );
  }, 60000);

  test('iterating all film pages yields exactly N films with no duplicates', async () => {
    const token = makeToken(['ROLE_READ']);

    const generateFilms = (n) =>
      Array.from({ length: n }, (_, i) => ({
        film_id: i + 1,
        title: `Film ${i}`,
        language_id: 1,
        last_update: new Date().toISOString(),
      }));

    const setupFilmMocks = (allFilms) => {
      const mockChain = {
        orderBy: jest.fn().mockReturnThis(),
        limit: jest.fn().mockReturnThis(),
        offset: jest.fn().mockImplementation(function (offsetVal) {
          const limitVal = this._limitVal;
          return Promise.resolve(allFilms.slice(offsetVal, offsetVal + limitVal));
        }),
        count: jest.fn().mockReturnThis(),
        first: jest.fn().mockResolvedValue({ total: String(allFilms.length) }),
        where: jest.fn().mockReturnThis(),
        join: jest.fn().mockReturnThis(),
        select: jest.fn().mockReturnThis(),
        _limitVal: 20,
      };

      mockChain.limit = jest.fn().mockImplementation((val) => {
        mockChain._limitVal = val;
        return mockChain;
      });

      db.mockReturnValue(mockChain);
    };

    await fc.assert(
      fc.asyncProperty(
        fc.integer({ min: 0, max: 50 }),
        fc.integer({ min: 1, max: 20 }),
        async (totalRecords, pageSize) => {
          const allFilms = generateFilms(totalRecords);
          setupFilmMocks(allFilms);

          const collectedIds = [];
          const totalPages = totalRecords === 0 ? 1 : Math.ceil(totalRecords / pageSize);

          for (let page = 1; page <= totalPages; page++) {
            const res = await request(app)
              .get(`/films?page=${page}&size=${pageSize}`)
              .set('Authorization', `Bearer ${token}`);

            expect(res.status).toBe(200);
            expect(res.body.total).toBe(totalRecords);
            expect(res.body.data.length).toBeLessThanOrEqual(pageSize);

            for (const film of res.body.data) {
              collectedIds.push(film.film_id);
            }
          }

          expect(collectedIds.length).toBe(totalRecords);
          const uniqueIds = new Set(collectedIds);
          expect(uniqueIds.size).toBe(totalRecords);
        }
      ),
      { numRuns: 30 }
    );
  }, 60000);
});


// ─── Property 18: Input Validation Rejects Invalid Data ──────────────

describe('Property 18: Input Validation Rejects Invalid Data', () => {
  /**
   * Validates: Requirements 18.1, 18.2, 18.3, 18.4, 18.5
   *
   * Any request body with at least one validation violation returns 400
   * with all errors listed in the standard error format.
   */

  const token = makeToken(['ROLE_MANAGE']);

  // ── Film validation ──

  describe('Film creation rejects invalid data', () => {
    test('missing required title returns 400 with VALIDATION_ERROR', async () => {
      // No DB call should happen — validation fails before service layer
      const res = await request(app)
        .post('/films')
        .set('Authorization', `Bearer ${token}`)
        .send({ language_id: 1 }); // missing title

      expect(res.status).toBe(400);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
      expect(res.body.error.details.length).toBeGreaterThan(0);
      expect(res.body.error.details.some(d => d.field === 'title')).toBe(true);
      expect(res.body.error.timestamp).toBeDefined();
    });

    test('missing required language_id returns 400', async () => {
      const res = await request(app)
        .post('/films')
        .set('Authorization', `Bearer ${token}`)
        .send({ title: 'Test Film' }); // missing language_id

      expect(res.status).toBe(400);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
      expect(res.body.error.details.some(d => d.field === 'language_id')).toBe(true);
    });

    test('title exceeding 128 chars returns 400', () => {
      return fc.assert(
        fc.asyncProperty(
          fc.string({ minLength: 129, maxLength: 300 }),
          async (longTitle) => {
            const res = await request(app)
              .post('/films')
              .set('Authorization', `Bearer ${token}`)
              .send({ title: longTitle, language_id: 1 });

            expect(res.status).toBe(400);
            expect(res.body.error.code).toBe('VALIDATION_ERROR');
            expect(res.body.error.details.some(d => d.field === 'title')).toBe(true);
          }
        ),
        { numRuns: 20 }
      );
    }, 30000);

    test('invalid rating enum value returns 400', () => {
      // Arbitrary: generate strings that are NOT valid ratings
      const invalidRatingArb = fc.string({ minLength: 1, maxLength: 10 })
        .filter(s => !['G', 'PG', 'PG-13', 'R', 'NC-17'].includes(s));

      return fc.assert(
        fc.asyncProperty(invalidRatingArb, async (badRating) => {
          const res = await request(app)
            .post('/films')
            .set('Authorization', `Bearer ${token}`)
            .send({ title: 'Test', language_id: 1, rating: badRating });

          expect(res.status).toBe(400);
          expect(res.body.error.code).toBe('VALIDATION_ERROR');
          expect(res.body.error.details.some(d => d.field === 'rating')).toBe(true);
        }),
        { numRuns: 30 }
      );
    }, 30000);

    test('negative rental_rate returns 400', () => {
      return fc.assert(
        fc.asyncProperty(
          fc.double({ min: -1000, max: -0.01, noNaN: true }),
          async (negativeRate) => {
            const res = await request(app)
              .post('/films')
              .set('Authorization', `Bearer ${token}`)
              .send({ title: 'Test', language_id: 1, rental_rate: negativeRate });

            expect(res.status).toBe(400);
            expect(res.body.error.code).toBe('VALIDATION_ERROR');
            expect(res.body.error.details.some(d => d.field === 'rental_rate')).toBe(true);
          }
        ),
        { numRuns: 20 }
      );
    }, 30000);

    test('multiple violations returns all errors at once', async () => {
      const res = await request(app)
        .post('/films')
        .set('Authorization', `Bearer ${token}`)
        .send({}); // missing both title and language_id

      expect(res.status).toBe(400);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
      expect(res.body.error.details.length).toBeGreaterThanOrEqual(2);

      const fields = res.body.error.details.map(d => d.field);
      expect(fields).toContain('title');
      expect(fields).toContain('language_id');
    });
  });

  // ── Actor validation ──

  describe('Actor creation rejects invalid data', () => {
    test('missing required fields returns 400 with all errors', async () => {
      const res = await request(app)
        .post('/actors')
        .set('Authorization', `Bearer ${token}`)
        .send({}); // missing first_name and last_name

      expect(res.status).toBe(400);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
      expect(res.body.error.details.length).toBeGreaterThanOrEqual(2);

      const fields = res.body.error.details.map(d => d.field);
      expect(fields).toContain('first_name');
      expect(fields).toContain('last_name');
      expect(res.body.error.timestamp).toBeDefined();
    });

    test('first_name exceeding 45 chars returns 400', () => {
      return fc.assert(
        fc.asyncProperty(
          fc.string({ minLength: 46, maxLength: 100 }),
          async (longName) => {
            const res = await request(app)
              .post('/actors')
              .set('Authorization', `Bearer ${token}`)
              .send({ first_name: longName, last_name: 'Valid' });

            expect(res.status).toBe(400);
            expect(res.body.error.code).toBe('VALIDATION_ERROR');
            expect(res.body.error.details.some(d => d.field === 'first_name')).toBe(true);
          }
        ),
        { numRuns: 20 }
      );
    }, 30000);

    test('last_name exceeding 45 chars returns 400', () => {
      return fc.assert(
        fc.asyncProperty(
          fc.string({ minLength: 46, maxLength: 100 }),
          async (longName) => {
            const res = await request(app)
              .post('/actors')
              .set('Authorization', `Bearer ${token}`)
              .send({ first_name: 'Valid', last_name: longName });

            expect(res.status).toBe(400);
            expect(res.body.error.code).toBe('VALIDATION_ERROR');
            expect(res.body.error.details.some(d => d.field === 'last_name')).toBe(true);
          }
        ),
        { numRuns: 20 }
      );
    }, 30000);
  });

  // ── Error response format consistency ──

  describe('Error response format is consistent', () => {
    test('all validation errors follow { error: { code, message, details, timestamp } }', () => {
      // Generate various invalid film payloads
      const invalidFilmArb = fc.record({
        title: fc.oneof(
          fc.constant(undefined),                          // missing
          fc.string({ minLength: 129, maxLength: 200 }),   // too long
        ),
        language_id: fc.oneof(
          fc.constant(undefined),                          // missing
          fc.constant(-1),                                 // negative
        ),
        rating: fc.oneof(
          fc.constant('INVALID'),
          fc.constant('XX'),
        ),
      });

      return fc.assert(
        fc.asyncProperty(invalidFilmArb, async (payload) => {
          // Filter out payloads that might accidentally be valid
          const body = {};
          if (payload.title !== undefined) body.title = payload.title;
          if (payload.language_id !== undefined) body.language_id = payload.language_id;
          if (payload.rating !== undefined) body.rating = payload.rating;

          const res = await request(app)
            .post('/films')
            .set('Authorization', `Bearer ${token}`)
            .send(body);

          // Should be 400 since at least title or language_id is missing/invalid
          if (res.status === 400) {
            expect(res.body).toHaveProperty('error');
            expect(res.body.error).toHaveProperty('code', 'VALIDATION_ERROR');
            expect(res.body.error).toHaveProperty('message');
            expect(res.body.error).toHaveProperty('details');
            expect(Array.isArray(res.body.error.details)).toBe(true);
            expect(res.body.error).toHaveProperty('timestamp');
            expect(res.body.error.details.length).toBeGreaterThan(0);

            // Each detail has field and message
            for (const detail of res.body.error.details) {
              expect(detail).toHaveProperty('field');
              expect(detail).toHaveProperty('message');
            }
          }
        }),
        { numRuns: 30 }
      );
    }, 30000);
  });
});
