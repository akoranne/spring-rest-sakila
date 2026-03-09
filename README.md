# Spring REST Sakila - Modernization Project

This repository contains both the legacy monolithic application and its modernized microservices architecture.

## Project Intent

This project demonstrates the modernization of a legacy Spring Boot monolithic application (Sakila REST API) into a cloud-native microservices architecture using Kiro AI assistance.

## Repository Structure

### `/archives`
Contains the original monolithic Spring Boot application. This legacy codebase has been preserved for reference and comparison purposes. The monolithic application includes:
- Complete REST API for the Sakila database
- JWT authentication
- Redis caching
- Spring Data JPA with QueryDSL
- HAL/HATEOAS support

### `/sakila-microservices`
Contains the modernized microservices architecture:
- [API Gateway](sakila-microservices/api-gateway/) - Entry point and routing
- [Auth Service](sakila-microservices/auth-service/) - Authentication and authorization
- [Catalog Service](sakila-microservices/catalog-service/) - Film and inventory management
- [Customer Service](sakila-microservices/customer-service/) - Customer management
- [Location Service](sakila-microservices/location-service/) - Address, city, and country data
- [Payment Service](sakila-microservices/payment-service/) - Payment processing
- [Rental Service](sakila-microservices/rental-service/) - Rental transactions
- [Store Service](sakila-microservices/store-service/) - Store and staff management
- [Infrastructure](sakila-microservices/infrastructure/) - Shared infrastructure configs

See [sakila-microservices/README.md](sakila-microservices/README.md) for detailed documentation.

## Modernization Goals

- Break down the monolith into domain-driven microservices
- Implement modern cloud-native patterns
- Improve scalability and maintainability
- Enable independent service deployment
- Adopt containerization and orchestration
- Implement observability and monitoring

## Documentation

Project documentation and analysis:
- [Initial Assessment](docs/initial_assessment.md) - Analysis of the legacy codebase
- [DDD Assessment](docs/ddd_assessment.md) - Domain-driven design evaluation
- [Prompts](docs/prompts.md) - AI prompts and guidance used in modernization

## Getting Started

### Legacy Application
See [archives/README.md](archives/README.md) for instructions on running the original monolithic application.

### Microservices
See [sakila-microservices/README.md](sakila-microservices/README.md) for instructions on running the microservices architecture.

---

## References

This modernization project is based on the original monolithic application:
- [spring-rest-sakila](https://github.com/codejsha/spring-rest-sakila) by codejsha

*This modernization project is being developed with assistance from Kiro AI.*
