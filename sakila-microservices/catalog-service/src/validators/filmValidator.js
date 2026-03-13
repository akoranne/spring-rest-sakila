const Joi = require('joi');

const RATINGS = ['G', 'PG', 'PG-13', 'R', 'NC-17'];

const createFilmSchema = Joi.object({
  title: Joi.string().max(128).required(),
  description: Joi.string().allow(null, ''),
  release_year: Joi.number().integer().min(1900).max(2100).allow(null),
  language_id: Joi.number().integer().positive().required(),
  original_language_id: Joi.number().integer().positive().allow(null),
  rental_duration: Joi.number().integer().positive().allow(null),
  rental_rate: Joi.number().positive().precision(2).allow(null),
  length: Joi.number().integer().positive().allow(null),
  replacement_cost: Joi.number().positive().precision(2).allow(null),
  rating: Joi.string().valid(...RATINGS).allow(null),
  special_features: Joi.array().items(Joi.string()).allow(null),
});

const updateFilmSchema = Joi.object({
  title: Joi.string().max(128),
  description: Joi.string().allow(null, ''),
  release_year: Joi.number().integer().min(1900).max(2100).allow(null),
  language_id: Joi.number().integer().positive(),
  original_language_id: Joi.number().integer().positive().allow(null),
  rental_duration: Joi.number().integer().positive().allow(null),
  rental_rate: Joi.number().positive().precision(2).allow(null),
  length: Joi.number().integer().positive().allow(null),
  replacement_cost: Joi.number().positive().precision(2).allow(null),
  rating: Joi.string().valid(...RATINGS).allow(null),
  special_features: Joi.array().items(Joi.string()).allow(null),
}).min(1);

module.exports = { createFilmSchema, updateFilmSchema, RATINGS };
