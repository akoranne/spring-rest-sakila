const knex = require('knex');
const config = require('../config');

const db = knex({
  client: 'pg',
  connection: config.databaseUrl,
});

module.exports = db;
