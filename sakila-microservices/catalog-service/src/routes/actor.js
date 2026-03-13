const express = require('express');
const router = express.Router();
const actorController = require('../controllers/actorController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/actors', jwtAuth, requireRole('ROLE_READ'), actorController.findAll);
router.post('/actors/search', jwtAuth, requireRole('ROLE_READ'), actorController.search);
router.get('/actors/:actorId', jwtAuth, requireRole('ROLE_READ'), actorController.findById);
router.get('/actors/:actorId/details', jwtAuth, requireRole('ROLE_READ'), actorController.findDetails);
router.get('/actors/:actorId/films', jwtAuth, requireRole('ROLE_READ'), actorController.findFilms);
router.post('/actors', jwtAuth, requireRole('ROLE_MANAGE'), actorController.create);
router.post('/actors/:actorId/films/:filmId', jwtAuth, requireRole('ROLE_MANAGE'), actorController.addFilm);
router.put('/actors/:actorId', jwtAuth, requireRole('ROLE_MANAGE'), actorController.update);
router.delete('/actors/:actorId', jwtAuth, requireRole('ROLE_MANAGE'), actorController.remove);
router.delete('/actors/:actorId/films/:filmId', jwtAuth, requireRole('ROLE_MANAGE'), actorController.removeFilm);

module.exports = router;
