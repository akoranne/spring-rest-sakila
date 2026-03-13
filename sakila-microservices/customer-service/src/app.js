const express = require('express');
const cors = require('cors');
const correlationId = require('./middleware/correlationId');
const requestLogger = require('./middleware/requestLogger');
const healthRoutes = require('./routes/health');
const customerRoutes = require('./routes/customer');
const errorHandler = require('./middleware/errorHandler');

const app = express();

app.use(express.json());
app.use(cors());
app.use(correlationId);
app.use(requestLogger);

app.use(healthRoutes);

app.use(customerRoutes);

app.use(errorHandler);

module.exports = app;
