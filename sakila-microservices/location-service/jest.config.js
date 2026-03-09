module.exports = {
  testEnvironment: 'node',
  collectCoverageFrom: ['src/**/*.js'],
  coverageThreshold: {
    'src/services/**/*.js': {
      lines: 80
    }
  }
};
