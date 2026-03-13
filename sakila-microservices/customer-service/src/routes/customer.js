const express = require('express');
const router = express.Router();
const customerController = require('../controllers/customerController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/customers', jwtAuth, requireRole('ROLE_MANAGE'), customerController.findAll);
router.post('/customers', jwtAuth, requireRole('ROLE_MANAGE'), customerController.create);
router.get('/customers/:customerId', jwtAuth, requireRole('ROLE_MANAGE'), customerController.findById);
router.put('/customers/:customerId', jwtAuth, requireRole('ROLE_MANAGE'), customerController.update);
router.delete('/customers/:customerId', jwtAuth, requireRole('ROLE_MANAGE'), customerController.remove);
router.get('/customers/:customerId/details', jwtAuth, requireRole('ROLE_MANAGE'), customerController.findDetails);

module.exports = router;
