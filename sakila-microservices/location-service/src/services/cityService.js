const cityRepository = require('../repositories/cityRepository');

const findAll = async ({ page, size, sort }) => {
  const [cities, countResult] = await Promise.all([
    cityRepository.findAll({ page, size, sort }),
    cityRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: cities, total, page, size };
};

const findById = async (cityId) => {
  const city = await cityRepository.findById(cityId);
  if (!city) {
    const err = new Error(`City with id ${cityId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return city;
};

const create = async (data) => {
  const [city] = await cityRepository.create(data);
  return city;
};

const update = async (cityId, data) => {
  await findById(cityId);
  const [city] = await cityRepository.update(cityId, data);
  return city;
};

const remove = async (cityId) => {
  await findById(cityId);
  await cityRepository.remove(cityId);
};

module.exports = { findAll, findById, create, update, remove };
