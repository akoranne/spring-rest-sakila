const express = require('express');
const router = express.Router();
const db = require('../db');

router.get('/health', (req, res) => {
  res.json({
    name: 'location-service',
    status: 'ok',
    uptime: process.uptime(),
  });
});

router.get('/health/ready', async (req, res) => {
  try {
    await db.raw('SELECT 1');
    res.json({ status: 'ready' });
  } catch (err) {
    res.status(503).json({ status: 'not ready' });
  }
});

module.exports = router;
