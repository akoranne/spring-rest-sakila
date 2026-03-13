const staffRepository = require('../repositories/staffRepository');

const findAll = async ({ page, size, sort }) => {
  const [staffs, countResult] = await Promise.all([
    staffRepository.findAll({ page, size, sort }),
    staffRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: staffs, total, page, size };
};

const findById = async (staffId) => {
  const staff = await staffRepository.findById(staffId);
  if (!staff) {
    const err = new Error(`Staff with id ${staffId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return staff;
};

const findByIdWithDetails = async (staffId) => {
  const staff = await staffRepository.findByIdWithDetails(staffId);
  if (!staff) {
    const err = new Error(`Staff with id ${staffId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return staff;
};

const findByStoreId = async (storeId) => {
  return staffRepository.findByStoreId(storeId);
};

const findByStoreIdAndStaffId = async (storeId, staffId) => {
  const staff = await staffRepository.findByStoreIdAndStaffId(storeId, staffId);
  if (!staff) {
    const err = new Error(`Staff with id ${staffId} not found in store ${storeId}`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return staff;
};

const create = async (data) => {
  const [staff] = await staffRepository.create(data);
  return staff;
};

const update = async (staffId, data) => {
  await findById(staffId);
  const [staff] = await staffRepository.update(staffId, data);
  return staff;
};

const remove = async (staffId) => {
  await findById(staffId);
  await staffRepository.remove(staffId);
};

module.exports = { findAll, findById, findByIdWithDetails, findByStoreId, findByStoreIdAndStaffId, create, update, remove };
