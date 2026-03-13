const db = require('../db');

const TABLE = 'store_schema.staff';
const STORE_TABLE = 'store_schema.store';

const findAll = ({ page = 1, size = 20, sort = 'staff_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => db(TABLE).count('* as total').first();

const findById = (staffId) => db(TABLE).where({ staff_id: staffId }).first();

const findByIdWithDetails = async (staffId) => {
  const staff = await db(TABLE).where({ [`${TABLE}.staff_id`]: staffId }).first();
  if (!staff) return null;
  const store = await db(STORE_TABLE)
    .where({ store_id: staff.store_id })
    .select('store_id', 'manager_staff_id', 'address_id')
    .first();
  staff.store = store || null;
  return staff;
};

const findByStoreId = (storeId) => db(TABLE).where({ store_id: storeId });

const findByStoreIdAndStaffId = (storeId, staffId) =>
  db(TABLE).where({ store_id: storeId, staff_id: staffId }).first();

const create = (data) => db(TABLE).insert(data).returning('*');

const update = (staffId, data) => db(TABLE).where({ staff_id: staffId }).update(data).returning('*');

const remove = (staffId) => db(TABLE).where({ staff_id: staffId }).del();

module.exports = { findAll, count, findById, findByIdWithDetails, findByStoreId, findByStoreIdAndStaffId, create, update, remove };
