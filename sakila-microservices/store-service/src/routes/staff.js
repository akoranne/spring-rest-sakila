const express = require('express');
const router = express.Router();
const staffController = require('../controllers/staffController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/staffs', jwtAuth, requireRole('ROLE_MANAGE'), staffController.findAll);
router.get('/staffs/:staffId', jwtAuth, requireRole('ROLE_MANAGE'), staffController.findById);
router.get('/staffs/:staffId/details', jwtAuth, requireRole('ROLE_MANAGE'), staffController.findDetails);
router.post('/staffs', jwtAuth, requireRole('ROLE_ADMIN'), staffController.create);
router.put('/staffs/:staffId', jwtAuth, requireRole('ROLE_ADMIN'), staffController.update);
router.delete('/staffs/:staffId', jwtAuth, requireRole('ROLE_ADMIN'), staffController.remove);

module.exports = router;
