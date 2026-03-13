const rentalService = require('../services/rentalService');
const { createRentalSchema, updateRentalSchema, returnRentalSchema } = require('../validators/rentalValidator');

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
    const sort = req.query.sort || 'rental_id';
    const customerId = req.query.customerId ? parseInt(req.query.customerId, 10) : undefined;
    const result = await rentalService.findAll({ page, size, sort, customerId });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const rental = await rentalService.findById(parseInt(req.params.rentalId, 10));
    res.json(rental);
  } catch (err) {
    next(err);
  }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createRentalSchema, req.body);
    const rental = await rentalService.create(data);
    res.status(201).json(rental);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateRentalSchema, req.body);
    const rental = await rentalService.update(parseInt(req.params.rentalId, 10), data);
    res.json(rental);
  } catch (err) {
    next(err);
  }
};

const returnRental = async (req, res, next) => {
  try {
    const data = validate(returnRentalSchema, req.body);
    const rental = await rentalService.returnRental(data);
    res.json(rental);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await rentalService.remove(parseInt(req.params.rentalId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, create, update, returnRental, remove };
