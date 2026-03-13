# Implementation Plan: Legacy Modernization

## Overview

Convert the Spring REST Sakila monolith into seven domain-bounded Node.js/Express microservices backed by PostgreSQL, orchestrated via Docker Compose with an NGINX API Gateway. Each service is an independent project in a new `sakila-microservices/` workspace. The monolith codebase remains completely unmodified. Implementation proceeds bottom-up: shared infrastructure and utilities first, then database migrations, then individual services, then gateway and orchestration, then testing and CI/CD.

## Tasks

- [x] 1. Set up workspace structure and shared utilities
  - [x] 1.1 Create the `sakila-microservices/` workspace directory structure
    - Create top-level `sakila-microservices/` directory with subdirectories: `api-gateway/`, `auth-service/`, `catalog-service/`, `customer-service/`, `location-service/`, `payment-service/`, `rental-service/`, `store-service/`, `infrastructure/`, `infrastructure/migrations/`, `infrastructure/db-init/`
    - Initialize each service directory with `package.json`, `.gitignore`, `.dockerignore`, `README.md`
    - Each `package.json` should include dependencies: `express`, `knex`, `pg`, `joi`, `jsonwebtoken`, `winston`, `cors`, `uuid`; devDependencies: `jest`, `fast-check`, `supertest`, `nock`
    - Create `jest.config.js` in each service with coverage thresholds (80% lines for service layer)
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7_

  - [x] 1.2 Create shared utility modules (config, logger, error handler, JWT middleware, correlation ID middleware)
    - In each service, create `src/config/index.js` that reads `DATABASE_URL`, `JWT_SECRET`, `PORT` (default 3000), `LOG_LEVEL` (default info), and service-specific URLs from environment variables; logs error and exits with non-zero code if required vars are missing
    - Create `src/middleware/logger.js` using Winston for structured JSON logging with `timestamp`, `method`, `path`, `statusCode`, `responseTime` fields
    - Create `src/middleware/requestLogger.js` Express middleware that logs every incoming request
    - Create `src/middleware/correlationId.js` middleware that reads `X-Correlation-ID` from incoming request or generates a UUID, attaches to `req.correlationId`, and sets it on the response header
    - Create `src/middleware/errorHandler.js` centralized error-handling middleware returning `{ error: { code, message, details, timestamp } }` format; never exposes stack traces or internal details on 500 errors
    - Create `src/middleware/auth.js` with `jwtAuth` middleware (extracts Bearer token, verifies signature/expiration, attaches `req.user`, returns 401 if invalid) and `requireRole(...roles)` factory (returns 403 if user lacks required role)
    - Create `src/utils/httpClient.js` helper for inter-service HTTP calls with 5-second timeout, correlation ID propagation, JWT forwarding, and 503 response on connection failure
    - _Requirements: 3.6, 5.5, 5.6, 6.3, 6.4, 6.5, 10.4, 10.5, 15.1, 15.2, 15.3, 15.4, 15.5, 19.1, 19.2, 19.3, 19.4_

  - [x] 1.3 Create the Express app scaffold (`src/app.js`) for each service
    - Create `src/app.js` in each service that sets up Express with JSON body parsing, correlation ID middleware, request logger middleware, health routes, service-specific routes, and error handler as last middleware
    - Create `src/server.js` entry point that loads config, creates the app, and starts listening on the configured PORT
    - Create `src/routes/health.js` with `GET /health` (returns `{ name, status: "ok", uptime }`) and `GET /health/ready` (pings database, returns 200 or 503)
    - _Requirements: 3.1, 3.2, 10.1, 10.2, 10.3_

- [ ] 2. Create PostgreSQL migration scripts
  - [x] 2.1 Write schema creation migration scripts for all seven domain schemas
    - Create `infrastructure/migrations/001_auth_schema.sql`: `auth_schema` with `authority` table (SERIAL authority_id, VARCHAR(50) email UNIQUE, VARCHAR(60) password, TEXT[] authority), last_update trigger
    - Create `infrastructure/migrations/002_catalog_schema.sql`: `catalog_schema` with `language`, `film` (with `film_rating` ENUM type, TEXT[] special_features), `actor`, `film_actor`, `film_category`, `film_text` (with tsvector column and GIN index), `category` tables; all intra-schema foreign keys preserved; last_update triggers
    - Create `infrastructure/migrations/003_customer_schema.sql`: `customer_schema` with `customer` table (store_id, address_id, authority_id as unconstrained integers); last_update trigger
    - Create `infrastructure/migrations/004_location_schema.sql`: `location_schema` with `country`, `city`, `address` tables; intra-schema foreign keys preserved; last_update triggers
    - Create `infrastructure/migrations/005_payment_schema.sql`: `payment_schema` with `payment` table (customer_id, staff_id, rental_id as unconstrained integers); last_update trigger
    - Create `infrastructure/migrations/006_rental_schema.sql`: `rental_schema` with `rental` table (inventory_id, customer_id, staff_id as unconstrained integers); last_update trigger
    - Create `infrastructure/migrations/007_store_schema.sql`: `store_schema` with `store`, `inventory`, `staff` tables (address_id, film_id, authority_id as unconstrained integers for cross-schema refs); intra-schema foreign keys preserved; last_update triggers
    - Create the shared `update_last_update_column()` trigger function used by all schemas
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8, 7.9_

  - [x] 2.2 Write stored procedures, functions, and views migration scripts
    - Create PostgreSQL equivalents for `inventory_in_stock()`, `inventory_held_by_customer()`, `film_in_stock()`, `film_not_in_stock()` in `store_schema`
    - Create `customer_schema.get_customer_balance()` function
    - Create `catalog_schema.film_list` view, `store_schema.staff_list` view
    - Create `infrastructure/migrations/008_seed_data.sql` with representative seed data for all schemas
    - _Requirements: 7.10, 7.11, 7.12_

  - [x] 2.3 Write database user creation and permission scripts
    - Create `infrastructure/db-init/init-schemas.sh` that creates per-service database users with USAGE and DML privileges scoped to their own schema only
    - Each user (auth_user, catalog_user, customer_user, location_user, payment_user, rental_user, store_user) should have no access to other schemas
    - _Requirements: 8.1, 8.3, 8.4_

  - [ ]* 2.4 Write property tests for database migration correctness
    - **Property 8: Auto-Increment to SERIAL Conversion** — Verify inserting rows without specifying ID auto-generates sequential IDs for all tables
    - **Validates: Requirements 7.2**
    - **Property 9: Last Update Trigger Auto-Updates Timestamp** — Verify updating any column auto-sets last_update to current timestamp
    - **Validates: Requirements 7.5**
    - **Property 10: Cross-Schema Foreign Key Removal** — Verify cross-schema reference columns have no FK constraints
    - **Validates: Requirements 7.8**

- [ ] 3. Checkpoint - Database foundation
  - Ensure all migration scripts run successfully against a PostgreSQL instance, all schemas are created, seed data loads, and per-service users have correct permissions. Ask the user if questions arise.

- [x] 4. Implement Auth Service
  - [x] 4.1 Implement Auth Service routes, controller, service, and repository
    - Create `auth-service/src/routes/auth.js` with `POST /login` route
    - Create `auth-service/src/controllers/authController.js` that validates request body (email, password required), calls auth service, returns JWT in `Authorization` header
    - Create `auth-service/src/services/authService.js` that looks up authority by email, verifies password, signs JWT with `{ sub: email, roles, iat, exp }` payload (1-hour expiry)
    - Create `auth-service/src/repositories/authorityRepository.js` using Knex.js scoped to `auth_schema.authority`
    - Create `auth-service/src/validators/authValidator.js` with Joi schema for login request (email required, password required)
    - Wire routes into `app.js`
    - _Requirements: 2.3, 4.9, 6.1, 6.2_

  - [x] 4.2 Write property tests for Auth Service
    - **Property 1: JWT Authentication Round Trip** — For any valid authority record, authenticating and decoding the JWT yields matching email and roles
    - **Validates: Requirements 2.3, 6.1, 6.2**
    - **Property 2: JWT Validation Rejects Invalid Tokens** — For any random string, the JWT middleware rejects it with 401
    - **Validates: Requirements 6.3, 6.4**

  - [x] 4.3 Write unit tests for Auth Service
    - Test authService.login success path (valid credentials return JWT)
    - Test authService.login failure paths (wrong password, non-existent email)
    - Test JWT middleware (valid token, expired token, missing token, tampered signature)
    - Test requireRole middleware (sufficient role passes, insufficient role returns 403)
    - Mock database calls via Knex.js mocks
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 13.1, 13.4, 13.6_

- [ ] 5. Implement Location Service
  - [x] 5.1 Implement Location Service routes, controllers, services, and repositories
    - Create routes for addresses: `GET /location/addresses`, `POST /location/addresses`, `GET /location/addresses/:addressId`, `PUT /location/addresses/:addressId`, `DELETE /location/addresses/:addressId`, `GET /location/addresses/:addressId/details`
    - Create routes for cities: `GET /location/cities`, `POST /location/cities`, `GET /location/cities/:cityId`, `PUT /location/cities/:cityId`, `DELETE /location/cities/:cityId`
    - Create controllers, services, and repositories for address and city CRUD operations using Knex.js scoped to `location_schema`
    - Create Joi validators for address (address, district, city_id required; max lengths enforced) and city (city, country_id required)
    - Implement pagination via `page`, `size`, `sort` query parameters on list endpoints
    - Apply `jwtAuth` and `requireRole('ROLE_READ')` for GET, `requireRole('ROLE_MANAGE')` for POST/PUT/DELETE
    - _Requirements: 2.6, 3.2, 3.4, 3.5, 4.10, 6.3, 18.1, 18.2_

  - [x] 5.2 Write unit tests for Location Service
    - Test address and city CRUD service-layer functions (success and error paths)
    - Test Joi validation schemas (valid input accepted, invalid input rejected with field-level errors)
    - Test pagination logic
    - Mock Knex.js database calls
    - _Requirements: 13.1, 13.2, 13.4_

- [-] 6. Implement Catalog Service
  - [x] 6.1 Implement Catalog Service routes, controllers, services, and repositories
    - Create routes for films: `GET /films`, `POST /films`, `GET /films/:filmId`, `PUT /films/:filmId`, `DELETE /films/:filmId`, `GET /films/:filmId/actors`, `GET /films/:filmId/actors/:actorId`, `GET /films/:filmId/details`
    - Create routes for actors: `GET /actors`, `POST /actors`, `GET /actors/:actorId`, `PUT /actors/:actorId`, `DELETE /actors/:actorId`, `GET /actors/:actorId/details`, `GET /actors/:actorId/films`, `POST /actors/:actorId/films`, `DELETE /actors/:actorId/films/:filmId`, `POST /actors/search`
    - Create controllers, services, and repositories for film and actor CRUD using Knex.js scoped to `catalog_schema`
    - Implement film filtering by category, release year, and rating via query parameters
    - Implement actor search by partial name match (first or last name)
    - Implement film text full-text search via PostgreSQL tsvector
    - Create Joi validators for film (title required max 128 chars, rental_rate positive decimal, rating enum G/PG/PG-13/R/NC-17) and actor (first_name, last_name required max 45 chars)
    - Implement pagination via `page`, `size`, `sort` query parameters
    - Apply `jwtAuth` and `requireRole('ROLE_READ')` for GET, `requireRole('ROLE_MANAGE')` for POST/PUT/DELETE
    - _Requirements: 2.4, 3.2, 3.4, 3.5, 4.1, 4.2, 6.6, 18.1, 18.2, 18.3, 18.4_

  - [x] 6.2 Write property tests for Catalog Service
    - **Property 4: Pagination Consistency** — Iterating all pages yields exactly N total records with no duplicates or omissions
    - **Validates: Requirements 3.5**
    - **Property 18: Input Validation Rejects Invalid Data** — Any request body with at least one validation violation returns 400 with all errors listed
    - **Validates: Requirements 18.1, 18.2, 18.3, 18.4, 18.5**

  - [x] 6.3 Write unit tests for Catalog Service
    - Test film and actor CRUD service-layer functions (success and error paths)
    - Test Joi validation schemas for films and actors
    - Test pagination, filtering, and search logic
    - Mock Knex.js database calls
    - _Requirements: 13.1, 13.2, 13.4_

- [x] 7. Checkpoint - Core services foundation
  - Ensure Auth, Location, and Catalog services start, health endpoints respond, and unit tests pass. Ask the user if questions arise.

- [x] 8. Implement Payment Service
  - [x] 8.1 Implement Payment Service routes, controllers, services, and repositories
    - Create routes: `GET /payments`, `GET /payments/:paymentId`, `PUT /payments/:paymentId`, `DELETE /payments/:paymentId`, `GET /payments/:paymentId/details`
    - Support `GET /payments?customerId=X` query parameter for cross-service filtering (used by Customer Service)
    - Create controllers, services, and repositories using Knex.js scoped to `payment_schema`
    - Create Joi validators for payment (amount as positive decimal required, customer_id, staff_id, rental_id as integers)
    - Implement pagination via `page`, `size`, `sort` query parameters
    - Apply `jwtAuth` and `requireRole('ROLE_MANAGE')` for all endpoints
    - _Requirements: 2.7, 3.2, 3.4, 3.5, 4.5, 6.9, 18.1, 18.3_

  - [x] 8.2 Write unit tests for Payment Service
    - Test payment CRUD service-layer functions (success and error paths)
    - Test Joi validation schemas
    - Test customerId query parameter filtering
    - Mock Knex.js database calls
    - _Requirements: 13.1, 13.2, 13.4_

- [x] 9. Implement Rental Service
  - [x] 9.1 Implement Rental Service routes, controllers, services, and repositories
    - Create routes: `GET /rentals`, `POST /rentals`, `GET /rentals/:rentalId`, `PUT /rentals/:rentalId`, `DELETE /rentals/:rentalId`, `PUT /rentals/return`
    - Support `GET /rentals?customerId=X` query parameter for cross-service filtering (used by Customer Service)
    - Implement rental creation with rental_date defaulting to `now()` if not provided, return_date nullable
    - Implement `PUT /rentals/return` that sets return_date on an existing rental
    - Create controllers, services, and repositories using Knex.js scoped to `rental_schema`
    - Create Joi validators for rental (inventory_id, customer_id, staff_id as required integers)
    - Implement pagination via `page`, `size`, `sort` query parameters
    - Apply `jwtAuth` and `requireRole('ROLE_MANAGE')` for all endpoints
    - _Requirements: 2.8, 3.2, 3.4, 3.5, 4.4, 5.3, 6.8, 18.1_

  - [ ] 9.2 Write unit tests for Rental Service
    - Test rental CRUD and return service-layer functions (success and error paths)
    - Test rental_date default behavior and return_date nullable logic
    - Test Joi validation schemas
    - Mock Knex.js database calls
    - _Requirements: 13.1, 13.2, 13.4_

- [x] 10. Implement Customer Service with inter-service communication
  - [x] 10.1 Implement Customer Service routes, controllers, services, and repositories
    - Create routes: `GET /customers`, `POST /customers`, `GET /customers/:customerId`, `PUT /customers/:customerId`, `DELETE /customers/:customerId`, `GET /customers/:customerId/details`
    - Create controllers, services, and repositories using Knex.js scoped to `customer_schema`
    - Create Joi validators for customer (first_name, last_name required max 45 chars, store_id, address_id as required integers)
    - Implement pagination via `page`, `size`, `sort` query parameters
    - Apply `jwtAuth` and `requireRole('ROLE_MANAGE')` for all endpoints
    - _Requirements: 2.5, 3.2, 3.4, 3.5, 4.3, 6.7, 18.1, 18.2_

  - [x] 10.2 Implement inter-service calls for customer details endpoint
    - In `GET /customers/:customerId/details`, call Payment Service (`GET /payments?customerId=X`) and Rental Service (`GET /rentals?customerId=X`) using the shared httpClient utility
    - Forward JWT token and X-Correlation-ID to downstream services
    - Return 503 with standard error format if either downstream service is unavailable
    - Aggregate customer data with payment and rental history in the response
    - _Requirements: 5.1, 5.2, 5.3, 5.5, 5.6, 8.2_

  - [x] 10.3 Write property tests for inter-service communication
    - **Property 5: Downstream Service Unavailability Returns 503** — When downstream service is unreachable, calling service returns 503 with standard error format
    - **Validates: Requirements 5.5**
    - **Property 7: Correlation ID Propagation** — All outgoing inter-service calls include the same X-Correlation-ID from the incoming request
    - **Validates: Requirements 5.6, 10.5**
    - **Property 17: Downstream Error Propagation** — When downstream returns an error, the calling service propagates an appropriate HTTP error status
    - **Validates: Requirements 15.5**

  - [x] 10.4 Write unit tests for Customer Service
    - Test customer CRUD service-layer functions (success and error paths)
    - Test inter-service call logic with nock mocks (success, downstream 404, downstream timeout)
    - Test Joi validation schemas
    - _Requirements: 13.1, 13.2, 13.4_

- [ ] 11. Implement Store Service
  - [ ] 11.1 Implement Store Service routes, controllers, services, and repositories
    - Create routes for stores: `GET /stores`, `POST /stores`, `GET /stores/:storeId`, `PUT /stores/:storeId`, `DELETE /stores/:storeId`, `GET /stores/:storeId/details`
    - Create routes for store staff: `GET /stores/:storeId/staffs`, `GET /stores/:storeId/staffs/:staffId`, `POST /stores/:storeId/staffs/:staffId`, `PUT /stores/:storeId/staffs/:staffId`, `DELETE /stores/:storeId/staffs/:staffId`
    - Create routes for staff: `GET /staffs`, `POST /staffs`, `GET /staffs/:staffId`, `PUT /staffs/:staffId`, `DELETE /staffs/:staffId`, `GET /staffs/:staffId/details`
    - Create routes for reports: `GET /reports/sales/categories`, `GET /reports/sales/stores`
    - Create controllers, services, and repositories using Knex.js scoped to `store_schema` (store, inventory, staff tables)
    - Create Joi validators for store (manager_staff_id, address_id required), staff (first_name, last_name max 45 chars, username max 16 chars, store_id required)
    - Implement pagination via `page`, `size`, `sort` query parameters
    - Apply `jwtAuth` and `requireRole('ROLE_MANAGE')` for read endpoints, `requireRole('ROLE_ADMIN')` for create/update/delete on stores and staff
    - _Requirements: 2.9, 3.2, 3.4, 3.5, 4.6, 4.7, 4.8, 6.10, 18.1, 18.2_

  - [x] 11.2 Write unit tests for Store Service
    - Test store, staff, and inventory CRUD service-layer functions (success and error paths)
    - Test sales report aggregation logic
    - Test Joi validation schemas
    - Test role-based access (ROLE_MANAGE vs ROLE_ADMIN)
    - Mock Knex.js database calls
    - _Requirements: 13.1, 13.2, 13.4_

- [x] 12. Checkpoint - All services implemented
  - Ensure all seven services start, health endpoints respond, and unit tests pass for all services. Ask the user if questions arise.

- [ ] 13. Implement API Gateway and Docker Compose orchestration
  - [ ] 13.1 Create NGINX API Gateway configuration
    - Create `api-gateway/nginx.conf` with path-based routing rules: `/api/v1/login` → auth-service:3001, `/api/v1/films` and `/api/v1/actors` → catalog-service:3002, `/api/v1/customers` → customer-service:3003, `/api/v1/location` → location-service:3004, `/api/v1/payments` → payment-service:3005, `/api/v1/rentals` → rental-service:3006, `/api/v1/stores`, `/api/v1/staffs`, `/api/v1/reports` → store-service:3007
    - Configure NGINX to return HTTP 502 with JSON error body when target service is unreachable
    - Create `api-gateway/Dockerfile` using official NGINX Alpine image
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7, 9.8_

  - [ ] 13.2 Create/update Dockerfiles for all microservices
    - Create a multi-stage Dockerfile in each service directory: build stage installs dependencies, production stage uses Node.js Alpine base image
    - Run Node.js process as non-root user
    - Expose configurable port (default 3000)
    - Include HEALTHCHECK instruction calling `GET /health`
    - Create `.dockerignore` in each service excluding `node_modules`, `tests/`, `*.md`
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6_

  - [ ] 13.3 Create Docker Compose configuration
    - Create `sakila-microservices/docker-compose.yml` defining services for PostgreSQL, all seven microservices, and the API Gateway
    - Configure PostgreSQL service with volume for data persistence, mount `infrastructure/migrations/` and `infrastructure/db-init/` for initialization
    - Configure each microservice with environment variables: `DATABASE_URL`, `JWT_SECRET`, `PORT`, `LOG_LEVEL`, and inter-service URLs (e.g., `PAYMENT_SERVICE_URL=http://payment-service:3005`)
    - Define Docker network for inter-service communication
    - Set service dependency ordering: PostgreSQL starts first, then services, then gateway
    - Expose only the API Gateway port 8080 externally
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 12.7_

  - [ ]* 13.4 Write property test for API Gateway routing
    - **Property 6: API Gateway Returns 502 When Target Unreachable** — When target microservice is unreachable, gateway returns 502 with descriptive error
    - **Validates: Requirements 9.8**

- [ ] 14. Implement cross-cutting property tests and shared middleware tests
  - [ ]* 14.1 Write property tests for health and observability
    - **Property 13: Health Endpoint Response Format** — GET /health returns 200 with JSON containing name (string), status (string), uptime (number)
    - **Validates: Requirements 10.1**
    - **Property 14: Readiness Endpoint Reflects Database Connectivity** — GET /health/ready returns 200 when DB is healthy, 503 when DB is unavailable
    - **Validates: Requirements 10.2, 10.3**
    - **Property 15: Structured Request Logging** — Every incoming request produces a structured JSON log with timestamp, method, path, statusCode, responseTime
    - **Validates: Requirements 10.4**

  - [ ]* 14.2 Write property tests for error handling and validation
    - **Property 3: Role-Based Access Control Enforcement** — For any endpoint and JWT without the required role, service returns 403
    - **Validates: Requirements 6.5**
    - **Property 16: Consistent Error Response Format** — All error responses (4xx/5xx) conform to `{ error: { code, message, details, timestamp } }` format
    - **Validates: Requirements 15.1, 15.2, 15.3, 15.4**

  - [ ]* 14.3 Write property tests for configuration management
    - **Property 19: Configuration Read From Environment Variables** — When env var is set, the service uses that value
    - **Validates: Requirements 3.6, 19.1, 19.2**
    - **Property 20: Default Values for Optional Configuration** — When PORT/LOG_LEVEL are unset, defaults to 3000/info
    - **Validates: Requirements 19.3**
    - **Property 21: Missing Required Configuration Causes Startup Failure** — When DATABASE_URL or JWT_SECRET is missing, service logs error and exits non-zero
    - **Validates: Requirements 19.4**

- [ ] 15. Checkpoint - Full stack orchestration
  - Ensure `docker-compose up` starts all services, the API Gateway routes requests correctly, health endpoints respond through the gateway, and all property tests pass. Ask the user if questions arise.

- [ ] 16. Implement integration tests
  - [ ]* 16.1 Write integration tests for Auth Service
    - Test `POST /login` with valid credentials returns JWT in Authorization header
    - Test `POST /login` with invalid credentials returns 401
    - Test protected endpoint access with valid/invalid/missing tokens
    - Use supertest against running service with real PostgreSQL (testcontainers)
    - Seed auth_schema.authority with known test data before suite, clean after
    - _Requirements: 14.1, 14.2, 14.3, 14.5_

  - [ ]* 16.2 Write integration tests for Catalog Service
    - Test all film and actor CRUD endpoints (correct status codes, response structure, DB state changes)
    - Test pagination, filtering by category/year/rating, and actor search
    - Test authentication enforcement (401/403 scenarios)
    - Seed catalog_schema with known test data
    - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_

  - [ ]* 16.3 Write integration tests for Customer Service
    - Test all customer CRUD endpoints
    - Test `GET /customers/:customerId/details` with mocked downstream Payment and Rental services (via nock)
    - Test authentication enforcement
    - Seed customer_schema with known test data
    - _Requirements: 14.1, 14.2, 14.3, 14.5, 14.6_

  - [ ]* 16.4 Write integration tests for remaining services (Location, Payment, Rental, Store)
    - Test all CRUD endpoints for each service
    - Test pagination and sorting behavior
    - Test authentication and authorization enforcement
    - Test Store Service reports endpoints
    - Seed each service's schema with known test data
    - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_

  - [ ]* 16.5 Write property tests for database migration integrity
    - **Property 11: Data Migration Integrity** — After running migration scripts, records exist in appropriate PostgreSQL schemas with equivalent field values
    - **Validates: Requirements 7.12**
    - **Property 12: Database Schema Isolation** — Each service's DB user cannot access other schemas (permission denied)
    - **Validates: Requirements 8.1, 8.2, 8.4**

- [ ] 17. Implement CI/CD pipeline and ECS deployment readiness
  - [ ] 17.1 Create GitHub Actions CI workflow
    - Create `.github/workflows/ci.yml` in `sakila-microservices/` that triggers on push to main and PRs
    - Run linting (`npm run lint`), unit tests (`npm test`), and integration tests for all services
    - Fail build if unit test coverage falls below 80% for any service
    - Run `npm audit` for security vulnerability scanning
    - Build Docker images for each service and tag with git commit SHA
    - _Requirements: 16.1, 16.2, 16.3, 16.4_

  - [ ] 17.2 Create ECS task definition templates
    - Create ECS task definition JSON files in `infrastructure/ecs/` for each microservice
    - Specify CPU and memory resource limits per service
    - Configure environment variables for DATABASE_URL, JWT_SECRET, and inter-service URLs (using placeholder references for Secrets Manager)
    - Configure health check calling the service's `/health` endpoint
    - Configure awslogs log driver for CloudWatch Logs routing
    - Specify container image reference from ECR
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5_

- [ ] 18. Final checkpoint - Complete system verification
  - Ensure all unit tests pass, all property tests pass, all integration tests pass, Docker Compose stack starts successfully, CI workflow is valid, and ECS task definitions are complete. Ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at key milestones
- Property tests validate universal correctness properties from the design document (21 properties total)
- The monolith codebase remains completely unmodified — all work is in `sakila-microservices/`
- All services share the same layered architecture pattern (routes → controllers → services → repositories)
- Inter-service communication uses synchronous HTTP REST with JWT forwarding and correlation ID propagation
