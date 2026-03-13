// Unit tests for Rental Service
// Validates: Requirements 13.1, 13.2, 13.4

process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';

jest.mock('../../src/repositories/rentalRepository');

const rentalRepository = require('../../src/repositories/rentalRepository');
const rentalService = require('../../src/services/rentalService');
const { createRentalSchema, updateRentalSchema, returnRentalSchema } = require('../../src/validators/rentalValidator');

afterEach(() => {
  jest.restoreAllMocks();
});

// ─── rentalService.findAll ───────────────────────────────────────────

describe('rentalService.findAll', () => {
  test('returns paginated rentals with total count', async () => {
    const mockRentals = [
      { rental_id: 1, inventory_id: 10, customer_id: 1, staff_id: 1 },
      { rental_id: 2, inventory_id: 20, customer_id: 2, staff_id: 1 },
    ];
    rentalRepository.findAll.mockResolvedValue(mockRentals);
    rentalRepository.count.mockResolvedValue({ total: '100' });

    const result = await rentalService.findAll({ page: 1, size: 2, sort: 'rental_id' });

    expect(result).toEqual({ data: mockRentals, total: 100, page: 1, size: 2 });
    expect(rentalRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'rental_id' });
    expect(rentalRepository.count).toHaveBeenCalledWith({});
  });

  test('passes customerId filter to repository', async () => {
    rentalRepository.findAll.mockResolvedValue([]);
    rentalRepository.count.mockResolvedValue({ total: '0' });

    await rentalService.findAll({ page: 1, size: 10, sort: 'rental_id', customerId: 42 });

    expect(rentalRepository.findAll).toHaveBeenCalledWith({
      page: 1, size: 10, sort: 'rental_id', customerId: 42,
    });
    expect(rentalRepository.count).toHaveBeenCalledWith({ customerId: 42 });
  });
});

// ─── rentalService.findById ──────────────────────────────────────────

describe('rentalService.findById', () => {
  test('returns rental when found', async () => {
    const mockRental = { rental_id: 1, inventory_id: 10, customer_id: 1 };
    rentalRepository.findById.mockResolvedValue(mockRental);

    const result = await rentalService.findById(1);
    expect(result).toEqual(mockRental);
  });

  test('throws 404 when rental not found', async () => {
    rentalRepository.findById.mockResolvedValue(null);

    await expect(rentalService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── rentalService.create ────────────────────────────────────────────

describe('rentalService.create', () => {
  test('creates rental with provided rental_date', async () => {
    const rentalDate = new Date('2026-03-01T10:00:00Z');
    const input = { inventory_id: 10, customer_id: 1, staff_id: 1, rental_date: rentalDate };
    const created = { rental_id: 1, ...input };
    rentalRepository.create.mockResolvedValue([created]);

    const result = await rentalService.create(input);

    expect(result).toEqual(created);
    expect(rentalRepository.create).toHaveBeenCalledWith(input);
  });

  test('defaults rental_date to now when not provided', async () => {
    const input = { inventory_id: 10, customer_id: 1, staff_id: 1 };
    const before = new Date();
    rentalRepository.create.mockImplementation((data) => {
      expect(data.rental_date).toBeInstanceOf(Date);
      expect(data.rental_date.getTime()).toBeGreaterThanOrEqual(before.getTime());
      return [{ rental_id: 1, ...data }];
    });

    const result = await rentalService.create({ ...input });

    expect(result.rental_id).toBe(1);
    expect(rentalRepository.create).toHaveBeenCalled();
  });

  test('creates rental with null return_date', async () => {
    const input = { inventory_id: 10, customer_id: 1, staff_id: 1, return_date: null };
    const created = { rental_id: 1, ...input, rental_date: expect.any(Date) };
    rentalRepository.create.mockResolvedValue([{ rental_id: 1, ...input, rental_date: new Date() }]);

    const result = await rentalService.create({ ...input });
    expect(result.return_date).toBeNull();
  });
});

// ─── rentalService.update ────────────────────────────────────────────

describe('rentalService.update', () => {
  test('returns updated rental', async () => {
    const existing = { rental_id: 1, inventory_id: 10, customer_id: 1, staff_id: 1 };
    const updateData = { inventory_id: 20 };
    const updated = { ...existing, ...updateData };

    rentalRepository.findById.mockResolvedValue(existing);
    rentalRepository.update.mockResolvedValue([updated]);

    const result = await rentalService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(rentalRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when rental does not exist', async () => {
    rentalRepository.findById.mockResolvedValue(null);

    await expect(rentalService.update(999, { inventory_id: 20 }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── rentalService.returnRental ──────────────────────────────────────

describe('rentalService.returnRental', () => {
  test('sets return_date on an active rental', async () => {
    const existing = { rental_id: 1, return_date: null };
    const returnDate = new Date('2026-03-10T12:00:00Z');
    const returned = { ...existing, return_date: returnDate };

    rentalRepository.findById.mockResolvedValue(existing);
    rentalRepository.update.mockResolvedValue([returned]);

    const result = await rentalService.returnRental({ rental_id: 1, return_date: returnDate });
    expect(result.return_date).toEqual(returnDate);
    expect(rentalRepository.update).toHaveBeenCalledWith(1, { return_date: returnDate });
  });

  test('defaults return_date to now when not provided', async () => {
    const existing = { rental_id: 1, return_date: null };
    const before = new Date();

    rentalRepository.findById.mockResolvedValue(existing);
    rentalRepository.update.mockImplementation((id, data) => {
      expect(data.return_date).toBeInstanceOf(Date);
      expect(data.return_date.getTime()).toBeGreaterThanOrEqual(before.getTime());
      return [{ ...existing, ...data }];
    });

    const result = await rentalService.returnRental({ rental_id: 1 });
    expect(result.return_date).toBeInstanceOf(Date);
  });

  test('throws 400 when rental already returned', async () => {
    const existing = { rental_id: 1, return_date: new Date('2026-03-05') };
    rentalRepository.findById.mockResolvedValue(existing);

    await expect(rentalService.returnRental({ rental_id: 1 }))
      .rejects.toMatchObject({ statusCode: 400, code: 'VALIDATION_ERROR' });
  });

  test('throws 404 when rental does not exist', async () => {
    rentalRepository.findById.mockResolvedValue(null);

    await expect(rentalService.returnRental({ rental_id: 999 }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── rentalService.remove ────────────────────────────────────────────

describe('rentalService.remove', () => {
  test('removes successfully', async () => {
    rentalRepository.findById.mockResolvedValue({ rental_id: 1 });
    rentalRepository.remove.mockResolvedValue(1);

    await expect(rentalService.remove(1)).resolves.toBeUndefined();
    expect(rentalRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when rental does not exist', async () => {
    rentalRepository.findById.mockResolvedValue(null);

    await expect(rentalService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── Rental Joi Validation (createRentalSchema) ─────────────────────

describe('Rental Joi Validation (createRentalSchema)', () => {
  test('accepts valid rental data', () => {
    const { error } = createRentalSchema.validate({
      inventory_id: 1, customer_id: 2, staff_id: 3,
    });
    expect(error).toBeUndefined();
  });

  test('accepts rental with optional rental_date', () => {
    const { error } = createRentalSchema.validate({
      inventory_id: 1, customer_id: 2, staff_id: 3,
      rental_date: '2026-03-01T10:00:00Z',
    });
    expect(error).toBeUndefined();
  });

  test('accepts rental with null return_date', () => {
    const { error } = createRentalSchema.validate({
      inventory_id: 1, customer_id: 2, staff_id: 3, return_date: null,
    });
    expect(error).toBeUndefined();
  });

  test('rejects missing inventory_id', () => {
    const { error } = createRentalSchema.validate(
      { customer_id: 2, staff_id: 3 },
      { abortEarly: false },
    );
    expect(error).toBeDefined();
    expect(error.details.some((d) => d.context.key === 'inventory_id')).toBe(true);
  });

  test('rejects missing customer_id', () => {
    const { error } = createRentalSchema.validate(
      { inventory_id: 1, staff_id: 3 },
      { abortEarly: false },
    );
    expect(error).toBeDefined();
    expect(error.details.some((d) => d.context.key === 'customer_id')).toBe(true);
  });

  test('rejects missing staff_id', () => {
    const { error } = createRentalSchema.validate(
      { inventory_id: 1, customer_id: 2 },
      { abortEarly: false },
    );
    expect(error).toBeDefined();
    expect(error.details.some((d) => d.context.key === 'staff_id')).toBe(true);
  });

  test('rejects non-integer inventory_id', () => {
    const { error } = createRentalSchema.validate({
      inventory_id: 1.5, customer_id: 2, staff_id: 3,
    });
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('inventory_id');
  });

  test('reports all missing required fields at once', () => {
    const { error } = createRentalSchema.validate({}, { abortEarly: false });
    expect(error).toBeDefined();
    const fields = error.details.map((d) => d.context.key);
    expect(fields).toContain('inventory_id');
    expect(fields).toContain('customer_id');
    expect(fields).toContain('staff_id');
  });
});

// ─── Rental Joi Validation (updateRentalSchema) ─────────────────────

describe('Rental Joi Validation (updateRentalSchema)', () => {
  test('requires at least one field', () => {
    const { error } = updateRentalSchema.validate({});
    expect(error).toBeDefined();
    expect(error.details[0].type).toBe('object.min');
  });

  test('accepts partial update with inventory_id', () => {
    const { error } = updateRentalSchema.validate({ inventory_id: 5 });
    expect(error).toBeUndefined();
  });

  test('accepts null return_date for clearing', () => {
    const { error } = updateRentalSchema.validate({ return_date: null });
    expect(error).toBeUndefined();
  });

  test('rejects non-integer customer_id', () => {
    const { error } = updateRentalSchema.validate({ customer_id: 'abc' });
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('customer_id');
  });
});

// ─── Rental Joi Validation (returnRentalSchema) ─────────────────────

describe('Rental Joi Validation (returnRentalSchema)', () => {
  test('accepts valid return with rental_id only', () => {
    const { error } = returnRentalSchema.validate({ rental_id: 1 });
    expect(error).toBeUndefined();
  });

  test('accepts return with optional return_date', () => {
    const { error } = returnRentalSchema.validate({
      rental_id: 1, return_date: '2026-03-10T12:00:00Z',
    });
    expect(error).toBeUndefined();
  });

  test('rejects missing rental_id', () => {
    const { error } = returnRentalSchema.validate({});
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('rental_id');
  });

  test('rejects non-integer rental_id', () => {
    const { error } = returnRentalSchema.validate({ rental_id: 'abc' });
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('rental_id');
  });
});
