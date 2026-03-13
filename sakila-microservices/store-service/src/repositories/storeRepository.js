const db = require('../db');

const TABLE = 'store_schema.store';
const STAFF_TABLE = 'store_schema.staff';
const INVENTORY_TABLE = 'store_schema.inventory';

const findAll = ({ page = 1, size = 20, sort = 'store_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => db(TABLE).count('* as total').first();

const findById = (storeId) => db(TABLE).where({ store_id: storeId }).first();

const findByIdWithDetails = async (storeId) => {
  const store = await db(TABLE)
    .where({ [`${TABLE}.store_id`]: storeId })
    .first();
  if (!store) return null;
  if (store.manager_staff_id) {
    const manager = await db(STAFF_TABLE)
      .where({ staff_id: store.manager_staff_id })
      .select('staff_id', 'first_name', 'last_name', 'username')
      .first();
    store.manager = manager || null;
  } else {
    store.manager = null;
  }
  const inventory = await db(INVENTORY_TABLE)
    .where({ store_id: storeId })
    .count('* as total')
    .first();
  store.inventory_count = parseInt(inventory.total, 10);
  return store;
};

const create = (data) => db(TABLE).insert(data).returning('*');

const update = (storeId, data) => db(TABLE).where({ store_id: storeId }).update(data).returning('*');

const remove = (storeId) => db(TABLE).where({ store_id: storeId }).del();

module.exports = { findAll, count, findById, findByIdWithDetails, create, update, remove };
