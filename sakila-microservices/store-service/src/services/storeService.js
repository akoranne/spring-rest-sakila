const storeRepository = require('../repositories/storeRepository');

const findAll = async ({ page, size, sort }) => {
  const [stores, countResult] = await Promise.all([
    storeRepository.findAll({ page, size, sort }),
    storeRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: stores, total, page, size };
};

const findById = async (storeId) => {
  const store = await storeRepository.findById(storeId);
  if (!store) {
    const err = new Error(`Store with id ${storeId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return store;
};

const findByIdWithDetails = async (storeId) => {
  const store = await storeRepository.findByIdWithDetails(storeId);
  if (!store) {
    const err = new Error(`Store with id ${storeId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return store;
};

const create = async (data) => {
  const [store] = await storeRepository.create(data);
  return store;
};

const update = async (storeId, data) => {
  await findById(storeId);
  const [store] = await storeRepository.update(storeId, data);
  return store;
};

const remove = async (storeId) => {
  await findById(storeId);
  await storeRepository.remove(storeId);
};

module.exports = { findAll, findById, findByIdWithDetails, create, update, remove };
