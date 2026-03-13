const filmRepository = require('../repositories/filmRepository');

const findAll = async ({ page, size, sort, category, release_year, rating }) => {
  const [films, countResult] = await Promise.all([
    filmRepository.findAll({ page, size, sort, category, release_year, rating }),
    filmRepository.count({ category, release_year, rating }),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: films, total, page, size };
};

const findById = async (filmId) => {
  const film = await filmRepository.findById(filmId);
  if (!film) {
    const err = new Error(`Film with id ${filmId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return film;
};

const findByIdWithDetails = async (filmId) => {
  const film = await filmRepository.findByIdWithDetails(filmId);
  if (!film) {
    const err = new Error(`Film with id ${filmId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return film;
};

const findActorsByFilmId = async (filmId) => {
  await findById(filmId);
  return filmRepository.findActorsByFilmId(filmId);
};

const findActorByFilmIdAndActorId = async (filmId, actorId) => {
  await findById(filmId);
  const actor = await filmRepository.findActorByFilmIdAndActorId(filmId, actorId);
  if (!actor) {
    const err = new Error(`Actor with id ${actorId} not found for film ${filmId}`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return actor;
};

const create = async (data) => {
  const [film] = await filmRepository.create(data);
  return film;
};

const update = async (filmId, data) => {
  await findById(filmId);
  const [film] = await filmRepository.update(filmId, data);
  return film;
};

const remove = async (filmId) => {
  await findById(filmId);
  await filmRepository.remove(filmId);
};

const fullTextSearch = async (searchTerm, { page, size }) => {
  const [results, countResult] = await Promise.all([
    filmRepository.fullTextSearch(searchTerm, { page, size }),
    filmRepository.fullTextSearchCount(searchTerm),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: results, total, page, size };
};

module.exports = {
  findAll, findById, findByIdWithDetails,
  findActorsByFilmId, findActorByFilmIdAndActorId,
  create, update, remove, fullTextSearch,
};
