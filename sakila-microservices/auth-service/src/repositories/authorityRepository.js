const db = require('../db');

const TABLE = 'auth_schema.authority';

const findByEmail = (email) => {
  return db(TABLE).where({ email }).first();
};

module.exports = { findByEmail };
