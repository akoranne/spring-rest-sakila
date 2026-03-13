const db = require('../db');

const TABLE = 'location_schema.address';

const findAll = ({ page = 1, size = 20, sort = 'address_id' }) => {
  const offset = (page - 1) * size;
  return db(TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => {
  return db(TABLE).count('* as total').first();
};

const findById = (addressId) => {
  return db(TABLE).where({ address_id: addressId }).first();
};

const findByIdWithDetails = (addressId) => {
  return db(TABLE)
    .join('location_schema.city', 'location_schema.address.city_id', 'location_schema.city.city_id')
    .join('location_schema.country', 'location_schema.city.country_id', 'location_schema.country.country_id')
    .where({ 'location_schema.address.address_id': addressId })
    .select(
      'location_schema.address.*',
      'location_schema.city.city',
      'location_schema.country.country'
    )
    .first();
};

const create = (data) => {
  return db(TABLE).insert(data).returning('*');
};

const update = (addressId, data) => {
  return db(TABLE).where({ address_id: addressId }).update(data).returning('*');
};

const remove = (addressId) => {
  return db(TABLE).where({ address_id: addressId }).del();
};

module.exports = { findAll, count, findById, findByIdWithDetails, create, update, remove };
