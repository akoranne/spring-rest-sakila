const express = require('express');
const router = express.Router();
const filmController = require('../controllers/filmController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/films', jwtAuth, requireRole('ROLE_READ'), filmController.findAll);
router.get('/films/:filmId', jwtAuth, requireRole('ROLE_READ'), filmController.findById);
router.get('/films/:filmId/details', jwtAuth, requireRole('ROLE_READ'), filmController.findDetails);
router.get('/films/:filmId/actors', jwtAuth, requireRole('ROLE_READ'), filmController.findActors);
router.get('/films/:filmId/actors/:actorId', jwtAuth, requireRole('ROLE_READ'), filmController.findActorById);
router.post('/films', jwtAuth, requireRole('ROLE_MANAGE'), filmController.create);
router.put('/films/:filmId', jwtAuth, requireRole('ROLE_MANAGE'), filmController.update);
router.delete('/films/:filmId', jwtAuth, requireRole('ROLE_MANAGE'), filmController.remove);

module.exports = router;
