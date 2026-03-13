const db = require('../db');

const RENTAL_TABLE = 'rental_schema.rental';

const findAll = ({ page = 1, size = 20, sort = 'rental_id', customerId }) => {
  const offset = (page - 1) * size;
  let query = db(RENTAL_TABLE);

  if (customerId) {
    query = query.where('customer_id', customerId);
  }

  return query.orderBy(sort).limit(size).offset(offset);
};

const count = ({ customerId } = {}) => {
  let query = db(RENTAL_TABLE);

  if (customerId) {
    query = query.where('customer_id', customerId);
  }

  return query.count('* as total').first();
};

const findById = (rentalId) => {
  return db(RENTAL_TABLE).where({ rental_id: rentalId }).first();
};

const create = (data) => {
  return db(RENTAL_TABLE).insert(data).returning('*');
};

const update = (rentalId, data) => {
  return db(RENTAL_TABLE).where({ rental_id: rentalId }).update(data).returning('*');
};

const remove = (rentalId) => {
  return db(RENTAL_TABLE).where({ rental_id: rentalId }).del();
};

module.exports = { findAll, count, findById, create, update, remove };
