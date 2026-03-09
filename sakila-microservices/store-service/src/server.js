const config = require('./config');
const app = require('./app');
const logger = require('./middleware/logger');

app.listen(config.port, () => {
  logger.info(`Store Service listening on port ${config.port}`);
});
