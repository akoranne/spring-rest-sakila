const express = require('express');
const router = express.Router();
const paymentController = require('../controllers/paymentController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/payments', jwtAuth, requireRole('ROLE_MANAGE'), paymentController.findAll);
router.get('/payments/:paymentId', jwtAuth, requireRole('ROLE_MANAGE'), paymentController.findById);
router.get('/payments/:paymentId/details', jwtAuth, requireRole('ROLE_MANAGE'), paymentController.findDetails);
router.put('/payments/:paymentId', jwtAuth, requireRole('ROLE_MANAGE'), paymentController.update);
router.delete('/payments/:paymentId', jwtAuth, requireRole('ROLE_MANAGE'), paymentController.remove);

module.exports = router;
