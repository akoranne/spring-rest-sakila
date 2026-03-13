// Unit tests for Catalog Service
// Validates: Requirements 13.1, 13.2, 13.4

process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';

jest.mock('../../src/repositories/filmRepository');
jest.mock('../../src/repositories/actorRepository');

const filmRepository = require('../../src/repositories/filmRepository');
const actorRepository = require('../../src/repositories/actorRepository');

const filmService = require('../../src/services/filmService');
const actorService = require('../../src/services/actorService');
const { createFilmSchema, updateFilmSchema } = require('../../src/validators/filmValidator');
const { createActorSchema, updateActorSchema, searchActorSchema } = require('../../src/validators/actorValidator');

afterEach(() => {
  jest.restoreAllMocks();
});

// ─── filmService.findAll ─────────────────────────────────────────────

describe('filmService.findAll', () => {
  test('returns paginated films with total count', async () => {
    const mockFilms = [
      { film_id: 1, title: 'Film A', rating: 'PG' },
      { film_id: 2, title: 'Film B', rating: 'R' },
    ];
    filmRepository.findAll.mockResolvedValue(mockFilms);
    filmRepository.count.mockResolvedValue({ total: '25' });

    const result = await filmService.findAll({ page: 1, size: 2, sort: 'film_id' });

    expect(result).toEqual({ data: mockFilms, total: 25, page: 1, size: 2 });
    expect(filmRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'film_id' });
    expect(filmRepository.count).toHaveBeenCalled();
  });

  test('passes filter parameters to repository', async () => {
    filmRepository.findAll.mockResolvedValue([]);
    filmRepository.count.mockResolvedValue({ total: '0' });

    await filmService.findAll({ page: 1, size: 10, sort: 'title', category: 'Action', release_year: 2006, rating: 'PG-13' });

    expect(filmRepository.findAll).toHaveBeenCalledWith({
      page: 1, size: 10, sort: 'title', category: 'Action', release_year: 2006, rating: 'PG-13',
    });
    expect(filmRepository.count).toHaveBeenCalledWith({
      category: 'Action', release_year: 2006, rating: 'PG-13',
    });
  });
});

// ─── filmService.findById ────────────────────────────────────────────

describe('filmService.findById', () => {
  test('returns film when found', async () => {
    const mockFilm = { film_id: 1, title: 'Film A' };
    filmRepository.findById.mockResolvedValue(mockFilm);

    const result = await filmService.findById(1);
    expect(result).toEqual(mockFilm);
  });

  test('throws 404 when film not found', async () => {
    filmRepository.findById.mockResolvedValue(null);

    await expect(filmService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── filmService.findByIdWithDetails ─────────────────────────────────

describe('filmService.findByIdWithDetails', () => {
  test('returns film with details when found', async () => {
    const mockFilm = { film_id: 1, title: 'Film A', language: 'English' };
    filmRepository.findByIdWithDetails.mockResolvedValue(mockFilm);

    const result = await filmService.findByIdWithDetails(1);
    expect(result).toEqual(mockFilm);
  });

  test('throws 404 when film not found', async () => {
    filmRepository.findByIdWithDetails.mockResolvedValue(null);

    await expect(filmService.findByIdWithDetails(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── filmService.findActorsByFilmId ──────────────────────────────────

describe('filmService.findActorsByFilmId', () => {
  test('returns actors for an existing film', async () => {
    const mockActors = [{ actor_id: 1, first_name: 'John', last_name: 'Doe' }];
    filmRepository.findById.mockResolvedValue({ film_id: 1 });
    filmRepository.findActorsByFilmId.mockResolvedValue(mockActors);

    const result = await filmService.findActorsByFilmId(1);
    expect(result).toEqual(mockActors);
  });

  test('throws 404 when film does not exist', async () => {
    filmRepository.findById.mockResolvedValue(null);

    await expect(filmService.findActorsByFilmId(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── filmService.findActorByFilmIdAndActorId ─────────────────────────

describe('filmService.findActorByFilmIdAndActorId', () => {
  test('returns actor when found for film', async () => {
    const mockActor = { actor_id: 1, first_name: 'John', last_name: 'Doe' };
    filmRepository.findById.mockResolvedValue({ film_id: 1 });
    filmRepository.findActorByFilmIdAndActorId.mockResolvedValue(mockActor);

    const result = await filmService.findActorByFilmIdAndActorId(1, 1);
    expect(result).toEqual(mockActor);
  });

  test('throws 404 when film does not exist', async () => {
    filmRepository.findById.mockResolvedValue(null);

    await expect(filmService.findActorByFilmIdAndActorId(999, 1))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });

  test('throws 404 when actor not associated with film', async () => {
    filmRepository.findById.mockResolvedValue({ film_id: 1 });
    filmRepository.findActorByFilmIdAndActorId.mockResolvedValue(null);

    await expect(filmService.findActorByFilmIdAndActorId(1, 999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── filmService.create ──────────────────────────────────────────────

describe('filmService.create', () => {
  test('returns the created film', async () => {
    const input = { title: 'New Film', language_id: 1, rental_rate: 4.99 };
    const created = { film_id: 10, ...input };
    filmRepository.create.mockResolvedValue([created]);

    const result = await filmService.create(input);
    expect(result).toEqual(created);
    expect(filmRepository.create).toHaveBeenCalledWith(input);
  });
});

// ─── filmService.update ──────────────────────────────────────────────

describe('filmService.update', () => {
  test('returns the updated film', async () => {
    const existing = { film_id: 1, title: 'Old Title' };
    const updateData = { title: 'New Title' };
    const updated = { ...existing, ...updateData };

    filmRepository.findById.mockResolvedValue(existing);
    filmRepository.update.mockResolvedValue([updated]);

    const result = await filmService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(filmRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when film does not exist', async () => {
    filmRepository.findById.mockResolvedValue(null);

    await expect(filmService.update(999, { title: 'X' }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── filmService.remove ──────────────────────────────────────────────

describe('filmService.remove', () => {
  test('removes the film successfully', async () => {
    filmRepository.findById.mockResolvedValue({ film_id: 1 });
    filmRepository.remove.mockResolvedValue(1);

    await expect(filmService.remove(1)).resolves.toBeUndefined();
    expect(filmRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when film does not exist', async () => {
    filmRepository.findById.mockResolvedValue(null);

    await expect(filmService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── filmService.fullTextSearch ──────────────────────────────────────

describe('filmService.fullTextSearch', () => {
  test('returns paginated search results', async () => {
    const mockResults = [{ film_id: 1, title: 'Dinosaur' }];
    filmRepository.fullTextSearch.mockResolvedValue(mockResults);
    filmRepository.fullTextSearchCount.mockResolvedValue({ total: '1' });

    const result = await filmService.fullTextSearch('dinosaur', { page: 1, size: 10 });

    expect(result).toEqual({ data: mockResults, total: 1, page: 1, size: 10 });
    expect(filmRepository.fullTextSearch).toHaveBeenCalledWith('dinosaur', { page: 1, size: 10 });
    expect(filmRepository.fullTextSearchCount).toHaveBeenCalledWith('dinosaur');
  });
});


// ─── actorService.findAll ────────────────────────────────────────────

describe('actorService.findAll', () => {
  test('returns paginated actors with total count', async () => {
    const mockActors = [
      { actor_id: 1, first_name: 'John', last_name: 'Doe' },
      { actor_id: 2, first_name: 'Jane', last_name: 'Smith' },
    ];
    actorRepository.findAll.mockResolvedValue(mockActors);
    actorRepository.count.mockResolvedValue({ total: '100' });

    const result = await actorService.findAll({ page: 1, size: 2, sort: 'actor_id' });

    expect(result).toEqual({ data: mockActors, total: 100, page: 1, size: 2 });
    expect(actorRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'actor_id' });
    expect(actorRepository.count).toHaveBeenCalled();
  });
});

// ─── actorService.findById ───────────────────────────────────────────

describe('actorService.findById', () => {
  test('returns actor when found', async () => {
    const mockActor = { actor_id: 1, first_name: 'John', last_name: 'Doe' };
    actorRepository.findById.mockResolvedValue(mockActor);

    const result = await actorService.findById(1);
    expect(result).toEqual(mockActor);
  });

  test('throws 404 when actor not found', async () => {
    actorRepository.findById.mockResolvedValue(null);

    await expect(actorService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── actorService.findByIdWithDetails ────────────────────────────────

describe('actorService.findByIdWithDetails', () => {
  test('returns actor with details when found', async () => {
    const mockActor = { actor_id: 1, first_name: 'John', last_name: 'Doe' };
    actorRepository.findByIdWithDetails.mockResolvedValue(mockActor);

    const result = await actorService.findByIdWithDetails(1);
    expect(result).toEqual(mockActor);
  });

  test('throws 404 when actor not found', async () => {
    actorRepository.findByIdWithDetails.mockResolvedValue(null);

    await expect(actorService.findByIdWithDetails(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── actorService.findFilmsByActorId ─────────────────────────────────

describe('actorService.findFilmsByActorId', () => {
  test('returns films for an existing actor', async () => {
    const mockFilms = [{ film_id: 1, title: 'Film A' }];
    actorRepository.findById.mockResolvedValue({ actor_id: 1 });
    actorRepository.findFilmsByActorId.mockResolvedValue(mockFilms);

    const result = await actorService.findFilmsByActorId(1);
    expect(result).toEqual(mockFilms);
  });

  test('throws 404 when actor does not exist', async () => {
    actorRepository.findById.mockResolvedValue(null);

    await expect(actorService.findFilmsByActorId(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── actorService.addFilmToActor ─────────────────────────────────────

describe('actorService.addFilmToActor', () => {
  test('returns the association record', async () => {
    const assoc = { actor_id: 1, film_id: 5 };
    actorRepository.findById.mockResolvedValue({ actor_id: 1 });
    actorRepository.addFilmToActor.mockResolvedValue([assoc]);

    const result = await actorService.addFilmToActor(1, 5);
    expect(result).toEqual(assoc);
  });

  test('throws 404 when actor does not exist', async () => {
    actorRepository.findById.mockResolvedValue(null);

    await expect(actorService.addFilmToActor(999, 5))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── actorService.removeFilmFromActor ────────────────────────────────

describe('actorService.removeFilmFromActor', () => {
  test('removes the association successfully', async () => {
    actorRepository.findById.mockResolvedValue({ actor_id: 1 });
    actorRepository.removeFilmFromActor.mockResolvedValue(1);

    await expect(actorService.removeFilmFromActor(1, 5)).resolves.toBeUndefined();
  });

  test('throws 404 when actor does not exist', async () => {
    actorRepository.findById.mockResolvedValue(null);

    await expect(actorService.removeFilmFromActor(999, 5))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });

  test('throws 404 when film not associated with actor', async () => {
    actorRepository.findById.mockResolvedValue({ actor_id: 1 });
    actorRepository.removeFilmFromActor.mockResolvedValue(0);

    await expect(actorService.removeFilmFromActor(1, 999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── actorService.search ─────────────────────────────────────────────

describe('actorService.search', () => {
  test('returns matching actors', async () => {
    const mockActors = [{ actor_id: 1, first_name: 'John', last_name: 'Doe' }];
    actorRepository.search.mockResolvedValue(mockActors);

    const result = await actorService.search('john');
    expect(result).toEqual(mockActors);
    expect(actorRepository.search).toHaveBeenCalledWith('john');
  });

  test('returns empty array when no matches', async () => {
    actorRepository.search.mockResolvedValue([]);

    const result = await actorService.search('zzzzz');
    expect(result).toEqual([]);
  });
});

// ─── actorService.create ─────────────────────────────────────────────

describe('actorService.create', () => {
  test('returns the created actor', async () => {
    const input = { first_name: 'New', last_name: 'Actor' };
    const created = { actor_id: 10, ...input };
    actorRepository.create.mockResolvedValue([created]);

    const result = await actorService.create(input);
    expect(result).toEqual(created);
    expect(actorRepository.create).toHaveBeenCalledWith(input);
  });
});

// ─── actorService.update ─────────────────────────────────────────────

describe('actorService.update', () => {
  test('returns the updated actor', async () => {
    const existing = { actor_id: 1, first_name: 'John', last_name: 'Doe' };
    const updateData = { first_name: 'Jonathan' };
    const updated = { ...existing, ...updateData };

    actorRepository.findById.mockResolvedValue(existing);
    actorRepository.update.mockResolvedValue([updated]);

    const result = await actorService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(actorRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when actor does not exist', async () => {
    actorRepository.findById.mockResolvedValue(null);

    await expect(actorService.update(999, { first_name: 'X' }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── actorService.remove ─────────────────────────────────────────────

describe('actorService.remove', () => {
  test('removes the actor successfully', async () => {
    actorRepository.findById.mockResolvedValue({ actor_id: 1 });
    actorRepository.remove.mockResolvedValue(1);

    await expect(actorService.remove(1)).resolves.toBeUndefined();
    expect(actorRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when actor does not exist', async () => {
    actorRepository.findById.mockResolvedValue(null);

    await expect(actorService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── Film Joi Validation ─────────────────────────────────────────────

describe('Film Joi Validation', () => {
  describe('createFilmSchema', () => {
    test('accepts valid input with all fields', () => {
      const valid = {
        title: 'Test Film',
        description: 'A test film',
        release_year: 2024,
        language_id: 1,
        rental_duration: 5,
        rental_rate: 4.99,
        length: 120,
        replacement_cost: 19.99,
        rating: 'PG',
        special_features: ['Trailers', 'Commentaries'],
      };
      const { error } = createFilmSchema.validate(valid);
      expect(error).toBeUndefined();
    });

    test('accepts minimal valid input (only required fields)', () => {
      const { error } = createFilmSchema.validate({ title: 'Minimal', language_id: 1 });
      expect(error).toBeUndefined();
    });

    test('rejects missing title', () => {
      const { error } = createFilmSchema.validate({ language_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('title');
    });

    test('rejects missing language_id', () => {
      const { error } = createFilmSchema.validate({ title: 'Film' });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('language_id');
    });

    test('rejects title exceeding 128 characters', () => {
      const { error } = createFilmSchema.validate({ title: 'a'.repeat(129), language_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('title');
    });

    test('rejects negative rental_rate', () => {
      const { error } = createFilmSchema.validate({ title: 'Film', language_id: 1, rental_rate: -1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('rental_rate');
    });

    test('rejects invalid rating value', () => {
      const { error } = createFilmSchema.validate({ title: 'Film', language_id: 1, rating: 'X' });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('rating');
    });

    test('accepts all valid rating values', () => {
      for (const rating of ['G', 'PG', 'PG-13', 'R', 'NC-17']) {
        const { error } = createFilmSchema.validate({ title: 'Film', language_id: 1, rating });
        expect(error).toBeUndefined();
      }
    });

    test('rejects release_year below 1900', () => {
      const { error } = createFilmSchema.validate({ title: 'Film', language_id: 1, release_year: 1800 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('release_year');
    });

    test('rejects release_year above 2100', () => {
      const { error } = createFilmSchema.validate({ title: 'Film', language_id: 1, release_year: 2200 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('release_year');
    });

    test('rejects non-positive language_id', () => {
      const { error } = createFilmSchema.validate({ title: 'Film', language_id: 0 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('language_id');
    });
  });

  describe('updateFilmSchema', () => {
    test('requires at least one field', () => {
      const { error } = updateFilmSchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].type).toBe('object.min');
    });

    test('accepts a single valid field', () => {
      const { error } = updateFilmSchema.validate({ title: 'Updated Title' });
      expect(error).toBeUndefined();
    });

    test('rejects title exceeding 128 characters', () => {
      const { error } = updateFilmSchema.validate({ title: 'a'.repeat(129) });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('title');
    });

    test('rejects invalid rating on update', () => {
      const { error } = updateFilmSchema.validate({ rating: 'INVALID' });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('rating');
    });
  });
});

// ─── Actor Joi Validation ────────────────────────────────────────────

describe('Actor Joi Validation', () => {
  describe('createActorSchema', () => {
    test('accepts valid input', () => {
      const { error } = createActorSchema.validate({ first_name: 'John', last_name: 'Doe' });
      expect(error).toBeUndefined();
    });

    test('rejects missing required fields', () => {
      const { error } = createActorSchema.validate({}, { abortEarly: false });
      expect(error).toBeDefined();
      const fields = error.details.map(d => d.context.key);
      expect(fields).toContain('first_name');
      expect(fields).toContain('last_name');
    });

    test('rejects first_name exceeding 45 characters', () => {
      const { error } = createActorSchema.validate({ first_name: 'a'.repeat(46), last_name: 'Doe' });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('first_name');
    });

    test('rejects last_name exceeding 45 characters', () => {
      const { error } = createActorSchema.validate({ first_name: 'John', last_name: 'a'.repeat(46) });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('last_name');
    });
  });

  describe('updateActorSchema', () => {
    test('requires at least one field', () => {
      const { error } = updateActorSchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].type).toBe('object.min');
    });

    test('accepts a single valid field', () => {
      const { error } = updateActorSchema.validate({ first_name: 'Updated' });
      expect(error).toBeUndefined();
    });

    test('rejects first_name exceeding 45 characters', () => {
      const { error } = updateActorSchema.validate({ first_name: 'a'.repeat(46) });
      expect(error).toBeDefined();
    });
  });

  describe('searchActorSchema', () => {
    test('accepts valid query', () => {
      const { error } = searchActorSchema.validate({ query: 'john' });
      expect(error).toBeUndefined();
    });

    test('rejects missing query', () => {
      const { error } = searchActorSchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('query');
    });

    test('rejects query exceeding 100 characters', () => {
      const { error } = searchActorSchema.validate({ query: 'a'.repeat(101) });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('query');
    });
  });
});
