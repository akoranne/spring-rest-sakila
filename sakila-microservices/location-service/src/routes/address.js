const express = require('express');
const router = express.Router();
const addressController = require('../controllers/addressController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/location/addresses', jwtAuth, requireRole('ROLE_READ'), addressController.findAll);
router.get('/location/addresses/:addressId', jwtAuth, requireRole('ROLE_READ'), addressController.findById);
router.get('/location/addresses/:addressId/details', jwtAuth, requireRole('ROLE_READ'), addressController.findDetails);
router.post('/location/addresses', jwtAuth, requireRole('ROLE_MANAGE'), addressController.create);
router.put('/location/addresses/:addressId', jwtAuth, requireRole('ROLE_MANAGE'), addressController.update);
router.delete('/location/addresses/:addressId', jwtAuth, requireRole('ROLE_MANAGE'), addressController.remove);

module.exports = router;
