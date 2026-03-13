const db = require('../db');

const TABLE = 'store_schema.inventory';

const findAll = ({ page = 1, size = 20, sort = 'inventory_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => db(TABLE).count('* as total').first();

const findById = (inventoryId) => db(TABLE).where({ inventory_id: inventoryId }).first();

const findByStoreId = (storeId) => db(TABLE).where({ store_id: storeId });

const create = (data) => db(TABLE).insert(data).returning('*');

const update = (inventoryId, data) => db(TABLE).where({ inventory_id: inventoryId }).update(data).returning('*');

const remove = (inventoryId) => db(TABLE).where({ inventory_id: inventoryId }).del();

module.exports = { findAll, count, findById, findByStoreId, create, update, remove };
