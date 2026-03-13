const db = require('../db');

const PAYMENT_TABLE = 'payment_schema.payment';

const findAll = ({ page = 1, size = 20, sort = 'payment_id', customerId }) => {
  const offset = (page - 1) * size;
  let query = db(PAYMENT_TABLE);

  if (customerId) {
    query = query.where('customer_id', customerId);
  }

  return query.orderBy(sort).limit(size).offset(offset);
};

const count = ({ customerId } = {}) => {
  let query = db(PAYMENT_TABLE);

  if (customerId) {
    query = query.where('customer_id', customerId);
  }

  return query.count('* as total').first();
};

const findById = (paymentId) => {
  return db(PAYMENT_TABLE).where({ payment_id: paymentId }).first();
};

const findByIdWithDetails = (paymentId) => {
  return db(PAYMENT_TABLE).where({ payment_id: paymentId }).first();
};

const update = (paymentId, data) => {
  return db(PAYMENT_TABLE).where({ payment_id: paymentId }).update(data).returning('*');
};

const remove = (paymentId) => {
  return db(PAYMENT_TABLE).where({ payment_id: paymentId }).del();
};

module.exports = { findAll, count, findById, findByIdWithDetails, update, remove };
