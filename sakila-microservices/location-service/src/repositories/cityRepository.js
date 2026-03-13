const db = require('../db');

const TABLE = 'location_schema.city';

const findAll = ({ page = 1, size = 20, sort = 'city_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => {
  return db(TABLE).count('* as total').first();
};

const findById = (cityId) => {
  return db(TABLE).where({ city_id: cityId }).first();
};

const create = (data) => {
  return db(TABLE).insert(data).returning('*');
};

const update = (cityId, data) => {
  return db(TABLE).where({ city_id: cityId }).update(data).returning('*');
};

const remove = (cityId) => {
  return db(TABLE).where({ city_id: cityId }).del();
};

module.exports = { findAll, count, findById, create, update, remove };
