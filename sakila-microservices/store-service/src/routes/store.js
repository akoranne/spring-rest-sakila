const express = require('express');
const router = express.Router();
const storeController = require('../controllers/storeController');
const { jwtAuth, requireRole } = require('../middleware/auth');

// Store CRUD
router.get('/stores', jwtAuth, requireRole('ROLE_MANAGE'), storeController.findAll);
router.get('/stores/:storeId', jwtAuth, requireRole('ROLE_MANAGE'), storeController.findById);
router.get('/stores/:storeId/details', jwtAuth, requireRole('ROLE_MANAGE'), storeController.findDetails);
router.post('/stores', jwtAuth, requireRole('ROLE_ADMIN'), storeController.create);
router.put('/stores/:storeId', jwtAuth, requireRole('ROLE_ADMIN'), storeController.update);
router.delete('/stores/:storeId', jwtAuth, requireRole('ROLE_ADMIN'), storeController.remove);

// Store staff
router.get('/stores/:storeId/staffs', jwtAuth, requireRole('ROLE_MANAGE'), storeController.findStoreStaff);
router.get('/stores/:storeId/staffs/:staffId', jwtAuth, requireRole('ROLE_MANAGE'), storeController.findStoreStaffById);
router.post('/stores/:storeId/staffs/:staffId', jwtAuth, requireRole('ROLE_ADMIN'), storeController.assignStoreStaff);
router.put('/stores/:storeId/staffs/:staffId', jwtAuth, requireRole('ROLE_ADMIN'), storeController.updateStoreStaff);
router.delete('/stores/:storeId/staffs/:staffId', jwtAuth, requireRole('ROLE_ADMIN'), storeController.removeStoreStaff);

module.exports = router;
