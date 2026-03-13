const addressRepository = require('../repositories/addressRepository');

const findAll = async ({ page, size, sort }) => {
  const [addresses, countResult] = await Promise.all([
    addressRepository.findAll({ page, size, sort }),
    addressRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: addresses, total, page, size };
};

const findById = async (addressId) => {
  const address = await addressRepository.findById(addressId);
  if (!address) {
    const err = new Error(`Address with id ${addressId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return address;
};

const findByIdWithDetails = async (addressId) => {
  const address = await addressRepository.findByIdWithDetails(addressId);
  if (!address) {
    const err = new Error(`Address with id ${addressId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return address;
};

const create = async (data) => {
  const [address] = await addressRepository.create(data);
  return address;
};

const update = async (addressId, data) => {
  await findById(addressId);
  const [address] = await addressRepository.update(addressId, data);
  return address;
};

const remove = async (addressId) => {
  await findById(addressId);
  await addressRepository.remove(addressId);
};

module.exports = { findAll, findById, findByIdWithDetails, create, update, remove };
