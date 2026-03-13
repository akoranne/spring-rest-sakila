// Unit tests for Store Service
// Validates: Requirements 13.1, 13.2, 13.4

process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/testdb';
process.env.JWT_SECRET = 'test-secret-for-unit-tests';

jest.mock('../../src/repositories/storeRepository');
jest.mock('../../src/repositories/staffRepository');
jest.mock('../../src/repositories/inventoryRepository');
jest.mock('../../src/db', () => ({
  raw: jest.fn(),
}));

const storeRepository = require('../../src/repositories/storeRepository');
const staffRepository = require('../../src/repositories/staffRepository');
const inventoryRepository = require('../../src/repositories/inventoryRepository');
const db = require('../../src/db');

const storeService = require('../../src/services/storeService');
const staffService = require('../../src/services/staffService');
const inventoryService = require('../../src/services/inventoryService');
const reportService = require('../../src/services/reportService');

const { createStoreSchema, updateStoreSchema } = require('../../src/validators/storeValidator');
const { createStaffSchema, updateStaffSchema } = require('../../src/validators/staffValidator');

afterEach(() => {
  jest.restoreAllMocks();
});

// ─── storeService.findAll ────────────────────────────────────────────

describe('storeService.findAll', () => {
  test('returns paginated stores with total count', async () => {
    const mockStores = [
      { store_id: 1, manager_staff_id: 1, address_id: 10 },
      { store_id: 2, manager_staff_id: 2, address_id: 20 },
    ];
    storeRepository.findAll.mockResolvedValue(mockStores);
    storeRepository.count.mockResolvedValue({ total: '5' });

    const result = await storeService.findAll({ page: 1, size: 2, sort: 'store_id' });

    expect(result).toEqual({ data: mockStores, total: 5, page: 1, size: 2 });
    expect(storeRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'store_id' });
    expect(storeRepository.count).toHaveBeenCalled();
  });
});

// ─── storeService.findById ───────────────────────────────────────────

describe('storeService.findById', () => {
  test('returns store when found', async () => {
    const mockStore = { store_id: 1, manager_staff_id: 1, address_id: 10 };
    storeRepository.findById.mockResolvedValue(mockStore);

    const result = await storeService.findById(1);
    expect(result).toEqual(mockStore);
  });

  test('throws 404 when store not found', async () => {
    storeRepository.findById.mockResolvedValue(null);

    await expect(storeService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── storeService.findByIdWithDetails ────────────────────────────────

describe('storeService.findByIdWithDetails', () => {
  test('returns store with details when found', async () => {
    const mockStore = { store_id: 1, manager_staff_id: 1, address_id: 10 };
    storeRepository.findByIdWithDetails.mockResolvedValue(mockStore);

    const result = await storeService.findByIdWithDetails(1);
    expect(result).toEqual(mockStore);
  });

  test('throws 404 when store not found', async () => {
    storeRepository.findByIdWithDetails.mockResolvedValue(null);

    await expect(storeService.findByIdWithDetails(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── storeService.create ─────────────────────────────────────────────

describe('storeService.create', () => {
  test('returns the created store', async () => {
    const input = { manager_staff_id: 1, address_id: 10 };
    const created = { store_id: 3, ...input };
    storeRepository.create.mockResolvedValue([created]);

    const result = await storeService.create(input);
    expect(result).toEqual(created);
    expect(storeRepository.create).toHaveBeenCalledWith(input);
  });
});

// ─── storeService.update ─────────────────────────────────────────────

describe('storeService.update', () => {
  test('returns the updated store', async () => {
    const existing = { store_id: 1, manager_staff_id: 1, address_id: 10 };
    const updateData = { address_id: 20 };
    const updated = { ...existing, ...updateData };

    storeRepository.findById.mockResolvedValue(existing);
    storeRepository.update.mockResolvedValue([updated]);

    const result = await storeService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(storeRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when store does not exist', async () => {
    storeRepository.findById.mockResolvedValue(null);

    await expect(storeService.update(999, { address_id: 20 }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── storeService.remove ─────────────────────────────────────────────

describe('storeService.remove', () => {
  test('removes successfully', async () => {
    storeRepository.findById.mockResolvedValue({ store_id: 1 });
    storeRepository.remove.mockResolvedValue(1);

    await expect(storeService.remove(1)).resolves.toBeUndefined();
    expect(storeRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when store does not exist', async () => {
    storeRepository.findById.mockResolvedValue(null);

    await expect(storeService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});


// ─── staffService.findAll ────────────────────────────────────────────

describe('staffService.findAll', () => {
  test('returns paginated staff with total count', async () => {
    const mockStaff = [
      { staff_id: 1, first_name: 'Mike', last_name: 'Hillyer', store_id: 1 },
      { staff_id: 2, first_name: 'Jon', last_name: 'Stephens', store_id: 2 },
    ];
    staffRepository.findAll.mockResolvedValue(mockStaff);
    staffRepository.count.mockResolvedValue({ total: '10' });

    const result = await staffService.findAll({ page: 1, size: 2, sort: 'staff_id' });

    expect(result).toEqual({ data: mockStaff, total: 10, page: 1, size: 2 });
    expect(staffRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'staff_id' });
    expect(staffRepository.count).toHaveBeenCalled();
  });
});

// ─── staffService.findById ───────────────────────────────────────────

describe('staffService.findById', () => {
  test('returns staff when found', async () => {
    const mockStaff = { staff_id: 1, first_name: 'Mike', last_name: 'Hillyer' };
    staffRepository.findById.mockResolvedValue(mockStaff);

    const result = await staffService.findById(1);
    expect(result).toEqual(mockStaff);
  });

  test('throws 404 when staff not found', async () => {
    staffRepository.findById.mockResolvedValue(null);

    await expect(staffService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── staffService.findByIdWithDetails ────────────────────────────────

describe('staffService.findByIdWithDetails', () => {
  test('returns staff with details when found', async () => {
    const mockStaff = { staff_id: 1, first_name: 'Mike', last_name: 'Hillyer', store_id: 1 };
    staffRepository.findByIdWithDetails.mockResolvedValue(mockStaff);

    const result = await staffService.findByIdWithDetails(1);
    expect(result).toEqual(mockStaff);
  });

  test('throws 404 when staff not found', async () => {
    staffRepository.findByIdWithDetails.mockResolvedValue(null);

    await expect(staffService.findByIdWithDetails(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── staffService.findByStoreId ──────────────────────────────────────

describe('staffService.findByStoreId', () => {
  test('returns staff for a given store', async () => {
    const mockStaff = [
      { staff_id: 1, first_name: 'Mike', store_id: 1 },
      { staff_id: 3, first_name: 'Jane', store_id: 1 },
    ];
    staffRepository.findByStoreId.mockResolvedValue(mockStaff);

    const result = await staffService.findByStoreId(1);
    expect(result).toEqual(mockStaff);
    expect(staffRepository.findByStoreId).toHaveBeenCalledWith(1);
  });

  test('returns empty array when no staff in store', async () => {
    staffRepository.findByStoreId.mockResolvedValue([]);

    const result = await staffService.findByStoreId(99);
    expect(result).toEqual([]);
  });
});

// ─── staffService.findByStoreIdAndStaffId ────────────────────────────

describe('staffService.findByStoreIdAndStaffId', () => {
  test('returns staff when found in store', async () => {
    const mockStaff = { staff_id: 1, store_id: 1, first_name: 'Mike' };
    staffRepository.findByStoreIdAndStaffId.mockResolvedValue(mockStaff);

    const result = await staffService.findByStoreIdAndStaffId(1, 1);
    expect(result).toEqual(mockStaff);
  });

  test('throws 404 when staff not found in store', async () => {
    staffRepository.findByStoreIdAndStaffId.mockResolvedValue(null);

    await expect(staffService.findByStoreIdAndStaffId(1, 999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── staffService.create ─────────────────────────────────────────────

describe('staffService.create', () => {
  test('returns the created staff', async () => {
    const input = { first_name: 'New', last_name: 'Staff', store_id: 1 };
    const created = { staff_id: 10, ...input };
    staffRepository.create.mockResolvedValue([created]);

    const result = await staffService.create(input);
    expect(result).toEqual(created);
    expect(staffRepository.create).toHaveBeenCalledWith(input);
  });
});

// ─── staffService.update ─────────────────────────────────────────────

describe('staffService.update', () => {
  test('returns the updated staff', async () => {
    const existing = { staff_id: 1, first_name: 'Mike', last_name: 'Hillyer' };
    const updateData = { first_name: 'Michael' };
    const updated = { ...existing, ...updateData };

    staffRepository.findById.mockResolvedValue(existing);
    staffRepository.update.mockResolvedValue([updated]);

    const result = await staffService.update(1, updateData);
    expect(result).toEqual(updated);
    expect(staffRepository.update).toHaveBeenCalledWith(1, updateData);
  });

  test('throws 404 when staff does not exist', async () => {
    staffRepository.findById.mockResolvedValue(null);

    await expect(staffService.update(999, { first_name: 'X' }))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── staffService.remove ─────────────────────────────────────────────

describe('staffService.remove', () => {
  test('removes successfully', async () => {
    staffRepository.findById.mockResolvedValue({ staff_id: 1 });
    staffRepository.remove.mockResolvedValue(1);

    await expect(staffService.remove(1)).resolves.toBeUndefined();
    expect(staffRepository.remove).toHaveBeenCalledWith(1);
  });

  test('throws 404 when staff does not exist', async () => {
    staffRepository.findById.mockResolvedValue(null);

    await expect(staffService.remove(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── inventoryService.findAll ────────────────────────────────────────

describe('inventoryService.findAll', () => {
  test('returns paginated inventory with total count', async () => {
    const mockItems = [
      { inventory_id: 1, film_id: 1, store_id: 1 },
      { inventory_id: 2, film_id: 2, store_id: 1 },
    ];
    inventoryRepository.findAll.mockResolvedValue(mockItems);
    inventoryRepository.count.mockResolvedValue({ total: '100' });

    const result = await inventoryService.findAll({ page: 1, size: 2, sort: 'inventory_id' });

    expect(result).toEqual({ data: mockItems, total: 100, page: 1, size: 2 });
    expect(inventoryRepository.findAll).toHaveBeenCalledWith({ page: 1, size: 2, sort: 'inventory_id' });
    expect(inventoryRepository.count).toHaveBeenCalled();
  });
});

// ─── inventoryService.findById ───────────────────────────────────────

describe('inventoryService.findById', () => {
  test('returns inventory item when found', async () => {
    const mockItem = { inventory_id: 1, film_id: 1, store_id: 1 };
    inventoryRepository.findById.mockResolvedValue(mockItem);

    const result = await inventoryService.findById(1);
    expect(result).toEqual(mockItem);
  });

  test('throws 404 when inventory item not found', async () => {
    inventoryRepository.findById.mockResolvedValue(null);

    await expect(inventoryService.findById(999))
      .rejects.toMatchObject({ statusCode: 404, code: 'RESOURCE_NOT_FOUND' });
  });
});

// ─── inventoryService.findByStoreId ──────────────────────────────────

describe('inventoryService.findByStoreId', () => {
  test('returns inventory for a given store', async () => {
    const mockItems = [
      { inventory_id: 1, film_id: 1, store_id: 1 },
      { inventory_id: 3, film_id: 5, store_id: 1 },
    ];
    inventoryRepository.findByStoreId.mockResolvedValue(mockItems);

    const result = await inventoryService.findByStoreId(1);
    expect(result).toEqual(mockItems);
    expect(inventoryRepository.findByStoreId).toHaveBeenCalledWith(1);
  });
});

// ─── reportService ───────────────────────────────────────────────────

describe('reportService.salesByCategory', () => {
  test('returns sales aggregated by category', async () => {
    const mockRows = [
      { category: 'Action', total_sales: '1234.56' },
      { category: 'Comedy', total_sales: '987.65' },
    ];
    db.raw.mockResolvedValue({ rows: mockRows });

    const result = await reportService.salesByCategory();
    expect(result).toEqual(mockRows);
    expect(db.raw).toHaveBeenCalled();
  });
});

describe('reportService.salesByStore', () => {
  test('returns sales aggregated by store', async () => {
    const mockRows = [
      { store_id: 1, total_sales: '5000.00' },
      { store_id: 2, total_sales: '3000.00' },
    ];
    db.raw.mockResolvedValue({ rows: mockRows });

    const result = await reportService.salesByStore();
    expect(result).toEqual(mockRows);
    expect(db.raw).toHaveBeenCalled();
  });
});

// ─── Store Joi Validation ────────────────────────────────────────────

describe('Store Joi Validation', () => {
  describe('createStoreSchema', () => {
    test('accepts valid input', () => {
      const { error } = createStoreSchema.validate({ manager_staff_id: 1, address_id: 10 });
      expect(error).toBeUndefined();
    });

    test('rejects missing manager_staff_id', () => {
      const { error } = createStoreSchema.validate({ address_id: 10 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('manager_staff_id');
    });

    test('rejects missing address_id', () => {
      const { error } = createStoreSchema.validate({ manager_staff_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('address_id');
    });

    test('rejects non-positive manager_staff_id', () => {
      const { error } = createStoreSchema.validate({ manager_staff_id: 0, address_id: 10 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('manager_staff_id');
    });

    test('rejects non-integer address_id', () => {
      const { error } = createStoreSchema.validate({ manager_staff_id: 1, address_id: 1.5 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('address_id');
    });
  });

  describe('updateStoreSchema', () => {
    test('requires at least one field', () => {
      const { error } = updateStoreSchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].type).toBe('object.min');
    });

    test('accepts a single valid field', () => {
      const { error } = updateStoreSchema.validate({ address_id: 20 });
      expect(error).toBeUndefined();
    });

    test('rejects non-positive address_id', () => {
      const { error } = updateStoreSchema.validate({ address_id: -1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('address_id');
    });
  });
});

// ─── Staff Joi Validation ────────────────────────────────────────────

describe('Staff Joi Validation', () => {
  describe('createStaffSchema', () => {
    test('accepts valid input with all fields', () => {
      const valid = {
        first_name: 'Mike',
        last_name: 'Hillyer',
        address_id: 3,
        store_id: 1,
        active: true,
        username: 'Mike',
        authority_id: 2,
      };
      const { error } = createStaffSchema.validate(valid);
      expect(error).toBeUndefined();
    });

    test('accepts minimal valid input (only required fields)', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', last_name: 'B', store_id: 1 });
      expect(error).toBeUndefined();
    });

    test('rejects missing first_name', () => {
      const { error } = createStaffSchema.validate({ last_name: 'B', store_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('first_name');
    });

    test('rejects missing last_name', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', store_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('last_name');
    });

    test('rejects missing store_id', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', last_name: 'B' });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('store_id');
    });

    test('rejects first_name exceeding 45 characters', () => {
      const { error } = createStaffSchema.validate({ first_name: 'a'.repeat(46), last_name: 'B', store_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('first_name');
    });

    test('rejects last_name exceeding 45 characters', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', last_name: 'b'.repeat(46), store_id: 1 });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('last_name');
    });

    test('rejects username exceeding 16 characters', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', last_name: 'B', store_id: 1, username: 'a'.repeat(17) });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('username');
    });

    test('allows null address_id', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', last_name: 'B', store_id: 1, address_id: null });
      expect(error).toBeUndefined();
    });

    test('allows empty username', () => {
      const { error } = createStaffSchema.validate({ first_name: 'A', last_name: 'B', store_id: 1, username: '' });
      expect(error).toBeUndefined();
    });
  });

  describe('updateStaffSchema', () => {
    test('requires at least one field', () => {
      const { error } = updateStaffSchema.validate({});
      expect(error).toBeDefined();
      expect(error.details[0].type).toBe('object.min');
    });

    test('accepts a single valid field', () => {
      const { error } = updateStaffSchema.validate({ first_name: 'Updated' });
      expect(error).toBeUndefined();
    });

    test('rejects first_name exceeding 45 characters', () => {
      const { error } = updateStaffSchema.validate({ first_name: 'a'.repeat(46) });
      expect(error).toBeDefined();
      expect(error.details[0].context.key).toBe('first_name');
    });

    test('accepts boolean active field', () => {
      const { error } = updateStaffSchema.validate({ active: false });
      expect(error).toBeUndefined();
    });
  });
});

// ─── Role-Based Access (Route Middleware Configuration) ───────────────

describe('Role-Based Access (Route Middleware Configuration)', () => {
  const jwt = require('jsonwebtoken');
  const request = require('supertest');
  const app = require('../../src/app');

  const makeToken = (roles) =>
    jwt.sign({ sub: 'test@test.com', roles }, process.env.JWT_SECRET, { expiresIn: '1h' });

  const manageToken = makeToken(['ROLE_MANAGE']);
  const adminToken = makeToken(['ROLE_ADMIN']);
  const readToken = makeToken(['ROLE_READ']);

  // Store read endpoints require ROLE_MANAGE
  test('GET /stores returns 403 for ROLE_READ', async () => {
    const res = await request(app)
      .get('/stores')
      .set('Authorization', `Bearer ${readToken}`);
    expect(res.status).toBe(403);
  });

  test('GET /stores returns non-403 for ROLE_MANAGE', async () => {
    storeRepository.findAll.mockResolvedValue([]);
    storeRepository.count.mockResolvedValue({ total: '0' });
    const res = await request(app)
      .get('/stores')
      .set('Authorization', `Bearer ${manageToken}`);
    expect(res.status).not.toBe(403);
  });

  // Store write endpoints require ROLE_ADMIN
  test('POST /stores returns 403 for ROLE_MANAGE', async () => {
    const res = await request(app)
      .post('/stores')
      .set('Authorization', `Bearer ${manageToken}`)
      .send({ manager_staff_id: 1, address_id: 1 });
    expect(res.status).toBe(403);
  });

  test('POST /stores returns non-403 for ROLE_ADMIN', async () => {
    storeRepository.create.mockResolvedValue([{ store_id: 1, manager_staff_id: 1, address_id: 1 }]);
    const res = await request(app)
      .post('/stores')
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ manager_staff_id: 1, address_id: 1 });
    expect(res.status).not.toBe(403);
  });

  // Staff read endpoints require ROLE_MANAGE
  test('GET /staffs returns 403 for ROLE_READ', async () => {
    const res = await request(app)
      .get('/staffs')
      .set('Authorization', `Bearer ${readToken}`);
    expect(res.status).toBe(403);
  });

  test('GET /staffs returns non-403 for ROLE_MANAGE', async () => {
    staffRepository.findAll.mockResolvedValue([]);
    staffRepository.count.mockResolvedValue({ total: '0' });
    const res = await request(app)
      .get('/staffs')
      .set('Authorization', `Bearer ${manageToken}`);
    expect(res.status).not.toBe(403);
  });

  // Staff write endpoints require ROLE_ADMIN
  test('POST /staffs returns 403 for ROLE_MANAGE', async () => {
    const res = await request(app)
      .post('/staffs')
      .set('Authorization', `Bearer ${manageToken}`)
      .send({ first_name: 'A', last_name: 'B', store_id: 1 });
    expect(res.status).toBe(403);
  });

  test('POST /staffs returns non-403 for ROLE_ADMIN', async () => {
    staffRepository.create.mockResolvedValue([{ staff_id: 1, first_name: 'A', last_name: 'B', store_id: 1 }]);
    const res = await request(app)
      .post('/staffs')
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ first_name: 'A', last_name: 'B', store_id: 1 });
    expect(res.status).not.toBe(403);
  });

  // Report endpoints require ROLE_MANAGE
  test('GET /reports/sales/categories returns 403 for ROLE_READ', async () => {
    const res = await request(app)
      .get('/reports/sales/categories')
      .set('Authorization', `Bearer ${readToken}`);
    expect(res.status).toBe(403);
  });

  test('GET /reports/sales/categories returns non-403 for ROLE_MANAGE', async () => {
    db.raw.mockResolvedValue({ rows: [] });
    const res = await request(app)
      .get('/reports/sales/categories')
      .set('Authorization', `Bearer ${manageToken}`);
    expect(res.status).not.toBe(403);
  });
});
