const addressService = require('../services/addressService');
const { createAddressSchema, updateAddressSchema } = require('../validators/addressValidator');

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
    const sort = req.query.sort || 'address_id';
    const result = await addressService.findAll({ page, size, sort });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const address = await addressService.findById(parseInt(req.params.addressId, 10));
    res.json(address);
  } catch (err) {
    next(err);
  }
};

const findDetails = async (req, res, next) => {
  try {
    const address = await addressService.findByIdWithDetails(parseInt(req.params.addressId, 10));
    res.json(address);
  } catch (err) {
    next(err);
  }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createAddressSchema, req.body);
    const address = await addressService.create(data);
    res.status(201).json(address);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateAddressSchema, req.body);
    const address = await addressService.update(parseInt(req.params.addressId, 10), data);
    res.json(address);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await addressService.remove(parseInt(req.params.addressId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, findDetails, create, update, remove };
