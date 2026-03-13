const express = require('express');
const router = express.Router();
const rentalController = require('../controllers/rentalController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/rentals', jwtAuth, requireRole('ROLE_MANAGE'), rentalController.findAll);
router.post('/rentals', jwtAuth, requireRole('ROLE_MANAGE'), rentalController.create);
router.put('/rentals/return', jwtAuth, requireRole('ROLE_MANAGE'), rentalController.returnRental);
router.get('/rentals/:rentalId', jwtAuth, requireRole('ROLE_MANAGE'), rentalController.findById);
router.put('/rentals/:rentalId', jwtAuth, requireRole('ROLE_MANAGE'), rentalController.update);
router.delete('/rentals/:rentalId', jwtAuth, requireRole('ROLE_MANAGE'), rentalController.remove);

module.exports = router;
