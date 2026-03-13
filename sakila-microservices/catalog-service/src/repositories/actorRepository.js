const db = require('../db');

const ACTOR_TABLE = 'catalog_schema.actor';
const FILM_ACTOR_TABLE = 'catalog_schema.film_actor';
const FILM_TABLE = 'catalog_schema.film';

const findAll = ({ page = 1, size = 20, sort = 'actor_id' }) => {
  const offset = (page - 1) * size;
  return db(ACTOR_TABLE).orderBy(sort).limit(size).offset(offset);
};

const count = () => {
  return db(ACTOR_TABLE).count('* as total').first();
};

const findById = (actorId) => {
  return db(ACTOR_TABLE).where({ actor_id: actorId }).first();
};

const findByIdWithDetails = (actorId) => {
  return db(ACTOR_TABLE).where({ actor_id: actorId }).first();
};

const findFilmsByActorId = (actorId) => {
  return db(FILM_ACTOR_TABLE)
    .join(FILM_TABLE, `${FILM_ACTOR_TABLE}.film_id`, `${FILM_TABLE}.film_id`)
    .where({ [`${FILM_ACTOR_TABLE}.actor_id`]: actorId })
    .select(`${FILM_TABLE}.*`);
};

const addFilmToActor = (actorId, filmId) => {
  return db(FILM_ACTOR_TABLE).insert({ actor_id: actorId, film_id: filmId }).returning('*');
};

const removeFilmFromActor = (actorId, filmId) => {
  return db(FILM_ACTOR_TABLE).where({ actor_id: actorId, film_id: filmId }).del();
};

const search = (query) => {
  return db(ACTOR_TABLE)
    .whereRaw('LOWER(first_name) LIKE ?', [`%${query.toLowerCase()}%`])
    .orWhereRaw('LOWER(last_name) LIKE ?', [`%${query.toLowerCase()}%`]);
};

const create = (data) => {
  return db(ACTOR_TABLE).insert(data).returning('*');
};

const update = (actorId, data) => {
  return db(ACTOR_TABLE).where({ actor_id: actorId }).update(data).returning('*');
};

const remove = (actorId) => {
  return db(ACTOR_TABLE).where({ actor_id: actorId }).del();
};

module.exports = {
  findAll, count, findById, findByIdWithDetails,
  findFilmsByActorId, addFilmToActor, removeFilmFromActor,
  search, create, update, remove,
};
