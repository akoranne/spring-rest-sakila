const express = require('express');
const cors = require('cors');
const correlationId = require('./middleware/correlationId');
const requestLogger = require('./middleware/requestLogger');
const healthRoutes = require('./routes/health');
const errorHandler = require('./middleware/errorHandler');

const app = express();

app.use(express.json());
app.use(cors());
app.use(correlationId);
app.use(requestLogger);

app.use(healthRoutes);

const storeRoutes = require('./routes/store');
const staffRoutes = require('./routes/staff');
const reportRoutes = require('./routes/report');

app.use(storeRoutes);
app.use(staffRoutes);
app.use(reportRoutes);

app.use(errorHandler);

module.exports = app;
