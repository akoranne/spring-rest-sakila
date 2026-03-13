const cityService = require('../services/cityService');
const { createCitySchema, updateCitySchema } = require('../validators/cityValidator');

const validate = (schema, body) => {
  const { error, value } = schema.validate(body, { abortEarly: false });
  if (error) {
    const details = error.details.map((d) => ({
      field: d.context.key,
      message: d.message,
    }));
    const err = new Error('Request validation failed');
    err.statusCode = 400;
    err.code = 'VALIDATION_ERROR';
    err.details = details;
    throw err;
  }
  return value;
};

const findAll = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page, 10) || 1;
    const size = parseInt(req.query.size, 10) || 20;
    const sort = req.query.sort || 'city_id';
    const result = await cityService.findAll({ page, size, sort });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const city = await cityService.findById(parseInt(req.params.cityId, 10));
    res.json(city);
  } catch (err) {
    next(err);
  }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createCitySchema, req.body);
    const city = await cityService.create(data);
    res.status(201).json(city);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateCitySchema, req.body);
    const city = await cityService.update(parseInt(req.params.cityId, 10), data);
    res.json(city);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await cityService.remove(parseInt(req.params.cityId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, create, update, remove };
