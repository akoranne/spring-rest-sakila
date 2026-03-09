const requiredVars = ['DATABASE_URL', 'JWT_SECRET'];

for (const varName of requiredVars) {
  if (!process.env[varName]) {
    console.error(`ERROR: Required environment variable ${varName} is missing`);
    process.exit(1);
  }
}

const config = {
  databaseUrl: process.env.DATABASE_URL,
  jwtSecret: process.env.JWT_SECRET,
  port: parseInt(process.env.PORT, 10) || 3000,
  logLevel: process.env.LOG_LEVEL || 'info',
  paymentServiceUrl: process.env.PAYMENT_SERVICE_URL,
  rentalServiceUrl: process.env.RENTAL_SERVICE_URL,
  customerServiceUrl: process.env.CUSTOMER_SERVICE_URL,
  catalogServiceUrl: process.env.CATALOG_SERVICE_URL,
  locationServiceUrl: process.env.LOCATION_SERVICE_URL,
  storeServiceUrl: process.env.STORE_SERVICE_URL,
  authServiceUrl: process.env.AUTH_SERVICE_URL,
};

module.exports = config;
