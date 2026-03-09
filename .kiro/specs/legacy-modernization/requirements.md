# Requirements Document

## Introduction

This document defines the requirements for modernizing the Spring REST Sakila monolith application into a microservices architecture. The existing system is a Java 17 Spring Boot 3.0.5 monolith backed by MySQL, serving a DVD rental business with 8 service domains (auth, catalog, customer, location, payment, rental, staff, store). The modernization converts the monolith into domain-bounded Node.js microservices backed by PostgreSQL, containerized with Docker, orchestrated via Docker Compose for local development, and prepared for deployment to Amazon ECS. The existing monolith codebase SHALL remain unmodified; all new microservice code SHALL be created in a separate workspace directory structure where each service is its own independent project with its own git repository.

## Glossary

- **Monolith**: The existing single-deployment Java Spring Boot application containing all service domains
- **Microservice**: An independently deployable Node.js service responsible for a single bounded context
- **Bounded_Context**: A domain-driven design boundary that encapsulates a cohesive set of business capabilities and owns its data
- **API_Gateway**: A reverse-proxy entry point that routes external requests to the appropriate Microservice
- **Catalog_Service**: The Microservice responsible for managing films, actors, categories, and film metadata
- **Customer_Service**: The Microservice responsible for managing customer accounts and profiles
- **Rental_Service**: The Microservice responsible for DVD rental and return operations
- **Payment_Service**: The Microservice responsible for payment processing and transaction records
- **Store_Service**: The Microservice responsible for store operations, inventory management, staff assignment, and sales reporting
- **Auth_Service**: The Microservice responsible for authentication, authorization, and JWT token management
- **Location_Service**: The Microservice responsible for geographic reference data (countries, cities, addresses)
- **Domain_Database**: A PostgreSQL database schema owned exclusively by a single Bounded_Context
- **Inter_Service_Communication**: HTTP-based synchronous communication between Microservices using REST APIs
- **Migration_Script**: A SQL or programmatic script that transforms data from the MySQL Sakila schema to the target PostgreSQL Domain_Database schemas
- **Container_Image**: A Docker image packaging a single Microservice with its runtime dependencies
- **Docker_Compose_Stack**: A docker-compose.yml configuration that orchestrates all Microservices, databases, and supporting infrastructure for local development
- **Health_Check_Endpoint**: An HTTP endpoint exposed by each Microservice that reports its operational status
- **ECS_Task_Definition**: An Amazon ECS configuration describing how a Container_Image runs in the target deployment environment
- **Modernization_Workspace**: A new top-level directory (sakila-microservices/) that contains all Microservice projects, shared infrastructure configurations, and the Docker_Compose_Stack, created separately from the Monolith codebase
- **Service_Project**: An independent Node.js project directory within the Modernization_Workspace that has its own package.json, git repository, Dockerfile, and source code

## Requirements

### Requirement 1: Workspace and Project Structure

**User Story:** As a developer, I want each microservice to be its own independent project in a new workspace separate from the legacy monolith, so that the old code remains untouched and each service can have its own git history, dependencies, and release lifecycle.

#### Acceptance Criteria

1. THE Monolith codebase SHALL remain completely unmodified throughout the modernization process
2. THE Modernization_Workspace SHALL be created as a new top-level directory named sakila-microservices/ at the same level as the Monolith project
3. THE Modernization_Workspace SHALL contain a separate Service_Project directory for each Microservice: auth-service/, catalog-service/, customer-service/, location-service/, payment-service/, rental-service/, and store-service/
4. THE Modernization_Workspace SHALL contain an api-gateway/ directory for the API_Gateway configuration
5. THE Modernization_Workspace SHALL contain an infrastructure/ directory for shared configurations including the Docker_Compose_Stack and Migration_Scripts
6. EACH Service_Project SHALL be initialized as an independent git repository with its own .gitignore, package.json, and README
7. EACH Service_Project SHALL have its own node_modules, test configuration, and Dockerfile so that it can be built, tested, and deployed independently
8. THE Modernization_Workspace SHALL contain a root-level docker-compose.yml that references all Service_Project Dockerfiles to orchestrate the full system for local development

### Requirement 2: Service Decomposition

**User Story:** As a platform engineer, I want the monolith decomposed into domain-bounded microservices, so that each service can be developed, deployed, and scaled independently.

#### Acceptance Criteria

1. THE Monolith SHALL be decomposed into seven Microservices: Auth_Service, Catalog_Service, Customer_Service, Location_Service, Payment_Service, Rental_Service, and Store_Service
2. WHEN a Bounded_Context is defined, THE Microservice SHALL own all business logic, data access, and API endpoints for that context
3. THE Auth_Service SHALL manage the authority table, JWT token issuance, and token validation
4. THE Catalog_Service SHALL manage the actor, film, film_actor, film_category, film_text, category, and language tables
5. THE Customer_Service SHALL manage the customer table and expose customer profile operations
6. THE Location_Service SHALL manage the country, city, and address tables
7. THE Payment_Service SHALL manage the payment table and expose payment CRUD and detail operations
8. THE Rental_Service SHALL manage the rental table and expose rent, return, and rental query operations
9. THE Store_Service SHALL manage the store, inventory, and staff tables, and expose store management, staff assignment, and sales reporting operations

### Requirement 3: Technology Conversion

**User Story:** As a developer, I want each microservice implemented in Node.js with Express, so that the team can leverage JavaScript ecosystem tooling and libraries.

#### Acceptance Criteria

1. THE Microservice SHALL be implemented using Node.js (version 20 LTS or later) with the Express framework
2. THE Microservice SHALL use a layered architecture with separate route, controller, service, and repository modules
3. THE Microservice SHALL use an ORM or query builder (such as Knex.js or Prisma) for database access
4. THE Microservice SHALL expose RESTful JSON API endpoints that preserve the existing API contract paths and HTTP methods from the Monolith
5. WHEN the Monolith exposes a paginated list endpoint, THE corresponding Microservice SHALL support equivalent pagination via query parameters (page, size, sort)
6. THE Microservice SHALL use environment variables for all configuration values including database connection strings, JWT secrets, and service URLs

### Requirement 4: API Contract Preservation

**User Story:** As an API consumer, I want the modernized services to maintain the same API contracts, so that existing clients continue to function without modification.

#### Acceptance Criteria

1. THE Catalog_Service SHALL expose GET /films, POST /films, GET /films/:filmId, PUT /films/:filmId, DELETE /films/:filmId, GET /films/:filmId/actors, GET /films/:filmId/actors/:actorId, and GET /films/:filmId/details endpoints
2. THE Catalog_Service SHALL expose GET /actors, POST /actors, GET /actors/:actorId, PUT /actors/:actorId, DELETE /actors/:actorId, GET /actors/:actorId/details, GET /actors/:actorId/films, POST /actors/:actorId/films, DELETE /actors/:actorId/films/:filmId, and POST /actors/search endpoints
3. THE Customer_Service SHALL expose GET /customers, POST /customers, GET /customers/:customerId, PUT /customers/:customerId, DELETE /customers/:customerId, and GET /customers/:customerId/details endpoints
4. THE Rental_Service SHALL expose GET /rentals, POST /rentals, GET /rentals/:rentalId, PUT /rentals/:rentalId, DELETE /rentals/:rentalId, and PUT /rentals/return endpoints
5. THE Payment_Service SHALL expose GET /payments, GET /payments/:paymentId, PUT /payments/:paymentId, DELETE /payments/:paymentId, and GET /payments/:paymentId/details endpoints
6. THE Store_Service SHALL expose GET /stores, POST /stores, GET /stores/:storeId, PUT /stores/:storeId, DELETE /stores/:storeId, GET /stores/:storeId/details, GET /stores/:storeId/staffs, GET /stores/:storeId/staffs/:staffId, POST /stores/:storeId/staffs/:staffId, PUT /stores/:storeId/staffs/:staffId, and DELETE /stores/:storeId/staffs/:staffId endpoints
7. THE Store_Service SHALL expose GET /staffs, POST /staffs, GET /staffs/:staffId, PUT /staffs/:staffId, DELETE /staffs/:staffId, and GET /staffs/:staffId/details endpoints
8. THE Store_Service SHALL expose GET /reports/sales/categories and GET /reports/sales/stores endpoints
9. THE Auth_Service SHALL expose POST /login endpoint that returns a JWT token in the Authorization header
10. THE Location_Service SHALL expose GET /location/addresses, POST /location/addresses, GET /location/addresses/:addressId, PUT /location/addresses/:addressId, DELETE /location/addresses/:addressId, GET /location/addresses/:addressId/details, GET /location/cities, POST /location/cities, GET /location/cities/:cityId, PUT /location/cities/:cityId, and DELETE /location/cities/:cityId endpoints

### Requirement 5: Cross-Service Communication

**User Story:** As a developer, I want clear inter-service communication patterns, so that services can resolve cross-domain references without direct database coupling.

#### Acceptance Criteria

1. WHEN the Customer_Service needs to display customer payment history, THE Customer_Service SHALL call the Payment_Service REST API to retrieve payment records by customer ID
2. WHEN the Customer_Service needs to display customer rental history, THE Customer_Service SHALL call the Rental_Service REST API to retrieve rental records by customer ID
3. WHEN the Rental_Service creates a rental record, THE Rental_Service SHALL reference customer_id, staff_id, and inventory_id as foreign key identifiers without directly accessing the Customer_Service, Store_Service, or Catalog_Service databases
4. WHEN the Payment_Service creates a payment record, THE Payment_Service SHALL reference customer_id, staff_id, and rental_id as foreign key identifiers without directly accessing other Domain_Databases
5. IF a downstream Microservice is unavailable during an Inter_Service_Communication call, THEN THE calling Microservice SHALL return an HTTP 503 response with a descriptive error message
6. THE Microservice SHALL include a correlation ID header (X-Correlation-ID) in all Inter_Service_Communication requests for distributed tracing

### Requirement 6: Authentication and Authorization

**User Story:** As a security engineer, I want centralized JWT-based authentication with role-based access control, so that all services enforce consistent security policies.

#### Acceptance Criteria

1. THE Auth_Service SHALL authenticate users by validating email and password credentials against the authority table in the Auth_Service Domain_Database
2. WHEN authentication succeeds, THE Auth_Service SHALL issue a signed JWT token containing the user email and assigned roles (ROLE_READ, ROLE_WRITE, ROLE_MANAGE, ROLE_ADMIN)
3. THE Microservice SHALL validate incoming JWT tokens by verifying the signature and expiration before processing protected endpoints
4. WHEN a request lacks a valid JWT token, THE Microservice SHALL return an HTTP 401 response
5. WHEN a request contains a valid JWT token but the user lacks the required role for the endpoint, THE Microservice SHALL return an HTTP 403 response
6. THE Catalog_Service SHALL require ROLE_READ for GET endpoints and ROLE_MANAGE for POST, PUT, and DELETE endpoints
7. THE Customer_Service SHALL require ROLE_MANAGE for all endpoints
8. THE Rental_Service SHALL require ROLE_MANAGE for all endpoints
9. THE Payment_Service SHALL require ROLE_MANAGE for all endpoints
10. THE Store_Service SHALL require ROLE_MANAGE for read endpoints and ROLE_ADMIN for create, update, and delete operations on stores and staff

### Requirement 7: Database Migration to PostgreSQL

**User Story:** As a database engineer, I want the MySQL Sakila schema migrated to PostgreSQL with domain-bounded schemas, so that each service owns its data independently.

#### Acceptance Criteria

1. THE Migration_Script SHALL create separate PostgreSQL schemas for each Bounded_Context: auth_schema, catalog_schema, customer_schema, location_schema, payment_schema, rental_schema, and store_schema
2. THE Migration_Script SHALL convert MySQL AUTO_INCREMENT columns to PostgreSQL SERIAL or BIGSERIAL columns
3. THE Migration_Script SHALL convert MySQL ENUM types to PostgreSQL ENUM types or CHECK constraints
4. THE Migration_Script SHALL convert MySQL SET types (authority roles, special_features) to PostgreSQL array types or junction tables
5. THE Migration_Script SHALL convert MySQL TIMESTAMP with ON UPDATE CURRENT_TIMESTAMP to PostgreSQL TIMESTAMP with trigger-based update tracking
6. THE Migration_Script SHALL convert MySQL TINYINT UNSIGNED, SMALLINT UNSIGNED, and MEDIUMINT UNSIGNED to appropriate PostgreSQL integer types (SMALLINT, INTEGER)
7. THE Migration_Script SHALL preserve all foreign key constraints within a single Domain_Database schema
8. WHEN a foreign key references an entity in a different Bounded_Context, THE Migration_Script SHALL replace the foreign key constraint with an unconstrained integer reference column
9. THE Migration_Script SHALL convert MySQL FULLTEXT indexes on film_text to PostgreSQL tsvector-based full-text search indexes
10. THE Migration_Script SHALL convert MySQL stored procedures (rewards_report, film_in_stock, film_not_in_stock) and functions (get_customer_balance, inventory_held_by_customer, inventory_in_stock) to PostgreSQL equivalents
11. THE Migration_Script SHALL convert MySQL views (customer_list, film_list, staff_list, sales_by_store, sales_by_film_category) to PostgreSQL views within the appropriate Domain_Database schema
12. THE Migration_Script SHALL migrate all existing data from the MySQL Sakila database to the target PostgreSQL Domain_Databases preserving referential integrity

### Requirement 8: Domain Database Isolation

**User Story:** As an architect, I want each microservice to own its database schema exclusively, so that no service can directly access another service's data store.

#### Acceptance Criteria

1. THE Microservice SHALL connect to its own Domain_Database using a dedicated database user with permissions scoped to its schema only
2. THE Microservice SHALL access data from other Bounded_Contexts exclusively through Inter_Service_Communication REST API calls
3. THE Domain_Database SHALL use a separate PostgreSQL schema (or database) per Bounded_Context to enforce isolation at the database level
4. IF a Microservice attempts to query a table outside its Domain_Database schema, THEN THE PostgreSQL database user permissions SHALL deny the query

### Requirement 9: API Gateway Routing

**User Story:** As a platform engineer, I want a single entry point that routes requests to the appropriate microservice, so that clients interact with one base URL.

#### Acceptance Criteria

1. THE API_Gateway SHALL route requests with path prefix /api/v1/login to the Auth_Service
2. THE API_Gateway SHALL route requests with path prefix /api/v1/films and /api/v1/actors to the Catalog_Service
3. THE API_Gateway SHALL route requests with path prefix /api/v1/customers to the Customer_Service
4. THE API_Gateway SHALL route requests with path prefix /api/v1/location to the Location_Service
5. THE API_Gateway SHALL route requests with path prefix /api/v1/payments to the Payment_Service
6. THE API_Gateway SHALL route requests with path prefix /api/v1/rentals to the Rental_Service
7. THE API_Gateway SHALL route requests with path prefix /api/v1/stores, /api/v1/staffs, and /api/v1/reports to the Store_Service
8. IF the target Microservice is unreachable, THEN THE API_Gateway SHALL return an HTTP 502 response with a descriptive error message

### Requirement 10: Health Checks and Observability

**User Story:** As an operations engineer, I want each microservice to expose health and readiness endpoints, so that orchestrators can monitor service status and route traffic appropriately.

#### Acceptance Criteria

1. THE Microservice SHALL expose a GET /health endpoint that returns HTTP 200 with a JSON body containing service name, status, and uptime
2. THE Microservice SHALL expose a GET /health/ready endpoint that returns HTTP 200 when the service can accept traffic and HTTP 503 when the service is not ready
3. WHEN the Microservice cannot connect to its Domain_Database, THE Health_Check_Endpoint /health/ready SHALL return HTTP 503
4. THE Microservice SHALL log all incoming requests with timestamp, HTTP method, path, response status code, and response time in milliseconds using structured JSON logging
5. THE Microservice SHALL propagate the X-Correlation-ID header from incoming requests to all outgoing Inter_Service_Communication calls

### Requirement 11: Containerization

**User Story:** As a DevOps engineer, I want each microservice packaged as a Docker container image, so that services can be deployed consistently across environments.

#### Acceptance Criteria

1. THE Container_Image SHALL use a multi-stage Dockerfile with a build stage and a production stage
2. THE Container_Image production stage SHALL use a Node.js Alpine base image to minimize image size
3. THE Container_Image SHALL run the Node.js process as a non-root user
4. THE Container_Image SHALL expose a single port (configurable via environment variable, defaulting to 3000)
5. THE Container_Image SHALL include a HEALTHCHECK instruction that calls the Health_Check_Endpoint
6. THE Container_Image SHALL use a .dockerignore file to exclude node_modules, test files, and documentation from the build context

### Requirement 12: Docker Compose Local Development

**User Story:** As a developer, I want a Docker Compose configuration that starts all microservices and dependencies locally, so that I can develop and test the full system on my machine.

#### Acceptance Criteria

1. THE Docker_Compose_Stack SHALL define services for all seven Microservices, the API_Gateway, and a PostgreSQL database instance
2. THE Docker_Compose_Stack SHALL configure each Microservice with environment variables for database connection, JWT secret, and Inter_Service_Communication URLs
3. THE Docker_Compose_Stack SHALL use Docker networks to isolate Microservice communication from external access
4. THE Docker_Compose_Stack SHALL use Docker volumes to persist PostgreSQL data across container restarts
5. THE Docker_Compose_Stack SHALL define service dependency ordering so that the PostgreSQL database starts before any Microservice
6. THE Docker_Compose_Stack SHALL expose the API_Gateway port (default 8080) as the single external entry point
7. WHEN a developer runs docker-compose up, THE Docker_Compose_Stack SHALL start all services and the system SHALL be accessible within 60 seconds

### Requirement 13: Unit Testing

**User Story:** As a developer, I want comprehensive unit tests for each microservice, so that business logic correctness is verified in isolation.

#### Acceptance Criteria

1. THE Microservice SHALL include unit tests for all service-layer functions covering success paths and error paths
2. THE Microservice SHALL include unit tests for all data validation and transformation logic
3. THE Microservice SHALL use a JavaScript testing framework (such as Jest or Vitest) for unit test execution
4. THE Microservice SHALL mock database calls and external service calls in unit tests to ensure isolation
5. WHEN a unit test suite is executed, THE test runner SHALL report code coverage and the coverage SHALL meet or exceed 80 percent line coverage for service-layer modules
6. THE Microservice SHALL include unit tests that verify JWT token validation logic including expired tokens, invalid signatures, and missing tokens

### Requirement 14: Integration Testing

**User Story:** As a developer, I want integration tests that verify each microservice works correctly with its database and external dependencies, so that end-to-end behavior is validated.

#### Acceptance Criteria

1. THE Microservice SHALL include integration tests that execute API requests against a running service instance with a real PostgreSQL database
2. THE Microservice SHALL use a test database that is seeded with known test data before each test suite and cleaned after each test suite
3. THE Microservice SHALL include integration tests for all CRUD endpoints verifying correct HTTP status codes, response body structure, and database state changes
4. THE Microservice SHALL include integration tests that verify pagination, filtering, and sorting behavior matches the original Monolith API behavior
5. THE Microservice SHALL include integration tests that verify authentication and authorization enforcement (401 for missing tokens, 403 for insufficient roles)
6. WHEN integration tests involve Inter_Service_Communication, THE test suite SHALL use mocked or stubbed downstream services to ensure test isolation

### Requirement 15: Error Handling

**User Story:** As an API consumer, I want consistent and descriptive error responses from all services, so that I can diagnose and handle failures programmatically.

#### Acceptance Criteria

1. WHEN a requested resource is not found, THE Microservice SHALL return an HTTP 404 response with a JSON body containing error code, message, and timestamp
2. WHEN a request body fails validation, THE Microservice SHALL return an HTTP 400 response with a JSON body containing error code, field-level validation messages, and timestamp
3. IF an unexpected error occurs during request processing, THEN THE Microservice SHALL return an HTTP 500 response with a JSON body containing error code and a generic error message without exposing internal details
4. THE Microservice SHALL use a consistent error response format across all services: { "error": { "code": string, "message": string, "details": array, "timestamp": string } }
5. WHEN a downstream Microservice returns an error during Inter_Service_Communication, THE calling Microservice SHALL propagate an appropriate HTTP error status to the original caller

### Requirement 16: CI/CD Pipeline

**User Story:** As a DevOps engineer, I want an automated CI/CD pipeline that builds, tests, and publishes container images for each microservice, so that deployments are consistent and repeatable.

#### Acceptance Criteria

1. WHEN code is pushed to the main branch, THE CI/CD pipeline SHALL execute linting, unit tests, and integration tests for all affected Microservices
2. WHEN all tests pass, THE CI/CD pipeline SHALL build a Container_Image for each affected Microservice and tag the image with the git commit SHA
3. THE CI/CD pipeline SHALL fail the build if unit test coverage falls below 80 percent for any Microservice
4. THE CI/CD pipeline SHALL run a security vulnerability scan on Node.js dependencies using npm audit or a similar tool
5. THE CI/CD pipeline SHALL publish passing Container_Images to a container registry (such as Amazon ECR)

### Requirement 17: ECS Deployment Readiness

**User Story:** As a platform engineer, I want ECS task definitions and service configurations prepared for each microservice, so that the system can be deployed to Amazon ECS with minimal additional effort.

#### Acceptance Criteria

1. THE ECS_Task_Definition SHALL specify CPU and memory resource limits for each Microservice container
2. THE ECS_Task_Definition SHALL configure environment variables for database connection strings, JWT secrets, and Inter_Service_Communication URLs using ECS task environment or AWS Secrets Manager references
3. THE ECS_Task_Definition SHALL configure a health check that calls the Health_Check_Endpoint of the Microservice
4. THE ECS_Task_Definition SHALL configure log routing to Amazon CloudWatch Logs using the awslogs log driver
5. THE ECS_Task_Definition SHALL specify the Container_Image reference from the container registry for each Microservice

### Requirement 18: Data Validation

**User Story:** As a developer, I want input validation on all API endpoints, so that invalid data is rejected before reaching the service layer.

#### Acceptance Criteria

1. THE Microservice SHALL validate all required fields are present in request bodies before processing
2. THE Microservice SHALL validate that string fields do not exceed the maximum length defined in the database schema (for example, first_name limited to 45 characters, title limited to 128 characters)
3. THE Microservice SHALL validate that numeric fields contain valid numeric values within the expected range (for example, rental_rate as a positive decimal, amount as a positive decimal)
4. THE Microservice SHALL validate that enum fields contain only allowed values (for example, rating accepts only G, PG, PG-13, R, NC-17)
5. WHEN validation fails, THE Microservice SHALL return an HTTP 400 response listing all validation errors for the request

### Requirement 19: Configuration Management

**User Story:** As a developer, I want externalized configuration for all microservices, so that the same container image can run in different environments without rebuilding.

#### Acceptance Criteria

1. THE Microservice SHALL read all environment-specific configuration from environment variables
2. THE Microservice SHALL support the following configuration variables: DATABASE_URL, JWT_SECRET, PORT, LOG_LEVEL, and service-specific Inter_Service_Communication base URLs
3. THE Microservice SHALL provide sensible default values for optional configuration variables (PORT defaults to 3000, LOG_LEVEL defaults to info)
4. IF a required configuration variable (DATABASE_URL, JWT_SECRET) is missing at startup, THEN THE Microservice SHALL log an error message identifying the missing variable and exit with a non-zero exit code
