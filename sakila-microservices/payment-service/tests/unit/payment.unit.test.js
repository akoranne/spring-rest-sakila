// Unit tests for Payment Service
// Validates: Requirements 13.1, 13.2, 13.4

process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';

jest.mock('../../src/repositories/paymentRepository');

const paymentRepository = require('../../src/repositories/paymentRepository');
const paymentService = require('../../src/services/paymentService');
const { updatePaymentSchema } = require('../../src/validators/paymentValidator');

afterEach(() => {
  jest.restoreAllMocks();
});

// ─── paymentService.findAll ──────────────────────────────────────────

describe('paymentService.findAll', () => {
  test('returns paginated payments with total count', async () => {
    const mockPayments = [
      { payment_id: 1, amount: 5.99, customer_id: 1 },
      { payment_id: 2, amount: 9.99, customer_id: 2 },
    ];
    paymentRepository.findAll.mockResolvedValue(mockPayments);
    paymentRepository.count.mockResolvedValue({ total: '50' });

    const result = await paymentService.findAll({ page: 1, size: 2, sort: 'payment_id' });

    expect(result).toEqual({ data: mockPayments, total: 50, page: 1, size: 2 });
    expect(paymentRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'payment_id' });
    expect(paymentRepository.count).toHaveBeenCalled();
  });

  test('passes customerId filter to repository', async () => {
    paymentRepository.findAll.mockResolvedValue([]);
    paymentRepository.count.mockResolvedValue({ total: '0' });

    await paymentService.findAll({ page: 1, size: 10, sort: 'payment_id', customerId: 42 });

    expect(paymentRepository.findAll).toHaveBeenCalledWith({
      page: 1, size: 10, sort: 'payment_id', customerId: 42,
    });
    expect(paymentRepository.count).toHaveBeenCalledWith({ customerId: 42 });
  });
});

// ─── paymentService.findById ─────────────────────────────────────────

describe('paymentService.findById', () => {
  test('returns payment when found', async () => {
    const mockPayment = { payment_id: 1, amount: 5.99 };
    paymentRepository.findById.mockResolvedValue(mockPayment);

    const result = await paymentService.findById(1);
    expect(result).toEqual(mockPayment);
  });

  test('throws 404 when payment not found', async () => {
    paymentRepository.findById.mockResolvedValue(null);

    await expect(paymentService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── paymentService.findByIdWithDetails ──────────────────────────────

describe('paymentService.findByIdWithDetails', () => {
  test('returns payment with details when found', async () => {
    const mockPayment = { payment_id: 1, amount: 5.99, customer_id: 1 };
    paymentRepository.findByIdWithDetails.mockResolvedValue(mockPayment);

    const result = await paymentService.findByIdWithDetails(1);
    expect(result).toEqual(mockPayment);
  });

  test('throws 404 when payment not found', async () => {
    paymentRepository.findByIdWithDetails.mockResolvedValue(null);

    await expect(paymentService.findByIdWithDetails(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── paymentService.update ───────────────────────────────────────────

describe('paymentService.update', () => {
  test('returns updated payment', async () => {
    const existing = { payment_id: 1, amount: 5.99 };
    const updateData = { amount: 7.99 };
    const updated = { ...existing, ...updateData };

    paymentRepository.findById.mockResolvedValue(existing);
    paymentRepository.update.mockResolvedValue([updated]);

    const result = await paymentService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(paymentRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when payment does not exist', async () => {
    paymentRepository.findById.mockResolvedValue(null);

    await expect(paymentService.update(999, { amount: 7.99 }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── paymentService.remove ───────────────────────────────────────────

describe('paymentService.remove', () => {
  test('removes successfully', async () => {
    paymentRepository.findById.mockResolvedValue({ payment_id: 1 });
    paymentRepository.remove.mockResolvedValue(1);

    await expect(paymentService.remove(1)).resolves.toBeUndefined();
    expect(paymentRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when payment does not exist', async () => {
    paymentRepository.findById.mockResolvedValue(null);

    await expect(paymentService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── Payment Joi Validation (updatePaymentSchema) ────────────────────

describe('Payment Joi Validation (updatePaymentSchema)', () => {
  test('requires at least one field', () => {
    const { error } = updatePaymentSchema.validate({});
    expect(error).toBeDefined();
    expect(error.details[0].type).toBe('object.min');
  });

  test('accepts valid amount', () => {
    const { error } = updatePaymentSchema.validate({ amount: 9.99 });
    expect(error).toBeUndefined();
  });

  test('rejects negative amount', () => {
    const { error } = updatePaymentSchema.validate({ amount: -5 });
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('amount');
  });

  test('accepts valid integer IDs', () => {
    const { error } = updatePaymentSchema.validate({ customer_id: 1, staff_id: 2, rental_id: 3 });
    expect(error).toBeUndefined();
  });

  test('rejects non-integer IDs', () => {
    const { error } = updatePaymentSchema.validate({ customer_id: 1.5 }, { abortEarly: false });
    expect(error).toBeDefined();
    expect(error.details[0].context.key).toBe('customer_id');
  });
});
