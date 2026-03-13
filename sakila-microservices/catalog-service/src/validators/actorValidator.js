const Joi = require('joi');

const createActorSchema = Joi.object({
  first_name: Joi.string().max(45).required(),
  last_name: Joi.string().max(45).required(),
});

const updateActorSchema = Joi.object({
  first_name: Joi.string().max(45),
  last_name: Joi.string().max(45),
}).min(1);

const searchActorSchema = Joi.object({
  query: Joi.string().max(100).required(),
});

module.exports = { createActorSchema, updateActorSchema, searchActorSchema };
