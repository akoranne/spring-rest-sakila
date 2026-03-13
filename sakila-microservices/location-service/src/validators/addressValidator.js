const Joi = require('joi');

const createAddressSchema = Joi.object({
  address: Joi.string().max(50).required(),
  address2: Joi.string().max(50).allow(null, ''),
  district: Joi.string().max(20).required(),
  city_id: Joi.number().integer().positive().required(),
  postal_code: Joi.string().max(10).allow(null, ''),
  phone: Joi.string().max(20).allow(null, ''),
});

const updateAddressSchema = Joi.object({
  address: Joi.string().max(50),
  address2: Joi.string().max(50).allow(null, ''),
  district: Joi.string().max(20),
  city_id: Joi.number().integer().positive(),
  postal_code: Joi.string().max(10).allow(null, ''),
  phone: Joi.string().max(20).allow(null, ''),
}).min(1);

module.exports = { createAddressSchema, updateAddressSchema };
