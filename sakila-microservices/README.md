# Sakila Microservices

Seven domain-bounded Node.js/Express microservices modernized from the Spring REST Sakila monolith, backed by PostgreSQL with an NGINX API Gateway.

## Services

| Service | Port | Description |
|---------|------|-------------|
| auth-service | 3001 | Authentication and JWT token issuance |
| catalog-service | 3002 | Films, actors, categories |
| customer-service | 3003 | Customer accounts and profiles |
| location-service | 3004 | Countries, cities, addresses |
| payment-service | 3005 | Payment records |
| rental-service | 3006 | DVD rental and return operations |
| store-service | 3007 | Stores, inventory, staff, reports |
| api-gateway | 8080 | NGINX reverse proxy (single entry point) |

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose
- [Node.js 20 LTS](https://nodejs.org/) (for local development without Docker)
- PostgreSQL 15+ (if running services individually)

## Building Docker Images

Build all service images individually:

```bash
# From the sakila-microservices/ directory
docker build -t sakila/auth-service ./auth-service
docker build -t sakila/catalog-service ./catalog-service
docker build -t sakila/customer-service ./customer-service
docker build -t sakila/location-service ./location-service
docker build -t sakila/payment-service ./payment-service
docker build -t sakila/rental-service ./rental-service
docker build -t sakila/store-service ./store-service
docker build -t sakila/api-gateway ./api-gateway
```

Or build everything at once via Docker Compose:

```bash
docker-compose build
```

To verify images were created:

```bash
docker images | grep sakila
```

## Quick Start (Docker Compose)

```bash
# From the sakila-microservices/ directory
docker-compose up --build
```

This builds all images (if not already built), starts PostgreSQL, runs all migration scripts, creates per-service database users, starts all seven microservices, and the NGINX API Gateway.

The API is available at `http://localhost:8080`.

To run in detached mode:

```bash
docker-compose up --build -d
```

To stop all services:

```bash
docker-compose down
```

To stop and remove volumes (wipes database data):

```bash
docker-compose down -v
```

## Running Individual Services Locally

### 1. Start PostgreSQL

```bash
# Start just the database
docker-compose up -d postgres
```

### 2. Run Migrations

```bash
# Connect to the running PostgreSQL container and run migration scripts
docker-compose exec postgres psql -U postgres -d sakila \
  -f /docker-entrypoint-initdb.d/migrations/001_auth_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/002_catalog_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/003_customer_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/004_location_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/005_payment_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/006_rental_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/007_store_schema.sql \
  -f /docker-entrypoint-initdb.d/migrations/008_functions.sql \
  -f /docker-entrypoint-initdb.d/migrations/009_views.sql \
  -f /docker-entrypoint-initdb.d/migrations/010_seed_data.sql
```

### 3. Install Dependencies and Start a Service

```bash
cd auth-service
npm install

# Set required environment variables
export DATABASE_URL=postgresql://auth_user:changeme@localhost:5432/sakila
export JWT_SECRET=your-secret-key
export PORT=3001

npm start
```

Repeat for each service, adjusting `DATABASE_URL` (use the service-specific user) and `PORT`.

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| DATABASE_URL | Yes | — | PostgreSQL connection string |
| JWT_SECRET | Yes | — | Secret key for signing/verifying JWTs |
| PORT | No | 3000 | HTTP port the service listens on |
| LOG_LEVEL | No | info | Winston log level (error, warn, info, debug) |
| PAYMENT_SERVICE_URL | No | — | Base URL for Payment Service (used by Customer Service) |
| RENTAL_SERVICE_URL | No | — | Base URL for Rental Service (used by Customer Service) |

## API Gateway Routes

All routes are prefixed with `/api/v1`:

| Route | Service |
|-------|---------|
| `/api/v1/login` | auth-service |
| `/api/v1/films`, `/api/v1/actors` | catalog-service |
| `/api/v1/customers` | customer-service |
| `/api/v1/location` | location-service |
| `/api/v1/payments` | payment-service |
| `/api/v1/rentals` | rental-service |
| `/api/v1/stores`, `/api/v1/staffs`, `/api/v1/reports` | store-service |

## Health Checks

Every service exposes:

- `GET /health` — returns service name, status, and uptime
- `GET /health/ready` — returns 200 if database is reachable, 503 otherwise

## Running Tests

```bash
cd auth-service
npm install
npm test                # run all tests
npm run test:coverage   # run with coverage report
```

## CI/CD Pipelines

Two GitHub Actions workflows live in `.github/workflows/`:

### CI Pipeline (`ci.yml`)

Runs automatically on every push to `main` and on pull requests.

What it does:
1. Detects which services have changed files (skips unchanged services)
2. Installs dependencies (`npm ci`)
3. Runs `npm audit` for security vulnerabilities
4. Runs tests with coverage (`npm run test:coverage`), enforcing 80% line coverage on service-layer code
5. On `main` branch pushes, builds Docker images tagged with the git commit SHA

No setup required — it triggers automatically when you push to a GitHub repository.

To run tests locally the same way CI does:

```bash
cd auth-service
npm ci
npm audit --audit-level=high
DATABASE_URL=postgresql://test:test@localhost:5432/testdb JWT_SECRET=test-secret npm run test:coverage
```

### CD Pipeline (`cd.yml`)

Runs on pushes to `main` when service code changes. Builds and pushes Docker images to Amazon ECR.

Setup required:

1. Create an ECR repository for each service (e.g., `sakila/auth-service`, `sakila/catalog-service`, etc.)

2. Create an IAM role for GitHub Actions with OIDC trust and ECR push permissions:
   ```json
   {
     "Effect": "Allow",
     "Action": [
       "ecr:GetAuthorizationToken",
       "ecr:BatchCheckLayerAvailability",
       "ecr:PutImage",
       "ecr:InitiateLayerUpload",
       "ecr:UploadLayerPart",
       "ecr:CompleteLayerUpload"
     ],
     "Resource": "*"
   }
   ```

3. Add the IAM role ARN as a GitHub repository secret:
   - Go to your repo Settings > Secrets and variables > Actions
   - Add `AWS_ROLE_ARN` with the role ARN value

4. (Optional) Update `AWS_REGION` and `ECR_REGISTRY_PREFIX` in `cd.yml` if your setup differs from the defaults (`us-east-1` and `sakila`)

Once configured, every push to `main` that touches service code will automatically build and push images tagged with the commit SHA and `latest`.

## Project Structure

```
sakila-microservices/
├── api-gateway/           # NGINX config and Dockerfile
├── auth-service/          # Authentication service
├── catalog-service/       # Film and actor management
├── customer-service/      # Customer profiles
├── location-service/      # Geographic reference data
├── payment-service/       # Payment records
├── rental-service/        # Rental operations
├── store-service/         # Store, inventory, staff, reports
└── infrastructure/
    ├── migrations/        # PostgreSQL schema migration scripts
    └── db-init/           # Per-service database user setup
```
