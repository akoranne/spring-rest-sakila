const logger = require('./logger');

const errorHandler = (err, req, res, _next) => {
  const statusCode = err.statusCode || 500;

  const errorResponse = {
    error: {
      code: statusCode === 500 ? 'INTERNAL_ERROR' : (err.code || 'INTERNAL_ERROR'),
      message: statusCode === 500 ? 'An unexpected error occurred' : err.message,
      details: statusCode === 500 ? [] : (err.details || []),
      timestamp: new Date().toISOString(),
    },
  };

  logger.error({
    err: {
      message: err.message,
      stack: err.stack,
      code: err.code,
    },
    correlationId: req.correlationId,
    path: req.path,
  });

  res.status(statusCode).json(errorResponse);
};

module.exports = errorHandler;
