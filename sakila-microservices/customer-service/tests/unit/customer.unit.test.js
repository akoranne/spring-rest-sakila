// Unit tests for Customer Service
// Validates: Requirements 13.1, 13.2, 13.4

process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';
process.env.PAYMENT_SERVICE_URL = 'http://payment-service:3005';
process.env.RENTAL_SERVICE_URL = 'http://rental-service:3006';

jest.mock('../../src/repositories/customerRepository');
jest.mock('../../src/utils/httpClient');

const customerRepository = require('../../src/repositories/customerRepository');
const httpClient = require('../../src/utils/httpClient');
const customerService = require('../../src/services/customerService');
const { createCustomerSchema, updateCustomerSchema } = require('../../src/validators/customerValidator');

afterEach(() => {
  jest.restoreAllMocks();
});

// ─── customerService.findAll ─────────────────────────────────────────

describe('customerService.findAll', () => {
  test('returns paginated customers with total count', async () => {
    const mockCustomers = [
      { customer_id: 1, first_name: 'John', last_name: 'Doe', store_id: 1, address_id: 1 },
      { customer_id: 2, first_name: 'Jane', last_name: 'Smith', store_id: 1, address_id: 2 },
    ];
    customerRepository.findAll.mockResolvedValue(mockCustomers);
    customerRepository.count.mockResolvedValue({ total: '50' });

    const result = await customerService.findAll({ page: 1, size: 2, sort: 'customer_id' });

    expect(result).toEqual({ data: mockCustomers, total: 50, page: 1, size: 2 });
    expect(customerRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'customer_id' });
    expect(customerRepository.count).toHaveBeenCalled();
  });
});

// ─── customerService.findById ────────────────────────────────────────

describe('customerService.findById', () => {
  test('returns customer when found', async () => {
    const mockCustomer = { customer_id: 1, first_name: 'John', last_name: 'Doe' };
    customerRepository.findById.mockResolvedValue(mockCustomer);

    const result = await customerService.findById(1);
    expect(result).toEqual(mockCustomer);
  });

  test('throws 404 when customer not found', async () => {
    customerRepository.findById.mockResolvedValue(null);

    await expect(customerService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── customerService.create ──────────────────────────────────────────

describe('customerService.create', () => {
  test('returns created customer', async () => {
    const inputData = { first_name: 'John', last_name: 'Doe', store_id: 1, address_id: 1 };
    const created = { customer_id: 1, ...inputData };
    customerRepository.create.mockResolvedValue([created]);

    const result = await customerService.create(inputData);
    expect(result).toEqual(created);
    expect(customerRepository.create).toHaveBeenCalledWith(inputData);
  });
});

// ─── customerService.update ──────────────────────────────────────────

describe('customerService.update', () => {
  test('returns updated customer', async () => {
    const existing = { customer_id: 1, first_name: 'John', last_name: 'Doe' };
    const updateData = { first_name: 'Johnny' };
    const updated = { ...existing, ...updateData };

    customerRepository.findById.mockResolvedValue(existing);
    customerRepository.update.mockResolvedValue([updated]);

    const result = await customerService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(customerRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when customer does not exist', async () => {
    customerRepository.findById.mockResolvedValue(null);

    await expect(customerService.update(999, { first_name: 'Johnny' }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── customerService.remove ──────────────────────────────────────────

describe('customerService.remove', () => {
  test('removes successfully', async () => {
    customerRepository.findById.mockResolvedValue({ customer_id: 1 });
    customerRepository.remove.mockResolvedValue(1);

    await expect(customerService.remove(1)).resolves.toBeUndefined();
    expect(customerRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when customer does not exist', async () => {
    customerRepository.findById.mockResolvedValue(null);

    await expect(customerService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── customerService.findByIdWithDetails ─────────────────────────────

describe('customerService.findByIdWithDetails', () => {
  const mockCustomer = { customer_id: 1, first_name: 'John', last_name: 'Doe' };

  test('returns customer with payments and rentals arrays', async () => {
    const mockPayments = [{ payment_id: 1, amount: 5.99 }];
    const mockRentals = [{ rental_id: 1, inventory_id: 10 }];

    customerRepository.findById.mockResolvedValue(mockCustomer);
    httpClient.get.mockImplementation((url) => {
      if (url.includes('/payments')) {
        return Promise.resolve({ status: 200, data: { data: mockPayments }, headers: {} });
      }
      if (url.includes('/rentals')) {
        return Promise.resolve({ status: 200, data: { data: mockRentals }, headers: {} });
      }
    });

    const result = await customerService.findByIdWithDetails(1, {
      correlationId: 'test-corr-id',
      authToken: 'test-token',
    });

    expect(result).toEqual({
      ...mockCustomer,
      payments: mockPayments,
      rentals: mockRentals,
    });
    expect(httpClient.get).toHaveBeenCalledTimes(2);
  });

  test('throws 503 when downstream service is unavailable', async () => {
    customerRepository.findById.mockResolvedValue(mockCustomer);
    httpClient.get.mockRejectedValue({
      statusCode: 503,
      code: 'SERVICE_UNAVAILABLE',
      message: 'Service request timed out',
    });

    await expect(customerService.findByIdWithDetails(1, {}))
      .rejects.toMatchObject({ statusCode: 503, code: 'SERVICE_UNAVAILABLE' });
  });

  test('propagates non-503 errors from downstream', async () => {
    customerRepository.findById.mockResolvedValue(mockCustomer);
    const downstreamError = new Error('Something unexpected');
    downstreamError.statusCode = 500;
    httpClient.get.mockRejectedValue(downstreamError);

    await expect(customerService.findByIdWithDetails(1, {}))
      .rejects.toMatchObject({ statusCode: 500 });
  });
});

// ─── Joi Validation (createCustomerSchema) ───────────────────────────

describe('Joi Validation (createCustomerSchema)', () => {
  test('requires first_name, last_name, store_id, address_id', () => {
    const { error } = createCustomerSchema.validate({}, { abortEarly: false });
    expect(error).toBeDefined();
    const fields = error.details.map((d) => d.context.key);
    expect(fields).toContain('first_name');
    expect(fields).toContain('last_name');
    expect(fields).toContain('store_id');
    expect(fields).toContain('address_id');
  });

  test('rejects first_name exceeding 45 characters', () => {
    const { error } = createCustomerSchema.validate({
      first_name: 'A'.repeat(46),
      last_name: 'Doe',
      store_id: 1,
      address_id: 1,
    });
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('first_name');
  });

  test('accepts valid input', () => {
    const { error } = createCustomerSchema.validate({
      first_name: 'John',
      last_name: 'Doe',
      store_id: 1,
      address_id: 1,
    });
    expect(error).toBeUndefined();
  });
});

// ─── Joi Validation (updateCustomerSchema) ───────────────────────────

describe('Joi Validation (updateCustomerSchema)', () => {
  test('requires at least one field', () => {
    const { error } = updateCustomerSchema.validate({});
    expect(error).toBeDefined();
    expect(error.details[0].type).toBe('object.min');
  });

  test('accepts partial updates', () => {
    const { error } = updateCustomerSchema.validate({ first_name: 'Johnny' });
    expect(error).toBeUndefined();
  });

  test('rejects empty object', () => {
    const { error } = updateCustomerSchema.validate({});
    expect(error).toBeDefined();
  });
});
