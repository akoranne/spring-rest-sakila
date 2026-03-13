const inventoryRepository = require('../repositories/inventoryRepository');

const findAll = async ({ page, size, sort }) => {
  const [items, countResult] = await Promise.all([
    inventoryRepository.findAll({ page, size, sort }),
    inventoryRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: items, total, page, size };
};

const findById = async (inventoryId) => {
  const item = await inventoryRepository.findById(inventoryId);
  if (!item) {
    const err = new Error(`Inventory with id ${inventoryId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return item;
};

const findByStoreId = async (storeId) => {
  return inventoryRepository.findByStoreId(storeId);
};

module.exports = { findAll, findById, findByStoreId };
