// Unit tests for Location Service
// Validates: Requirements 13.1, 13.2, 13.4

// Set required env vars before any module imports (config exits if missing)
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';

// Mock the repositories
jest.mock('../../src/repositories/addressRepository');
jest.mock('../../src/repositories/cityRepository');

const addressRepository = require('../../src/repositories/addressRepository');
const cityRepository = require('../../src/repositories/cityRepository');

const addressService = require('../../src/services/addressService');
const cityService = require('../../src/services/cityService');
const { createAddressSchema, updateAddressSchema } = require('../../src/validators/addressValidator');
const { createCitySchema, updateCitySchema } = require('../../src/validators/cityValidator');

afterEach(() => {
  jest.restoreAllMocks();
});

// ─── addressService.findAll ──────────────────────────────────────────

describe('addressService.findAll', () => {
  test('returns paginated addresses with total count', async () => {
    const mockAddresses = [
      { address_id: 1, address: '123 Main St', district: 'Central', city_id: 1 },
      { address_id: 2, address: '456 Oak Ave', district: 'West', city_id: 2 },
    ];
    addressRepository.findAll.mockResolvedValue(mockAddresses);
    addressRepository.count.mockResolvedValue({ total: '10' });

    const result = await addressService.findAll({ page: 1, size: 2, sort: 'address_id' });

    expect(result).toEqual({ data: mockAddresses, total: 10, page: 1, size: 2 });
    expect(addressRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'address_id' });
    expect(addressRepository.count).toHaveBeenCalled();
  });
});

// ─── addressService.findById ─────────────────────────────────────────

describe('addressService.findById', () => {
  test('returns address when found', async () => {
    const mockAddress = { address_id: 1, address: '123 Main St', district: 'Central', city_id: 1 };
    addressRepository.findById.mockResolvedValue(mockAddress);

    const result = await addressService.findById(1);

    expect(result).toEqual(mockAddress);
    expect(addressRepository.findById).toHaveBeenCalledWith(1);
  });

  test('throws 404 when address not found', async () => {
    addressRepository.findById.mockResolvedValue(null);

    await expect(addressService.findById(999))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── addressService.findByIdWithDetails ──────────────────────────────

describe('addressService.findByIdWithDetails', () => {
  test('returns address with city and country details when found', async () => {
    const mockAddress = {
      address_id: 1, address: '123 Main St', district: 'Central',
      city_id: 1, city: 'London', country: 'United Kingdom',
    };
    addressRepository.findByIdWithDetails.mockResolvedValue(mockAddress);

    const result = await addressService.findByIdWithDetails(1);

    expect(result).toEqual(mockAddress);
    expect(addressRepository.findByIdWithDetails).toHaveBeenCalledWith(1);
  });

  test('throws 404 when address not found', async () => {
    addressRepository.findByIdWithDetails.mockResolvedValue(null);

    await expect(addressService.findByIdWithDetails(999))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── addressService.create ───────────────────────────────────────────

describe('addressService.create', () => {
  test('returns the created address', async () => {
    const input = { address: '789 Elm St', district: 'East', city_id: 3 };
    const created = { address_id: 5, ...input };
    addressRepository.create.mockResolvedValue([created]);

    const result = await addressService.create(input);

    expect(result).toEqual(created);
    expect(addressRepository.create).toHaveBeenCalledWith(input);
  });
});

// ─── addressService.update ───────────────────────────────────────────

describe('addressService.update', () => {
  test('returns the updated address', async () => {
    const existing = { address_id: 1, address: '123 Main St', district: 'Central', city_id: 1 };
    const updateData = { address: '123 Updated St' };
    const updated = { ...existing, ...updateData };

    addressRepository.findById.mockResolvedValue(existing);
    addressRepository.update.mockResolvedValue([updated]);

    const result = await addressService.update(1, updateData);

    expect(result).toEqual(updated);
    expect(addressRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when address does not exist', async () => {
    addressRepository.findById.mockResolvedValue(null);

    await expect(addressService.update(999, { address: 'New' }))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── addressService.remove ───────────────────────────────────────────

describe('addressService.remove', () => {
  test('removes the address successfully', async () => {
    addressRepository.findById.mockResolvedValue({ address_id: 1 });
    addressRepository.remove.mockResolvedValue(1);

    await expect(addressService.remove(1)).resolves.toBeUndefined();
    expect(addressRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when address does not exist', async () => {
    addressRepository.findById.mockResolvedValue(null);

    await expect(addressService.remove(999))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── cityService.findAll ─────────────────────────────────────────────

describe('cityService.findAll', () => {
  test('returns paginated cities with total count', async () => {
    const mockCities = [
      { city_id: 1, city: 'London', country_id: 1 },
      { city_id: 2, city: 'Paris', country_id: 2 },
    ];
    cityRepository.findAll.mockResolvedValue(mockCities);
    cityRepository.count.mockResolvedValue({ total: '50' });

    const result = await cityService.findAll({ page: 2, size: 2, sort: 'city' });

    expect(result).toEqual({ data: mockCities, total: 50, page: 2, size: 2 });
    expect(cityRepository.findAll).toHaveBeenCalledWith({ page: 2, size: 2, sort: 'city' });
    expect(cityRepository.count).toHaveBeenCalled();
  });
});

// ─── cityService.findById ────────────────────────────────────────────

describe('cityService.findById', () => {
  test('returns city when found', async () => {
    const mockCity = { city_id: 1, city: 'London', country_id: 1 };
    cityRepository.findById.mockResolvedValue(mockCity);

    const result = await cityService.findById(1);

    expect(result).toEqual(mockCity);
  });

  test('throws 404 when city not found', async () => {
    cityRepository.findById.mockResolvedValue(null);

    await expect(cityService.findById(999))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── cityService.create ──────────────────────────────────────────────

describe('cityService.create', () => {
  test('returns the created city', async () => {
    const input = { city: 'Berlin', country_id: 3 };
    const created = { city_id: 10, ...input };
    cityRepository.create.mockResolvedValue([created]);

    const result = await cityService.create(input);

    expect(result).toEqual(created);
    expect(cityRepository.create).toHaveBeenCalledWith(input);
  });
});

// ─── cityService.update ──────────────────────────────────────────────

describe('cityService.update', () => {
  test('returns the updated city', async () => {
    const existing = { city_id: 1, city: 'London', country_id: 1 };
    const updateData = { city: 'Greater London' };
    const updated = { ...existing, ...updateData };

    cityRepository.findById.mockResolvedValue(existing);
    cityRepository.update.mockResolvedValue([updated]);

    const result = await cityService.update(1, updateData);

    expect(result).toEqual(updated);
    expect(cityRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when city does not exist', async () => {
    cityRepository.findById.mockResolvedValue(null);

    await expect(cityService.update(999, { city: 'New' }))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── cityService.remove ──────────────────────────────────────────────

describe('cityService.remove', () => {
  test('removes the city successfully', async () => {
    cityRepository.findById.mockResolvedValue({ city_id: 1 });
    cityRepository.remove.mockResolvedValue(1);

    await expect(cityService.remove(1)).resolves.toBeUndefined();
    expect(cityRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when city does not exist', async () => {
    cityRepository.findById.mockResolvedValue(null);

    await expect(cityService.remove(999))
      .rejects
      .toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── Address Joi Validation ──────────────────────────────────────────

describe('Address Joi Validation', () => {
  describe('createAddressSchema', () => {
    test('accepts valid input', () => {
      const valid = { address: '123 Main St', district: 'Central', city_id: 1, postal_code: '12345', phone: '555-1234' };
      const { error } = createAddressSchema.validate(valid);
      expect(error).toBeUndefined();
    });

    test('rejects missing required fields', () => {
      const { error } = createAddressSchema.validate({}, { abortEarly: false });
      expect(error).toBeDefined();
      const fields = error.details.map(d => d.context.key);
      expect(fields).toContain('address');
      expect(fields).toContain('district');
      expect(fields).toContain('city_id');
    });

    test('rejects address exceeding max length', () => {
      const { error } = createAddressSchema.validate({
        address: 'a'.repeat(51),
        district: 'Central',
        city_id: 1,
      });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('address');
    });

    test('rejects district exceeding max length', () => {
      const { error } = createAddressSchema.validate({
        address: '123 Main St',
        district: 'd'.repeat(21),
        city_id: 1,
      });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('district');
    });

    test('rejects non-positive city_id', () => {
      const { error } = createAddressSchema.validate({
        address: '123 Main St',
        district: 'Central',
        city_id: -1,
      });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('city_id');
    });
  });

  describe('updateAddressSchema', () => {
    test('requires at least one field', () => {
      const { error } = updateAddressSchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].type).toBe('object.min');
    });

    test('accepts a single valid field', () => {
      const { error } = updateAddressSchema.validate({ address: 'Updated St' });
      expect(error).toBeUndefined();
    });
  });
});

// ─── City Joi Validation ─────────────────────────────────────────────

describe('City Joi Validation', () => {
  describe('createCitySchema', () => {
    test('accepts valid input', () => {
      const { error } = createCitySchema.validate({ city: 'London', country_id: 1 });
      expect(error).toBeUndefined();
    });

    test('rejects missing required fields', () => {
      const { error } = createCitySchema.validate({}, { abortEarly: false });
      expect(error).toBeDefined();
      const fields = error.details.map(d => d.context.key);
      expect(fields).toContain('city');
      expect(fields).toContain('country_id');
    });

    test('rejects city exceeding max length', () => {
      const { error } = createCitySchema.validate({ city: 'c'.repeat(51), country_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('city');
    });
  });

  describe('updateCitySchema', () => {
    test('requires at least one field', () => {
      const { error } = updateCitySchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].type).toBe('object.min');
    });

    test('accepts a single valid field', () => {
      const { error } = updateCitySchema.validate({ city: 'Updated City' });
      expect(error).toBeUndefined();
    });
  });
});
