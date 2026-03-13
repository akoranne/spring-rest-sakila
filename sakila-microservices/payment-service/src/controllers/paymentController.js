const paymentService = require('../services/paymentService');
const { updatePaymentSchema } = require('../validators/paymentValidator');

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
    const sort = req.query.sort || 'payment_id';
    const customerId = req.query.customerId ? parseInt(req.query.customerId, 10) : undefined;
    const result = await paymentService.findAll({ page, size, sort, customerId });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const payment = await paymentService.findById(parseInt(req.params.paymentId, 10));
    res.json(payment);
  } catch (err) {
    next(err);
  }
};

const findDetails = async (req, res, next) => {
  try {
    const payment = await paymentService.findByIdWithDetails(parseInt(req.params.paymentId, 10));
    res.json(payment);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updatePaymentSchema, req.body);
    const payment = await paymentService.update(parseInt(req.params.paymentId, 10), data);
    res.json(payment);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await paymentService.remove(parseInt(req.params.paymentId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, findDetails, update, remove };
