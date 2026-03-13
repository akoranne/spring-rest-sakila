const db = require('../db');

const TABLE = 'store_schema.store';

const findAll = ({ page = 1, size = 20, sort = 'store_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => db(TABLE).count('* as total').first();

const findById = (storeId) => db(TABLE).where({ store_id: storeId }).first();

const findByIdWithDetails = (storeId) => db(TABLE).where({ store_id: storeId }).first();

const create = (data) => db(TABLE).insert(data).returning('*');

const update = (storeId, data) => db(TABLE).where({ store_id: storeId }).update(data).returning('*');

const remove = (storeId) => db(TABLE).where({ store_id: storeId }).del();

module.exports = { findAll, count, findById, findByIdWithDetails, create, update, remove };
