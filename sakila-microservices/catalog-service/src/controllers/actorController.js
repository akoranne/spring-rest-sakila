const actorService = require('../services/actorService');
const { createActorSchema, updateActorSchema, searchActorSchema } = require('../validators/actorValidator');

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
    const sort = req.query.sort || 'actor_id';
    const result = await actorService.findAll({ page, size, sort });
    res.json(result);
  } catch (err) {
    next(err);
  }
};

const findById = async (req, res, next) => {
  try {
    const actor = await actorService.findById(parseInt(req.params.actorId, 10));
    res.json(actor);
  } catch (err) {
    next(err);
  }
};

const findDetails = async (req, res, next) => {
  try {
    const actor = await actorService.findByIdWithDetails(parseInt(req.params.actorId, 10));
    res.json(actor);
  } catch (err) {
    next(err);
  }
};

const findFilms = async (req, res, next) => {
  try {
    const films = await actorService.findFilmsByActorId(parseInt(req.params.actorId, 10));
    res.json(films);
  } catch (err) {
    next(err);
  }
};

const addFilm = async (req, res, next) => {
  try {
    const result = await actorService.addFilmToActor(
      parseInt(req.params.actorId, 10),
      parseInt(req.params.filmId, 10)
    );
    res.status(201).json(result);
  } catch (err) {
    next(err);
  }
};

const removeFilm = async (req, res, next) => {
  try {
    await actorService.removeFilmFromActor(
      parseInt(req.params.actorId, 10),
      parseInt(req.params.filmId, 10)
    );
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

const search = async (req, res, next) => {
  try {
    const data = validate(searchActorSchema, req.body);
    const actors = await actorService.search(data.query);
    res.json(actors);
  } catch (err) {
    next(err);
  }
};

const create = async (req, res, next) => {
  try {
    const data = validate(createActorSchema, req.body);
    const actor = await actorService.create(data);
    res.status(201).json(actor);
  } catch (err) {
    next(err);
  }
};

const update = async (req, res, next) => {
  try {
    const data = validate(updateActorSchema, req.body);
    const actor = await actorService.update(parseInt(req.params.actorId, 10), data);
    res.json(actor);
  } catch (err) {
    next(err);
  }
};

const remove = async (req, res, next) => {
  try {
    await actorService.remove(parseInt(req.params.actorId, 10));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = { findAll, findById, findDetails, findFilms, addFilm, removeFilm, search, create, update, remove };
