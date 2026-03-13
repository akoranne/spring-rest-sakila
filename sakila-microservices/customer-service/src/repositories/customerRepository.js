const db = require('../db');

const TABLE = 'customer_schema.customer';

const findAll = ({ page = 1, size = 20, sort = 'customer_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => {
  return db(TABLE).count('* as total').first();
};

const findById = (customerId) => {
  return db(TABLE).where({ customer_id: customerId }).first();
};

const create = (data) => {
  return db(TABLE).insert(data).returning('*');
};

const update = (customerId, data) => {
  return db(TABLE).where({ customer_id: customerId }).update(data).returning('*');
};

const remove = (customerId) => {
  return db(TABLE).where({ customer_id: customerId }).del();
};

module.exports = { findAll, count, findById, create, update, remove };
