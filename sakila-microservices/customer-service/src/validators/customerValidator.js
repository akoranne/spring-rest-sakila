const Joi = require('joi');

const createCustomerSchema = Joi.object({
  first_name: Joi.string().max(45).required(),
  last_name: Joi.string().max(45).required(),
  store_id: Joi.number().integer().required(),
  address_id: Joi.number().integer().required(),
  active: Joi.boolean().optional(),
  authority_id: Joi.number().integer().optional(),
});

const updateCustomerSchema = Joi.object({
  first_name: Joi.string().max(45),
  last_name: Joi.string().max(45),
  store_id: Joi.number().integer(),
  address_id: Joi.number().integer(),
  active: Joi.boolean(),
  authority_id: Joi.number().integer(),
}).min(1);

module.exports = { createCustomerSchema, updateCustomerSchema };
