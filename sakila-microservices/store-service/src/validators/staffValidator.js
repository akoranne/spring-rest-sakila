const Joi = require('joi');

const createStaffSchema = Joi.object({
  first_name: Joi.string().max(45).required(),
  last_name: Joi.string().max(45).required(),
  address_id: Joi.number().integer().positive().allow(null),
  store_id: Joi.number().integer().positive().required(),
  active: Joi.boolean().default(true),
  username: Joi.string().max(16).allow(null, ''),
  authority_id: Joi.number().integer().positive().allow(null),
});

const updateStaffSchema = Joi.object({
  first_name: Joi.string().max(45),
  last_name: Joi.string().max(45),
  address_id: Joi.number().integer().positive().allow(null),
  store_id: Joi.number().integer().positive(),
  active: Joi.boolean(),
  username: Joi.string().max(16).allow(null, ''),
  authority_id: Joi.number().integer().positive().allow(null),
}).min(1);

module.exports = { createStaffSchema, updateStaffSchema };
