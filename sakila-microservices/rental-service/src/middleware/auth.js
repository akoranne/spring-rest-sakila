const jwt = require('jsonwebtoken');
const config = require('../config');

const jwtAuth = (req, res, next) => {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      error: {
        code: 'UNAUTHORIZED',
        message: 'Missing or invalid authentication token',
        details: [],
        timestamp: new Date().toISOString(),
      },
    });
  }

  const token = authHeader.slice(7);

  try {
    const decoded = jwt.verify(token, config.jwtSecret);
    req.user = decoded;
    next();
  } catch (err) {
    return res.status(401).json({
      error: {
        code: 'UNAUTHORIZED',
        message: 'Invalid or expired authentication token',
        details: [],
        timestamp: new Date().toISOString(),
      },
    });
  }
};

const requireRole = (...roles) => (req, res, next) => {
  if (!req.user || !req.user.roles || !roles.some(role => req.user.roles.includes(role))) {
    return res.status(403).json({
      error: {
        code: 'FORBIDDEN',
        message: 'Insufficient permissions',
        details: [],
        timestamp: new Date().toISOString(),
      },
    });
  }
  next();
};

module.exports = { jwtAuth, requireRole };
