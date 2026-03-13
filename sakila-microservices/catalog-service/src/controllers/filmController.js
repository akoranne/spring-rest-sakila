const filmService = require('../services/filmService');
const { createFilmSchema, updateFilmSchema } = require('../validators/filmValidator');

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
    const sort = req.query.sort || 'film_id';
    const { category, release_year, rating } = req.query;
    const result = await filmService.findAll({
      page, size, sort, category,
      release_year: release_year ? parseInt(release_year, 10) : undefined,
      rating,
    });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const film = await filmService.findById(parseInt(req.params.filmId, 10));
    res.json(film);
  } catch (err) {
    next(err);
  }
};

const findDetails = async (req, res, next) => {
  try {
    const film = await filmService.findByIdWithDetails(parseInt(req.params.filmId, 10));
    res.json(film);
  } catch (err) {
    next(err);
  }
};

const findActors = async (req, res, next) => {
  try {
    const actors = await filmService.findActorsByFilmId(parseInt(req.params.filmId, 10));
    res.json(actors);
  } catch (err) {
    next(err);
  }
};

const findActorById = async (req, res, next) => {
  try {
    const actor = await filmService.findActorByFilmIdAndActorId(
      parseInt(req.params.filmId, 10),
      parseInt(req.params.actorId, 10)
    );
    res.json(actor);
  } catch (err) {
    next(err);
  }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createFilmSchema, req.body);
    const film = await filmService.create(data);
    res.status(201).json(film);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateFilmSchema, req.body);
    const film = await filmService.update(parseInt(req.params.filmId, 10), data);
    res.json(film);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await filmService.remove(parseInt(req.params.filmId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, findDetails, findActors, findActorById, create, update, remove };
