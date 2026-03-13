const db = require('../db');

const FILM_TABLE = 'catalog_schema.film';
const FILM_ACTOR_TABLE = 'catalog_schema.film_actor';
const ACTOR_TABLE = 'catalog_schema.actor';
const FILM_CATEGORY_TABLE = 'catalog_schema.film_category';
const CATEGORY_TABLE = 'catalog_schema.category';
const LANGUAGE_TABLE = 'catalog_schema.language';
const FILM_TEXT_TABLE = 'catalog_schema.film_text';

const findAll = ({ page = 1, size = 20, sort = 'film_id', category, release_year, rating }) => {
  const offset = (page - 1) * size;
  let query = db(FILM_TABLE);

  if (category) {
    query = query
      .join(FILM_CATEGORY_TABLE, `${FILM_TABLE}.film_id`, `${FILM_CATEGORY_TABLE}.film_id`)
      .join(CATEGORY_TABLE, `${FILM_CATEGORY_TABLE}.category_id`, `${CATEGORY_TABLE}.category_id`)
      .where(`${CATEGORY_TABLE}.name`, category)
      .select(`${FILM_TABLE}.*`);
  }

  if (release_year) {
    query = query.where(`${FILM_TABLE}.release_year`, release_year);
  }

  if (rating) {
    query = query.where(`${FILM_TABLE}.rating`, rating);
  }

  return query.orderBy(sort).limit(size).offset(offset);
};

const count = ({ category, release_year, rating } = {}) => {
  let query = db(FILM_TABLE);

  if (category) {
    query = query
      .join(FILM_CATEGORY_TABLE, `${FILM_TABLE}.film_id`, `${FILM_CATEGORY_TABLE}.film_id`)
      .join(CATEGORY_TABLE, `${FILM_CATEGORY_TABLE}.category_id`, `${CATEGORY_TABLE}.category_id`)
      .where(`${CATEGORY_TABLE}.name`, category);
  }

  if (release_year) {
    query = query.where(`${FILM_TABLE}.release_year`, release_year);
  }

  if (rating) {
    query = query.where(`${FILM_TABLE}.rating`, rating);
  }

  return query.count('* as total').first();
};

const findById = (filmId) => {
  return db(FILM_TABLE).where({ film_id: filmId }).first();
};

const findByIdWithDetails = (filmId) => {
  return db(FILM_TABLE)
    .join(LANGUAGE_TABLE, `${FILM_TABLE}.language_id`, `${LANGUAGE_TABLE}.language_id`)
    .where({ [`${FILM_TABLE}.film_id`]: filmId })
    .select(`${FILM_TABLE}.*`, `${LANGUAGE_TABLE}.name as language`)
    .first();
};

const findActorsByFilmId = (filmId) => {
  return db(FILM_ACTOR_TABLE)
    .join(ACTOR_TABLE, `${FILM_ACTOR_TABLE}.actor_id`, `${ACTOR_TABLE}.actor_id`)
    .where({ [`${FILM_ACTOR_TABLE}.film_id`]: filmId })
    .select(`${ACTOR_TABLE}.*`);
};

const findActorByFilmIdAndActorId = (filmId, actorId) => {
  return db(FILM_ACTOR_TABLE)
    .join(ACTOR_TABLE, `${FILM_ACTOR_TABLE}.actor_id`, `${ACTOR_TABLE}.actor_id`)
    .where({ [`${FILM_ACTOR_TABLE}.film_id`]: filmId, [`${FILM_ACTOR_TABLE}.actor_id`]: actorId })
    .select(`${ACTOR_TABLE}.*`)
    .first();
};

const create = (data) => {
  return db(FILM_TABLE).insert(data).returning('*');
};

const update = (filmId, data) => {
  return db(FILM_TABLE).where({ film_id: filmId }).update(data).returning('*');
};

const remove = (filmId) => {
  return db(FILM_TABLE).where({ film_id: filmId }).del();
};

const fullTextSearch = (searchTerm, { page = 1, size = 20 }) => {
  const offset = (page - 1) * size;
  return db(FILM_TEXT_TABLE)
    .whereRaw(`fulltext_search @@ plainto_tsquery('english', ?)`, [searchTerm])
    .limit(size)
    .offset(offset);
};

const fullTextSearchCount = (searchTerm) => {
  return db(FILM_TEXT_TABLE)
    .whereRaw(`fulltext_search @@ plainto_tsquery('english', ?)`, [searchTerm])
    .count('* as total')
    .first();
};

module.exports = {
  findAll, count, findById, findByIdWithDetails,
  findActorsByFilmId, findActorByFilmIdAndActorId,
  create, update, remove,
  fullTextSearch, fullTextSearchCount,
};
