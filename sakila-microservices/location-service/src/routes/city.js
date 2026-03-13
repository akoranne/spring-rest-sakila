const express = require('express');
const router = express.Router();
const cityController = require('../controllers/cityController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/location/cities', jwtAuth, requireRole('ROLE_READ'), cityController.findAll);
router.get('/location/cities/:cityId', jwtAuth, requireRole('ROLE_READ'), cityController.findById);
router.post('/location/cities', jwtAuth, requireRole('ROLE_MANAGE'), cityController.create);
router.put('/location/cities/:cityId', jwtAuth, requireRole('ROLE_MANAGE'), cityController.update);
router.delete('/location/cities/:cityId', jwtAuth, requireRole('ROLE_MANAGE'), cityController.remove);

module.exports = router;
