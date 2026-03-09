# Domain-Driven Design Assessment

**Assessment Date:** March 5, 2026  
**Project:** Spring REST Sakila API Service

---

## 1. Technical Domains

### 1.1 Cross-Cutting / Shared Kernel

**Package:** `com.example.app.common`

The shared kernel provides foundational infrastructure used across all service modules.

| Sub-domain | Components | Purpose |
|---|---|---|
| Constants | `Category`, `Country`, `FilmRating`, `Language`, `SpecialFeature`, `ErrorCode`, `ErrorLevel`, `HalRelation` | Enumerated reference data and system-wide constants |
| Domain Primitives | `FullName` (entity + dto), `NullCheckable`, `Updatable` | Shared value objects and behavioral contracts |
| Mapping | `GenericMapper`, `FullNameMapper`, `DoMapping` | MapStruct-based entity-to-DTO transformation |
| Serialization | `NullToEmptyStringSerializer` | Custom JSON serialization |
| Exception Handling | `GlobalExceptionHandler`, `ResourceNotFoundException`, `ResourceNotAvailableException` | Centralized error handling with structured `ErrorResponseDto` |
| Security | `JwtAuthenticationFilter`, `JwtAuthenticationProvider`, `JwtAuthenticationToken`, `JwtTokenProvider` | JWT-based authentication pipeline |
| Observability | `MdcLoggingFilter` | MDC-based request correlation |
| Utilities | `ExpressionUtils` | Expression evaluation helpers |

### 1.2 Configuration

**Package:** `com.example.app.config`

| Component | Purpose |
|---|---|
| `AuthConfig` | Authentication manager and provider wiring |
| `SecurityFilterConfig` | HTTP security rules, endpoint authorization, CORS |
| `PersistenceConfig` | JPA, Querydsl, Blaze-Persistence configuration |
| `RedisConfig` | Redis connection and caching setup |
| `LoggingFilterConfig` | MDC filter registration |

### 1.3 Service Module Internal Architecture

Each of the 8 service modules follows a consistent layered pattern:

```
services/<module>/
├── assembler/       # HATEOAS RepresentationModelAssembler (DTO → HAL resource)
├── controller/      # REST endpoints with @Secured authorization
├── domain/
│   ├── converter/   # JPA AttributeConverter (enum ↔ DB column)
│   ├── dto/         # Request/Response DTOs, internal DTOs
│   ├── entity/      # JPA entities with @ManyToOne relationships
│   ├── mapper/      # MapStruct entity ↔ DTO mappers
│   └── vo/          # Value objects (e.g., RentalStatus, UserRole)
├── repository/
│   ├── custom/      # Querydsl/Blaze-Persistence custom query interfaces + implementations
│   └── <Repo>.java  # Spring Data JPA repositories
└── service/
    ├── <Svc>.java        # Service interface
    └── <SvcImpl>.java    # Service implementation
```

### 1.4 Data Access Patterns

| Pattern | Implementation |
|---|---|
| ORM | Spring Data JPA with Hibernate 6 |
| Advanced Queries | Querydsl JPA + Blaze-Persistence (projections, filtering, joins) |
| Custom Repositories | `Custom*Repository` interfaces with `*RepositoryImpl` classes |
| Caching | Redis via Spring Data Redis (Lettuce client) |
| Connection Pooling | HikariCP (30 min/max connections) |

### 1.5 API / Presentation Layer Patterns

| Pattern | Implementation |
|---|---|
| REST | Spring Web MVC with `@RestController` |
| Hypermedia | Spring HATEOAS with `RepresentationModelAssembler` |
| Documentation | Spring REST Docs + epages OpenAPI + Postman generation |
| Authorization | Role-based via `@Secured` (ROLE_READ, ROLE_WRITE, ROLE_MANAGE, ROLE_ADMIN) |
| Pagination | Spring Data `Pageable` with `@PageableDefault` |

---

## 2. Business Domains

### 2.1 Domain Map Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                        DVD Rental Business                          │
│                                                                     │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────────────┐    │
│  │   Identity    │   │   Catalog    │   │   Store Operations   │    │
│  │   & Access    │   │              │   │                      │    │
│  │              │   │  Actor       │   │  Store               │    │
│  │  Authority   │   │  Film        │   │  Inventory           │    │
│  │  UserRole    │   │  Category    │   │  Staff Management    │    │
│  │  JWT Auth    │   │  FilmActor   │   │  Reporting           │    │
│  │              │   │  FilmCategory│   │                      │    │
│  └──────┬───────┘   └──────┬───────┘   └──────────┬───────────┘    │
│         │                  │                       │                │
│         │    ┌─────────────┼───────────────────────┤                │
│         │    │             │                       │                │
│  ┌──────┴────┴──┐   ┌─────┴────────┐   ┌─────────┴──────────┐     │
│  │   Customer   │   │   Rental     │   │   Payment          │     │
│  │   Management │   │   Operations │   │   Processing       │     │
│  │              │   │              │   │                    │     │
│  │  Customer    │   │  Rental      │   │  Payment           │     │
│  │  Address     │   │  DVD Rent    │   │  Sales Reports     │     │
│  │  City/Country│   │  DVD Return  │   │                    │     │
│  └──────────────┘   └──────────────┘   └────────────────────┘     │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                    Location (Supporting)                      │   │
│  │         Country → City → Address                             │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Core Domain: Rental Operations

**Package:** `services.rental`  
**Aggregate Root:** `RentalEntity`

This is the heart of the DVD rental business. A rental represents the act of a customer borrowing a physical DVD copy from a store.

**Entities:**
- `RentalEntity` — rental_id, rental_date, return_date, last_update

**Relationships:**
- `Rental` → `InventoryEntity` (which DVD copy, @ManyToOne)
- `Rental` → `CustomerEntity` (who rented, @ManyToOne)
- `Rental` → `StaffEntity` (who processed, @ManyToOne)

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| Rent DVD | `POST /rentals` | Create a rental record (auto-sets rental date if null) |
| Return DVD | `PUT /rentals/return` | Record return date for a rental |
| Get Rental | `GET /rentals/{id}` | View rental details |
| Update Rental | `PUT /rentals/{id}` | Modify rental record |
| Delete Rental | `DELETE /rentals/{id}` | Remove rental record |
| Overdue Check | (service only) | Calculate overdue rentals and fees |

**Domain Logic:**
- Rental date defaults to `now()` if not provided
- Return date is nullable (null = still rented)
- Overdue calculation available at service level (`getOverdueRentalList`, `getOverdueRental`)
- Rental status tracked via `RentalStatus` value object (OUTSTANDING, RETURNED)

### 2.3 Core Domain: Payment Processing

**Package:** `services.payment`  
**Aggregate Root:** `PaymentEntity`

Payments record financial transactions tied to rentals.

**Entities:**
- `PaymentEntity` — payment_id, amount, payment_date, last_update

**Relationships:**
- `Payment` → `CustomerEntity` (who paid, @ManyToOne)
- `Payment` → `StaffEntity` (who received, @ManyToOne)
- `Payment` → `RentalEntity` (for which rental, @ManyToOne, nullable)

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| List Payments | `GET /payments` | Paginated payment list |
| Get Payment | `GET /payments/{id}` | Payment details |
| Payment Details | `GET /payments/{id}/details` | Extended payment info |
| Update Payment | `PUT /payments/{id}` | Modify payment |
| Delete Payment | `DELETE /payments/{id}` | Remove payment |
| Create Payment | (service only) | Create payment linked to customer, staff, rental |

**Domain Logic:**
- Payment creation requires Customer, Staff, and Rental context
- Rental reference is optional (nullable FK)
- No direct `POST /payments` endpoint — payments are created through service orchestration

### 2.4 Supporting Domain: Catalog

**Package:** `services.catalog`  
**Aggregate Roots:** `ActorEntity`, `FilmEntity`

The catalog manages the film library and actor information.

**Entities:**
- `ActorEntity` — actor_id, first_name, last_name
- `FilmEntity` — film_id, title, description, release_year, rental_duration, rental_rate, length, replacement_cost, rating, special_features
- `CategoryEntity` — category_id, name
- `FilmActorEntity` — composite PK (actor_id, film_id), many-to-many join
- `FilmCategoryEntity` — composite PK (film_id, category_id), many-to-many join

**Relationships:**
- `Film` ↔ `Actor` (many-to-many via `FilmActor`)
- `Film` ↔ `Category` (many-to-many via `FilmCategory`)
- `Film` → `LanguageEntity` (primary language, @ManyToOne)
- `Film` → `LanguageEntity` (original language, @ManyToOne, nullable)

**Business Operations:**

Actor operations:
| Operation | Endpoint | Description |
|---|---|---|
| List Actors | `GET /actors` | Paginated actor list |
| Get Actor | `GET /actors/{id}` | Actor details |
| Actor Details | `GET /actors/{id}/details` | Extended info with film list |
| Actor Films | `GET /actors/{id}/films` | Films by actor (filterable by category, year, rating) |
| Actor Film Detail | `GET /actors/{id}/films/{filmId}/details` | Detailed film info for actor |
| Search Actors | `POST /actors/search` | Search by name |
| Add Actor | `POST /actors` | Create actor |
| Add Actor Film | `POST /actors/{id}/films` | Associate actor with film |
| Update Actor | `PUT /actors/{id}` | Modify actor |
| Delete Actor | `DELETE /actors/{id}` | Remove actor |
| Remove Actor Film | `DELETE /actors/{id}/films/{filmId}` | Disassociate actor from film |

Film operations:
| Operation | Endpoint | Description |
|---|---|---|
| List Films | `GET /films` | Filterable by category, year, rating |
| Get Film | `GET /films/{id}` | Film details |
| Film Details | `GET /films/{id}/details` | Extended info with actors |
| Film Actors | `GET /films/{id}/actors` | Actors in film |
| Film Actor | `GET /films/{id}/actors/{actorId}` | Specific actor in film |
| Add Film | `POST /films` | Create film |
| Update Film | `PUT /films/{id}` | Modify film |
| Delete Film | `DELETE /films/{id}` | Remove film |
| Rental Price | (service only) | Get film rental rate |

**Domain Logic:**
- Films have rich metadata: rating (G, PG, PG-13, R, NC-17), special features (Trailers, Commentaries, Deleted Scenes, Behind the Scenes)
- Film filtering supports category, release year, and rating combinations
- Rental pricing is derived from film's `rental_rate` field
- Actor search supports partial name matching (first or last name)

### 2.5 Supporting Domain: Store Operations

**Package:** `services.store`  
**Aggregate Root:** `StoreEntity`

Stores are physical locations that hold inventory and employ staff.

**Entities:**
- `StoreEntity` — store_id, manager_staff_id, address_id
- `InventoryEntity` — inventory_id, film_id, store_id (represents a physical DVD copy)

**Relationships:**
- `Store` → `StaffEntity` (manager, @ManyToOne)
- `Store` → `AddressEntity` (location, @ManyToOne)
- `Inventory` → `FilmEntity` (which film, @ManyToOne)
- `Inventory` → `StoreEntity` (which store, @ManyToOne)

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| List Stores | `GET /stores` | Paginated store list |
| Get Store | `GET /stores/{id}` | Store details |
| Store Details | `GET /stores/{id}/details` | Extended store info |
| Store Staff | `GET /stores/{id}/staffs` | Staff assigned to store |
| Add Store | `POST /stores` | Create store (ADMIN only) |
| Add Store Staff | `POST /stores/{id}/staffs/{staffId}` | Assign staff (ADMIN) |
| Update Store | `PUT /stores/{id}` | Modify store (ADMIN) |
| Delete Store | `DELETE /stores/{id}` | Remove store (ADMIN) |
| Inventory Check | (service only) | Check stock by store and film |

**Reporting Sub-domain:**
| Operation | Endpoint | Description |
|---|---|---|
| Sales by Category | `GET /reports/sales/categories` | Revenue breakdown by film category |
| Sales by Store | `GET /reports/sales/stores` | Revenue breakdown by store |

**Domain Logic:**
- Each store has exactly one manager (unique constraint on manager_staff_id)
- Inventory represents individual physical DVD copies (multiple copies of same film possible)
- Inventory stock check supports store+film combination queries
- Store operations (create, update, delete) require ADMIN role
- Reports aggregate sales data across categories and stores

### 2.6 Supporting Domain: Customer Management

**Package:** `services.customer`  
**Aggregate Root:** `CustomerEntity`

Manages customer accounts and their transaction history.

**Entities:**
- `CustomerEntity` — customer_id, store_id, first_name, last_name, address_id, active, authority_id, create_date

**Relationships:**
- `Customer` → `StoreEntity` (home store, @ManyToOne)
- `Customer` → `AddressEntity` (address, @ManyToOne)
- `Customer` → `AuthorityEntity` (login credentials, @ManyToOne)

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| List Customers | `GET /customers` | Paginated customer list |
| Get Customer | `GET /customers/{id}` | Customer details |
| Customer Details | `GET /customers/{id}/details` | Extended customer info |
| Customer Payments | `GET /customers/{id}/payments` | Payment history (filterable by date range) |
| Customer Rentals | `GET /customers/{id}/rentals` | Rental history (filterable by status, date range) |
| Add Customer | `POST /customers` | Create customer |
| Update Customer | `PUT /customers/{id}` | Modify customer |
| Delete Customer | `DELETE /customers/{id}` | Remove customer |

**Domain Logic:**
- Customers are linked to a home store
- Active/inactive status tracking
- Payment history supports date range filtering
- Rental history supports status filtering (OUTSTANDING, RETURNED) and date range

### 2.7 Supporting Domain: Staff Management

**Package:** `services.staff`  
**Aggregate Root:** `StaffEntity`

Manages employee records for store operations.

**Entities:**
- `StaffEntity` — staff_id, first_name, last_name, address_id, store_id, active, username, authority_id

**Relationships:**
- `Staff` → `StoreEntity` (assigned store, @ManyToOne)
- `Staff` → `AddressEntity` (address, @ManyToOne)
- `Staff` → `AuthorityEntity` (login credentials, @ManyToOne)

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| List Staff | `GET /staffs` | Paginated staff list |
| Get Staff | `GET /staffs/{id}` | Staff details |
| Staff Details | `GET /staffs/{id}/details` | Extended staff info |
| Add Staff | `POST /staffs` | Create staff (ADMIN only) |
| Update Staff | `PUT /staffs/{id}` | Modify staff (ADMIN) |
| Delete Staff | `DELETE /staffs/{id}` | Remove staff (ADMIN) |

**Domain Logic:**
- Staff have usernames for system access
- Active/inactive status tracking
- Staff management operations require ADMIN role
- Staff are assigned to a single store

### 2.8 Generic Sub-domain: Location

**Package:** `services.location`

Provides geographic reference data used by customers, staff, and stores.

**Entities:**
- `CountryEntity` — country_id, country (mapped to `Country` enum)
- `CityEntity` — city_id, city, country_id
- `AddressEntity` — address_id, address, address2, district, city_id, postal_code, phone
- `LanguageEntity` — language_id, name (mapped to `Language` enum)

**Hierarchy:** Country → City → Address

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| List Addresses | `GET /location/addresses` | Paginated address list |
| Get Address | `GET /location/addresses/{id}` | Address details |
| Address Details | `GET /location/addresses/{id}/details` | Extended address info |
| List Cities | `GET /location/cities` | Paginated city list |
| Get City | `GET /location/cities/{id}` | City details |
| CRUD for both | POST, PUT, DELETE | Standard create/update/delete |

**Domain Logic:**
- Country and Language are modeled as enums (static reference data)
- Geometry/spatial data support is partially implemented (commented out)
- Addresses are shared across customers, staff, and stores

### 2.9 Generic Sub-domain: Identity & Access

**Package:** `services.auth`

Handles authentication and authorization.

**Entities:**
- `AuthorityEntity` — authority_id, email, password, authority (SET of roles)

**Roles (Value Object `UserRole`):**
- `ROLE_READ` — Read-only access to catalog data
- `ROLE_WRITE` — Write access
- `ROLE_MANAGE` — Management operations (customers, rentals, payments, reports)
- `ROLE_ADMIN` — Administrative operations (stores, staff)

**Business Operations:**
| Operation | Endpoint | Description |
|---|---|---|
| Login | `POST /login` | Authenticate and receive JWT token |

**Domain Logic:**
- JWT-based stateless authentication
- Role-based access control via `@Secured` annotations
- Roles stored as MySQL SET type (multiple roles per user)
- Customers and Staff both link to Authority for credentials

---

## 3. Entity Relationship Summary

```
                    ┌──────────┐
                    │ Authority│
                    └────┬─────┘
                    ╱         ╲
              ┌────┴───┐  ┌───┴─────┐
              │Customer│  │  Staff  │
              └──┬──┬──┘  └──┬──┬───┘
                 │  │        │  │
    ┌────────────┘  │        │  └────────────┐
    │               │        │               │
┌───┴───┐     ┌─────┴────┐  │          ┌────┴───┐
│Address│     │  Rental  │◄─┘          │ Store  │
└───┬───┘     └────┬─────┘             └───┬────┘
    │              │                       │
┌───┴──┐     ┌────┴─────┐           ┌─────┴─────┐
│ City │     │ Payment  │           │ Inventory │
└───┬──┘     └──────────┘           └─────┬─────┘
    │                                     │
┌───┴────┐                          ┌─────┴─────┐
│Country │                          │   Film    │
└────────┘                          └──┬────┬───┘
                                       │    │
                                 ┌─────┴┐  ┌┴────────┐
                                 │Actor │  │Category │
                                 └──────┘  └─────────┘
```

---

## 4. Cross-Module Dependencies

The following table shows which modules depend on other modules' types:

| Module | Depends On |
|---|---|
| `auth` | (none — standalone) |
| `location` | (none — standalone) |
| `catalog` | `location` (LanguageEntity, LanguageConverter) |
| `store` | `location` (AddressEntity), `staff` (StaffEntity), `catalog` (FilmEntity) |
| `staff` | `location` (AddressEntity), `store` (StoreEntity), `auth` (AuthorityEntity) |
| `customer` | `location` (AddressEntity), `store` (StoreEntity), `auth` (AuthorityEntity), `payment` (DTOs), `rental` (DTOs) |
| `rental` | `customer` (CustomerEntity), `staff` (StaffEntity), `store` (InventoryEntity) |
| `payment` | `customer` (CustomerEntity), `staff` (StaffEntity), `rental` (RentalEntity) |

**Circular dependency risk:** `staff` ↔ `store` (Staff references Store, Store references Staff as manager). This is handled at the JPA level via foreign keys but creates tight coupling.

---

## 5. Bounded Context Analysis

### 5.1 Current State

The application is structured as a modular monolith with 8 service packages. While the package structure suggests bounded contexts, the entity-level cross-references create tight coupling.

### 5.2 Natural Bounded Contexts

Based on the domain analysis, the following bounded contexts emerge naturally:

| Bounded Context | Modules | Justification |
|---|---|---|
| Identity & Access | `auth`, `common.security` | Self-contained authentication/authorization |
| Catalog | `catalog` | Film/actor management with minimal external dependencies |
| Location | `location` | Pure reference data, no business logic dependencies |
| Store Management | `store`, `staff` | Tightly coupled (staff ↔ store circular reference) |
| Customer Management | `customer` | Customer lifecycle, but queries span rental and payment |
| Rental & Payment | `rental`, `payment` | Core business transactions, heavily cross-referencing |

### 5.3 Observations

1. The codebase already follows a module-per-domain structure, which is a good foundation for DDD.
2. Entity-level `@ManyToOne` relationships create direct coupling between modules. In a stricter DDD approach, these would be replaced with ID references and resolved at the service/application layer.
3. The `customer` module's service interface imports DTOs from `payment` and `rental`, indicating that customer is an orchestrating context rather than a pure domain.
4. The `store` module hosts both store management and reporting, which could be separated into distinct contexts.
5. The `common` package acts as a shared kernel but contains both infrastructure concerns (security, filters) and domain concepts (FullName, constants), which could benefit from clearer separation.
6. Reference data (Country, Language, Category, FilmRating) is modeled as enums in the shared kernel rather than as domain entities, which simplifies the model but limits extensibility.

---

## 6. Authorization Model by Domain

| Domain | Read | Write | Manage | Admin |
|---|---|---|---|---|
| Catalog (actors, films) | ✅ | — | ✅ (create/update/delete) | — |
| Customer | — | — | ✅ | — |
| Rental | — | — | ✅ | — |
| Payment | — | — | ✅ | — |
| Reports | — | — | ✅ | — |
| Location | ✅ | — | ✅ (create/update/delete) | — |
| Staff | — | — | ✅ (read) | ✅ (create/update/delete) |
| Store | — | — | ✅ (read) | ✅ (create/update/delete) |

---

## 7. Summary

The Sakila application models a DVD rental business with well-defined domains. The codebase already exhibits many DDD-aligned patterns: module-per-domain packaging, clear aggregate roots, value objects, and a shared kernel. The main areas for improvement are reducing cross-module entity coupling (favoring ID references over direct entity associations), separating the reporting sub-domain from store operations, and clarifying the boundary between infrastructure and domain concerns in the common package.
