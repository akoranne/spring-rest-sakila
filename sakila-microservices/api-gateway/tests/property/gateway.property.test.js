// Feature: legacy-modernization, Property 6: API Gateway Returns 502 When Target Unreachable
// Validates: Requirements 9.8

const { execSync } = require('child_process');
const path = require('path');
const fc = require('fast-check');
const fetch = require('node-fetch');

const GATEWAY_PORT = 18080; // Use a high port to avoid conflicts
const CONTAINER_NAME = 'api-gateway-property-test';
const GATEWAY_URL = `http://localhost:${GATEWAY_PORT}`;

// Check if Docker is available before running tests
function isDockerAvailable() {
  try {
    execSync('docker info', { stdio: 'ignore' });
    return true;
  } catch (_) {
    return false;
  }
}

const DOCKER_AVAILABLE = isDockerAvailable();
const describeOrSkip = DOCKER_AVAILABLE ? describe : describe.skip;

if (!DOCKER_AVAILABLE) {
  test('skipped: Docker is not available (required for NGINX gateway tests)', () => {
    console.warn('Docker daemon is not running. Skipping API Gateway property tests.');
  });
}

// All route prefixes the gateway is configured to proxy
const ROUTE_PREFIXES = [
  '/api/v1/login',
  '/api/v1/films',
  '/api/v1/actors',
  '/api/v1/customers',
  '/api/v1/location',
  '/api/v1/payments',
  '/api/v1/rentals',
  '/api/v1/stores',
  '/api/v1/staffs',
  '/api/v1/reports',
];

// Arbitrary: pick any of the configured route prefixes
const routePrefixArb = fc.constantFrom(...ROUTE_PREFIXES);

// Arbitrary: generate optional path suffixes (e.g., /123, /123/details)
const pathSegmentArb = fc.stringOf(
  fc.constantFrom(...'abcdefghijklmnopqrstuvwxyz0123456789'),
  { minLength: 1, maxLength: 8 }
);
const pathSuffixArb = fc.oneof(
  fc.constant(''),
  pathSegmentArb.map(s => `/${s}`),
  fc.tuple(pathSegmentArb, pathSegmentArb).map(([a, b]) => `/${a}/${b}`)
);

// Arbitrary: pick an HTTP method commonly used with REST APIs
const httpMethodArb = fc.constantFrom('GET', 'POST', 'PUT', 'DELETE');

function startGateway() {
  const confPath = path.resolve(__dirname, '../../nginx.conf');

  // Stop any leftover container from a previous run
  try {
    execSync(`docker rm -f ${CONTAINER_NAME} 2>/dev/null`, { stdio: 'ignore' });
  } catch (_) { /* ignore */ }

  // Start NGINX with no backend services — all upstreams will be unreachable.
  // We mount the config and add a custom resolver so NGINX starts even though
  // upstream hostnames don't resolve. The trick: override the upstream block
  // via an inline config that points all upstreams to a port nothing listens on.
  const testConf = path.resolve(__dirname, '../fixtures/test-nginx.conf');

  execSync(
    `docker run -d --name ${CONTAINER_NAME} ` +
    `-p ${GATEWAY_PORT}:8080 ` +
    `-v ${testConf}:/etc/nginx/conf.d/default.conf:ro ` +
    `nginx:1.25-alpine`,
    { stdio: 'pipe' }
  );

  // Wait for NGINX to be ready
  const maxRetries = 20;
  for (let i = 0; i < maxRetries; i++) {
    try {
      execSync(`curl -sf http://localhost:${GATEWAY_PORT}/health`, { stdio: 'ignore' });
      return; // ready
    } catch (_) {
      execSync('sleep 0.5', { stdio: 'ignore' });
    }
  }
  throw new Error('NGINX gateway did not become ready in time');
}

function stopGateway() {
  try {
    execSync(`docker rm -f ${CONTAINER_NAME}`, { stdio: 'ignore' });
  } catch (_) { /* ignore */ }
}

describeOrSkip('Property 6: API Gateway Returns 502 When Target Unreachable', () => {
  /**
   * Validates: Requirements 9.8
   *
   * For any request routed through the API Gateway where the target
   * microservice is unreachable, the gateway should return HTTP 502
   * with a descriptive JSON error body.
   */

  beforeAll(() => {
    startGateway();
  });

  afterAll(() => {
    stopGateway();
  });

  test('returns 502 with standard error format for any routed path when backend is unreachable', async () => {
    await fc.assert(
      fc.asyncProperty(routePrefixArb, pathSuffixArb, httpMethodArb, async (prefix, suffix, method) => {
        const url = `${GATEWAY_URL}${prefix}${suffix}`;

        const response = await fetch(url, {
          method,
          headers: { 'Content-Type': 'application/json' },
          // POST/PUT need a body to avoid NGINX 411
          body: (method === 'POST' || method === 'PUT') ? '{}' : undefined,
        });

        // NGINX should return 502 since no backend is listening
        expect(response.status).toBe(502);

        const contentType = response.headers.get('content-type');
        expect(contentType).toContain('application/json');

        const body = await response.json();

        // Verify standard error response format
        expect(body).toHaveProperty('error');
        expect(body.error).toHaveProperty('code', 'BAD_GATEWAY');
        expect(typeof body.error.message).toBe('string');
        expect(body.error.message.length).toBeGreaterThan(0);
        expect(body.error).toHaveProperty('details');
        expect(Array.isArray(body.error.details)).toBe(true);
        expect(body.error).toHaveProperty('timestamp');
        expect(typeof body.error.timestamp).toBe('string');
      }),
      { numRuns: 50 }
    );
  }, 120000);

  test('health endpoint still returns 200 when backends are down', async () => {
    const response = await fetch(`${GATEWAY_URL}/health`);
    expect(response.status).toBe(200);

    const body = await response.json();
    expect(body.name).toBe('api-gateway');
    expect(body.status).toBe('ok');
  });
});
