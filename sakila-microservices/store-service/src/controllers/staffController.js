const staffService = require('../services/staffService');
const { createStaffSchema, updateStaffSchema } = require('../validators/staffValidator');

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
    const sort = req.query.sort || 'staff_id';
    const result = await staffService.findAll({ page, size, sort });
    res.json(result);
  } catch (err) { next(err); }
};

const findById = async (req, res, next) => {
  try {
    const staff = await staffService.findById(parseInt(req.params.staffId, 10));
    res.json(staff);
  } catch (err) { next(err); }
};

const findDetails = async (req, res, next) => {
  try {
    const staff = await staffService.findByIdWithDetails(parseInt(req.params.staffId, 10));
    res.json(staff);
  } catch (err) { next(err); }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createStaffSchema, req.body);
    const staff = await staffService.create(data);
    res.status(201).json(staff);
  } catch (err) { next(err); }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateStaffSchema, req.body);
    const staff = await staffService.update(parseInt(req.params.staffId, 10), data);
    res.json(staff);
  } catch (err) { next(err); }
};

const remove = async (req, res, next) => {
  try {
    await staffService.remove(parseInt(req.params.staffId, 10));
    res.status(204).send();
  } catch (err) { next(err); }
};

module.exports = { findAll, findById, findDetails, create, update, remove };
