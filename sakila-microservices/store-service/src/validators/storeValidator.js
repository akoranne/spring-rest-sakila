const Joi = require('joi');

const createStoreSchema = Joi.object({
  manager_staff_id: Joi.number().integer().positive().required(),
  address_id: Joi.number().integer().positive().required(),
});

const updateStoreSchema = Joi.object({
  manager_staff_id: Joi.number().integer().positive(),
  address_id: Joi.number().integer().positive(),
}).min(1);

module.exports = { createStoreSchema, updateStoreSchema };
