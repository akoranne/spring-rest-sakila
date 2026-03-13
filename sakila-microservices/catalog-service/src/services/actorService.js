const actorRepository = require('../repositories/actorRepository');

const findAll = async ({ page, size, sort }) => {
  const [actors, countResult] = await Promise.all([
    actorRepository.findAll({ page, size, sort }),
    actorRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: actors, total, page, size };
};

const findById = async (actorId) => {
  const actor = await actorRepository.findById(actorId);
  if (!actor) {
    const err = new Error(`Actor with id ${actorId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return actor;
};

const findByIdWithDetails = async (actorId) => {
  const actor = await actorRepository.findByIdWithDetails(actorId);
  if (!actor) {
    const err = new Error(`Actor with id ${actorId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return actor;
};

const findFilmsByActorId = async (actorId) => {
  await findById(actorId);
  return actorRepository.findFilmsByActorId(actorId);
};

const addFilmToActor = async (actorId, filmId) => {
  await findById(actorId);
  const [result] = await actorRepository.addFilmToActor(actorId, filmId);
  return result;
};

const removeFilmFromActor = async (actorId, filmId) => {
  await findById(actorId);
  const deleted = await actorRepository.removeFilmFromActor(actorId, filmId);
  if (!deleted) {
    const err = new Error(`Film ${filmId} not associated with actor ${actorId}`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
};

const search = async (query) => {
  return actorRepository.search(query);
};

const create = async (data) => {
  const [actor] = await actorRepository.create(data);
  return actor;
};

const update = async (actorId, data) => {
  await findById(actorId);
  const [actor] = await actorRepository.update(actorId, data);
  return actor;
};

const remove = async (actorId) => {
  await findById(actorId);
  await actorRepository.remove(actorId);
};

module.exports = {
  findAll, findById, findByIdWithDetails,
  findFilmsByActorId, addFilmToActor, removeFilmFromActor,
  search, create, update, remove,
};
