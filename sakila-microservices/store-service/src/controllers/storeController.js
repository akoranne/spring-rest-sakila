const storeService = require('../services/storeService');
const staffService = require('../services/staffService');
const { createStoreSchema, updateStoreSchema } = require('../validators/storeValidator');

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
    const sort = req.query.sort || 'store_id';
    const result = await storeService.findAll({ page, size, sort });
    res.json(result);
  } catch (err) { next(err); }
};

const findById = async (req, res, next) => {
  try {
    const store = await storeService.findById(parseInt(req.params.storeId, 10));
    res.json(store);
  } catch (err) { next(err); }
};

const findDetails = async (req, res, next) => {
  try {
    const store = await storeService.findByIdWithDetails(parseInt(req.params.storeId, 10));
    res.json(store);
  } catch (err) { next(err); }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createStoreSchema, req.body);
    const store = await storeService.create(data);
    res.status(201).json(store);
  } catch (err) { next(err); }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateStoreSchema, req.body);
    const store = await storeService.update(parseInt(req.params.storeId, 10), data);
    res.json(store);
  } catch (err) { next(err); }
};

const remove = async (req, res, next) => {
  try {
    await storeService.remove(parseInt(req.params.storeId, 10));
    res.status(204).send();
  } catch (err) { next(err); }
};

const findStoreStaff = async (req, res, next) => {
  try {
    const storeId = parseInt(req.params.storeId, 10);
    await storeService.findById(storeId);
    const staff = await staffService.findByStoreId(storeId);
    res.json(staff);
  } catch (err) { next(err); }
};

const findStoreStaffById = async (req, res, next) => {
  try {
    const storeId = parseInt(req.params.storeId, 10);
    const staffId = parseInt(req.params.staffId, 10);
    const staff = await staffService.findByStoreIdAndStaffId(storeId, staffId);
    res.json(staff);
  } catch (err) { next(err); }
};

const assignStoreStaff = async (req, res, next) => {
  try {
    const storeId = parseInt(req.params.storeId, 10);
    const staffId = parseInt(req.params.staffId, 10);
    await storeService.findById(storeId);
    const staff = await staffService.update(staffId, { store_id: storeId });
    res.json(staff);
  } catch (err) { next(err); }
};

const updateStoreStaff = async (req, res, next) => {
  try {
    const staffId = parseInt(req.params.staffId, 10);
    const data = validate(require('../validators/staffValidator').updateStaffSchema, req.body);
    const staff = await staffService.update(staffId, data);
    res.json(staff);
  } catch (err) { next(err); }
};

const removeStoreStaff = async (req, res, next) => {
  try {
    const staffId = parseInt(req.params.staffId, 10);
    await staffService.remove(staffId);
    res.status(204).send();
  } catch (err) { next(err); }
};

module.exports = { findAll, findById, findDetails, create, update, remove, findStoreStaff, findStoreStaffById, assignStoreStaff, updateStoreStaff, removeStoreStaff };
