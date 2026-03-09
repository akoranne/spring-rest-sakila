const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const config = require('../config');
const authorityRepository = require('../repositories/authorityRepository');

const login = async (email, password) => {
  const authority = await authorityRepository.findByEmail(email);

  if (!authority) {
    const err = new Error('Invalid email or password');
    err.statusCode = 401;
    err.code = 'UNAUTHORIZED';
    throw err;
  }

  const valid = await bcrypt.compare(password, authority.password);

  if (!valid) {
    const err = new Error('Invalid email or password');
    err.statusCode = 401;
    err.code = 'UNAUTHORIZED';
    throw err;
  }

  const roles = authority.authority || [];

  const token = jwt.sign(
    { sub: email, roles },
    config.jwtSecret,
    { expiresIn: '1h' }
  );

  return token;
};

module.exports = { login };
