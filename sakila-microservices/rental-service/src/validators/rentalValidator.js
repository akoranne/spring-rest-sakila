const Joi = require('joi');

const createRentalSchema = Joi.object({
  inventory_id: Joi.number().integer().required(),
  customer_id: Joi.number().integer().required(),
  staff_id: Joi.number().integer().required(),
  rental_date: Joi.date().optional(),
  return_date: Joi.date().allow(null).optional(),
});

const updateRentalSchema = Joi.object({
  inventory_id: Joi.number().integer(),
  customer_id: Joi.number().integer(),
  staff_id: Joi.number().integer(),
  rental_date: Joi.date(),
  return_date: Joi.date().allow(null),
}).min(1);

const returnRentalSchema = Joi.object({
  rental_id: Joi.number().integer().required(),
  return_date: Joi.date().optional(),
});

module.exports = { createRentalSchema, updateRentalSchema, returnRentalSchema };
