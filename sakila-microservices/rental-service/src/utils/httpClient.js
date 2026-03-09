const http = require('http');
const https = require('https');

const DEFAULT_TIMEOUT = 5000;

function request(method, url, { body, correlationId, authToken } = {}) {
  return new Promise((resolve, reject) => {
    const parsedUrl = new URL(url);
    const transport = parsedUrl.protocol === 'https:' ? https : http;

    const headers = {
      'Content-Type': 'application/json',
    };

    if (correlationId) {
      headers['X-Correlation-ID'] = correlationId;
    }

    if (authToken) {
      headers['Authorization'] = `Bearer ${authToken}`;
    }

    const options = {
      method,
      hostname: parsedUrl.hostname,
      port: parsedUrl.port,
      path: parsedUrl.pathname + parsedUrl.search,
      headers,
      timeout: DEFAULT_TIMEOUT,
    };

    const req = transport.request(options, (res) => {
      let data = '';
      res.on('data', (chunk) => { data += chunk; });
      res.on('end', () => {
        try {
          const parsed = JSON.parse(data);
          resolve({ status: res.statusCode, data: parsed, headers: res.headers });
        } catch {
          resolve({ status: res.statusCode, data, headers: res.headers });
        }
      });
    });

    req.on('timeout', () => {
      req.destroy();
      const error = new Error('Service request timed out');
      error.statusCode = 503;
      error.code = 'SERVICE_UNAVAILABLE';
      reject(error);
    });

    req.on('error', (err) => {
      const error = new Error(`Service unavailable: ${err.message}`);
      error.statusCode = 503;
      error.code = 'SERVICE_UNAVAILABLE';
      reject(error);
    });

    if (body) {
      req.write(JSON.stringify(body));
    }

    req.end();
  });
}

const httpClient = {
  get(url, { correlationId, authToken } = {}) {
    return request('GET', url, { correlationId, authToken });
  },
  post(url, body, { correlationId, authToken } = {}) {
    return request('POST', url, { body, correlationId, authToken });
  },
};

module.exports = httpClient;
