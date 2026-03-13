const express = require('express');
const router = express.Router();
const reportController = require('../controllers/reportController');
const { jwtAuth, requireRole } = require('../middleware/auth');

router.get('/reports/sales/categories', jwtAuth, requireRole('ROLE_MANAGE'), reportController.salesByCategory);
router.get('/reports/sales/stores', jwtAuth, requireRole('ROLE_MANAGE'), reportController.salesByStore);

module.exports = router;
