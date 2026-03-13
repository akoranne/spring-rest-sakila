// Feature: legacy-modernization
// Property 19: Configuration Read From Environment Variables
// Property 20: Default Values for Optional Configuration
// Property 21: Missing Required Configuration Causes Startup Failure

const fc = require('fast-check');
const { execFileSync } = require('child_process');
const path = require('path');

// All env var keys the config module reads
const ALL_CONFIG_KEYS = [
  'DATABASE_URL', 'JWT_SECRET', 'PORT', 'LOG_LEVEL',
  'PAYMENT_SERVICE_URL', 'RENTAL_SERVICE_URL', 'CUSTOMER_SERVICE_URL',
  'CATALOG_SERVICE_URL', 'LOCATION_SERVICE_URL', 'STORE_SERVICE_URL',
  'AUTH_SERVICE_URL',
];

/**
 * Loads the config module in a clean state with only the given env vars set.
 * Uses jest.isolateModules to get a fresh module evaluation each time.
 * Mocks process.exit so the test runner survives missing required vars.
 */
function loadConfigWithEnv(envOverrides) {
  // 1. Snapshot and remove all config-related env vars
  const snapshot = {};
  for (const key of ALL_CONFIG_KEYS) {
    snapshot[key] = process.env[key];
    delete process.env[key];
  }

  // 2. Set only the overrides
  for (const [key, value] of Object.entries(envOverrides)) {
    process.env[key] = value;
  }

  // 3. Mock process.exit and console.error before requiring
  const exitSpy = jest.spyOn(process, 'exit').mockImplementation(() => {});
  const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

  // 4. Load fresh module via jest.isolateModules
  let config = null;
  jest.isolateModules(() => {
    config = require('../../src/config/index.js');
  });

  const exitCalled = exitSpy.mock.calls.length > 0;
  const errorMessages = consoleSpy.mock.calls.map(c => c.join(' '));

  // 5. Restore spies
  exitSpy.mockRestore();
  consoleSpy.mockRestore();

  // 6. Restore original env
  for (const key of ALL_CONFIG_KEYS) {
    if (snapshot[key] !== undefined) {
      process.env[key] = snapshot[key];
    } else {
      delete process.env[key];
    }
  }

  return { config, exitCalled, errorMessages };
}


describe('Property 19: Configuration Read From Environment Variables', () => {
  /**
   * Validates: Requirements 3.6, 19.1, 19.2
   *
   * For any supported configuration variable, when the environment variable
   * is set, the microservice config should use that value.
   */

  test('DATABASE_URL and JWT_SECRET are read from env', () => {
    const dbUrlArb = fc.constantFrom(
      'postgresql://user:pass@localhost:5432/mydb',
      'postgresql://admin:secret@db.host:5433/sakila',
      'postgresql://test:test@127.0.0.1/testdb'
    );
    const secretArb = fc.constantFrom('my-secret-key', 'another-secret', 'jwt-s3cr3t');

    fc.assert(
      fc.property(dbUrlArb, secretArb, (dbUrl, secret) => {
        const { config } = loadConfigWithEnv({
          DATABASE_URL: dbUrl,
          JWT_SECRET: secret,
        });

        expect(config.databaseUrl).toBe(dbUrl);
        expect(config.jwtSecret).toBe(secret);
      }),
      { numRuns: 20 }
    );
  });

  test('PORT is read from env as an integer', () => {
    const portArb = fc.integer({ min: 1024, max: 65535 });

    fc.assert(
      fc.property(portArb, (port) => {
        const { config } = loadConfigWithEnv({
          DATABASE_URL: 'postgresql://test:test@localhost/db',
          JWT_SECRET: 'secret',
          PORT: String(port),
        });

        expect(config.port).toBe(port);
      }),
      { numRuns: 30 }
    );
  });

  test('LOG_LEVEL is read from env', () => {
    const logLevelArb = fc.constantFrom('error', 'warn', 'info', 'debug', 'verbose', 'silly');

    fc.assert(
      fc.property(logLevelArb, (level) => {
        const { config } = loadConfigWithEnv({
          DATABASE_URL: 'postgresql://test:test@localhost/db',
          JWT_SECRET: 'secret',
          LOG_LEVEL: level,
        });

        expect(config.logLevel).toBe(level);
      }),
      { numRuns: 6 }
    );
  });

  test('service URL env vars are read into config', () => {
    const urlArb = fc.constantFrom(
      'http://payment:3005',
      'http://rental:3006',
      'http://customer:3003',
      'http://catalog:3002',
      'http://location:3004',
      'http://store:3007',
      'http://auth:3001'
    );

    fc.assert(
      fc.property(urlArb, urlArb, urlArb, (payUrl, rentalUrl, storeUrl) => {
        const { config } = loadConfigWithEnv({
          DATABASE_URL: 'postgresql://test:test@localhost/db',
          JWT_SECRET: 'secret',
          PAYMENT_SERVICE_URL: payUrl,
          RENTAL_SERVICE_URL: rentalUrl,
          STORE_SERVICE_URL: storeUrl,
        });

        expect(config.paymentServiceUrl).toBe(payUrl);
        expect(config.rentalServiceUrl).toBe(rentalUrl);
        expect(config.storeServiceUrl).toBe(storeUrl);
      }),
      { numRuns: 20 }
    );
  });
});

describe('Property 20: Default Values for Optional Configuration', () => {
  /**
   * Validates: Requirements 19.3
   *
   * When PORT and LOG_LEVEL are not set in the environment, the
   * microservice should use defaults: PORT=3000, LOG_LEVEL=info.
   */

  test('PORT defaults to 3000 when not set', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const { config } = loadConfigWithEnv({
          DATABASE_URL: 'postgresql://test:test@localhost/db',
          JWT_SECRET: 'secret',
        });

        expect(config.port).toBe(3000);
      }),
      { numRuns: 5 }
    );
  });

  test('LOG_LEVEL defaults to info when not set', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const { config } = loadConfigWithEnv({
          DATABASE_URL: 'postgresql://test:test@localhost/db',
          JWT_SECRET: 'secret',
        });

        expect(config.logLevel).toBe('info');
      }),
      { numRuns: 5 }
    );
  });

  test('defaults apply even when other optional vars are set', () => {
    fc.assert(
      fc.property(
        fc.constantFrom('http://pay:3005', 'http://rent:3006'),
        (serviceUrl) => {
          const { config } = loadConfigWithEnv({
            DATABASE_URL: 'postgresql://test:test@localhost/db',
            JWT_SECRET: 'secret',
            PAYMENT_SERVICE_URL: serviceUrl,
          });

          expect(config.port).toBe(3000);
          expect(config.logLevel).toBe('info');
        }
      ),
      { numRuns: 5 }
    );
  });
});

describe('Property 21: Missing Required Configuration Causes Startup Failure', () => {
  /**
   * Validates: Requirements 19.4
   *
   * When DATABASE_URL or JWT_SECRET is missing, the service should log
   * an error identifying the missing variable and exit with non-zero code.
   */

  test('missing DATABASE_URL triggers process.exit', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const { exitCalled } = loadConfigWithEnv({
          JWT_SECRET: 'secret',
        });
        expect(exitCalled).toBe(true);
      }),
      { numRuns: 5 }
    );
  });

  test('missing JWT_SECRET triggers process.exit', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const { exitCalled } = loadConfigWithEnv({
          DATABASE_URL: 'postgresql://test:test@localhost/db',
        });
        expect(exitCalled).toBe(true);
      }),
      { numRuns: 5 }
    );
  });

  test('missing both required vars triggers process.exit', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const { exitCalled } = loadConfigWithEnv({});
        expect(exitCalled).toBe(true);
      }),
      { numRuns: 5 }
    );
  });

  test('logs error message identifying the missing variable', () => {
    const requiredVarArb = fc.constantFrom('DATABASE_URL', 'JWT_SECRET');

    fc.assert(
      fc.property(requiredVarArb, (missingVar) => {
        const env = {};
        if (missingVar === 'DATABASE_URL') {
          env.JWT_SECRET = 'secret';
        } else {
          env.DATABASE_URL = 'postgresql://test:test@localhost/db';
        }

        const { errorMessages } = loadConfigWithEnv(env);
        const mentionsMissing = errorMessages.some(msg => msg.includes(missingVar));
        expect(mentionsMissing).toBe(true);
      }),
      { numRuns: 4 }
    );
  });

  test('exits with non-zero code via child process', () => {
    const configPath = require.resolve('../../src/config/index.js');
    const requiredVarArb = fc.constantFrom('DATABASE_URL', 'JWT_SECRET');

    fc.assert(
      fc.property(requiredVarArb, (missingVar) => {
        const script = `require('${configPath.replace(/\\/g, '\\\\')}')`;

        const env = { PATH: process.env.PATH };
        if (missingVar === 'DATABASE_URL') {
          env.JWT_SECRET = 'secret';
        } else {
          env.DATABASE_URL = 'postgresql://test:test@localhost/db';
        }

        let exitCode = 0;
        try {
          execFileSync('node', ['-e', script], {
            env,
            stdio: 'pipe',
            timeout: 5000,
          });
        } catch (err) {
          exitCode = err.status;
        }

        expect(exitCode).not.toBe(0);
      }),
      { numRuns: 4 }
    );
  });
});
