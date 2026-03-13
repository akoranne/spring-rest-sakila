const Joi = require('joi');

const createCitySchema = Joi.object({
  city: Joi.string().max(50).required(),
  country_id: Joi.number().integer().positive().required(),
});

const updateCitySchema = Joi.object({
  city: Joi.string().max(50),
  country_id: Joi.number().integer().positive(),
}).min(1);

module.exports = { createCitySchema, updateCitySchema };
