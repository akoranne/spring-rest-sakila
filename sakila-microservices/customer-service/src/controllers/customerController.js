const customerService = require('../services/customerService');
const { createCustomerSchema, updateCustomerSchema } = require('../validators/customerValidator');

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
    const sort = req.query.sort || 'customer_id';
    const result = await customerService.findAll({ page, size, sort });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const customer = await customerService.findById(parseInt(req.params.customerId, 10));
    res.json(customer);
  } catch (err) {
    next(err);
  }
};

const findDetails = async (req, res, next) => {
  try {
    const customerId = parseInt(req.params.customerId, 10);
    const authHeader = req.headers.authorization;
    const authToken = authHeader && authHeader.startsWith('Bearer ')
      ? authHeader.slice(7)
      : authHeader;
    const customer = await customerService.findByIdWithDetails(customerId, {
      correlationId: req.correlationId,
      authToken,
    });
    res.json(customer);
  } catch (err) {
    next(err);
  }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createCustomerSchema, req.body);
    const customer = await customerService.create(data);
    res.status(201).json(customer);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateCustomerSchema, req.body);
    const customer = await customerService.update(parseInt(req.params.customerId, 10), data);
    res.json(customer);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await customerService.remove(parseInt(req.params.customerId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, findDetails, create, update, remove };
