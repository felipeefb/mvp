### MVP — Spring Boot Starter API

This project is a minimal-yet-complete Spring Boot REST API template with production-minded defaults: modular package layout, DTO + Mapper pattern, JPA with Flyway migrations, auditing, soft delete, JWT security, i18n error messages, OpenAPI docs, and a working example module (`role`) plus `user` support.

Use it as a starting point to add new domains/entities quickly while keeping consistency and testability.

**[📚 View API Documentation (Javadoc)](https://felipeefb.github.io/mvp/)**

---

### Table of contents

- [Tech stack](#tech-stack)
- [How the project is organized](#how-the-project-is-organized)
- [Architecture and conventions](#architecture-and-conventions)
- [System design](#system-design)
- [Security and permissions](#security-and-permissions)
- [Auditing and soft delete](#auditing-and-soft-delete)
- [Database & migrations](#database--migrations)
- [Internationalization (i18n)](#internationalization-i18n)
- [Implementing a new feature (DDD flow)](#implementing-a-new-feature-ddd-flow)
- [How to implement a new entity (step-by-step)](#how-to-implement-a-new-entity-step-by-step)
- [Creating a new Flyway migration](#creating-a-new-flyway-migration)
- [Automatically generating migrations (plugins & tools)](#automatically-generating-migrations-plugins--tools)
- [Javadoc: writing and generating API docs](#javadoc-writing-and-generating-api-docs)
- [Running locally](#running-locally)
- [Testing](#testing)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [License](#license)

### Tech stack

- Java 21, Spring Boot 3
- Spring Data JPA (Hibernate)
- Flyway for database migrations
- Spring Security as a Resource Server, JWT (HS256)
- MapStruct for mapping between Entities and DTOs
- Testcontainers + JUnit for integration testing (PostgreSQL)
- i18n message resolution (`MessageSource`) for user-facing errors
- OpenAPI 3 via springdoc (`/swagger-ui.html`)

---

### How the project is organized

This repository uses Gradle subprojects, but keeps a single source tree. Modules are separated by package:

- `src/main/java/com/felipe/belo/mvp/application` — Spring Boot entry point and web layer (controllers, auth)
- `src/main/java/com/felipe/belo/mvp/usecase` — services, DTOs, and mappers (business orchestration)
- `src/main/java/com/felipe/belo/mvp/infra` — entities and repositories (persistence)
- `src/main/java/com/felipe/belo/mvp/core` — cross-cutting concerns and shared utilities

Packages are grouped by feature (modulith style), with `core` for cross-cutting concerns and `utils` for shared helpers.

- `com.felipe.belo.mvp.application` — Spring Boot entry point and web layer
- `com.felipe.belo.mvp.application.role` — role controllers, DTOs, and mappers
- `com.felipe.belo.mvp.application.user` — user controllers, DTOs, and mappers
- `com.felipe.belo.mvp.core` — shared config/components
  - `config` — OpenAPI config, auditing, and base security props
    - `security` — `SecurityProps`
    - `module` — `AuditAwareImpl` (auditor provider)
  - `component` — `MessageService` (i18n) 
  - `domain` — domain models and `AudityEntity` (auditing base for JPA)
  - `exception` — `BusinessException`, `ApiExceptionHandler`
- `com.felipe.belo.mvp.usecase.core` — shared application services (e.g., `CurrentUserService`)
- `com.felipe.belo.mvp.usecase.role` — role use cases and commands
- `com.felipe.belo.mvp.infra.role` — role persistence (JPA entities, repositories, adapters)
- `com.felipe.belo.mvp.usecase.user` — user use cases and commands
- `com.felipe.belo.mvp.infra.user` — user persistence (JPA entities, repositories, adapters)
- `com.felipe.belo.mvp.core.utils` — helpers and `permissions.Permissions` enum

Resources:
- `src/main/resources/application.properties` — environment configuration
- `src/main/resources/db/migration/V*.sql` — Flyway migrations
- `src/main/resources/i18n/messages*.properties` — localized messages

Tests:
- `src/test/java/...` — unit and integration tests, including Testcontainers configuration

---

### Architecture and conventions

This project follows clean architecture/DDD boundaries with explicit ports and adapters.

1. Domain model
   - Plain Java objects in `core.domain.model`
2. Use case
   - Commands + ports + services in `usecase`
   - Throw `BusinessException(HttpStatus, i18nKey, args...)` for domain errors
3. Infrastructure
   - JPA `@Entity` models in `infra` extending `AudityEntity`
   - Spring Data repositories + adapters implement use case ports
   - Framework beans live in `infra.config` (e.g., `PasswordEncoder`, JPA scanning)
4. Application
   - `@RestController` under `application`
   - Request/response DTOs mapped to domain models via mappers

Cross-cutting:
- Security & Auth: JWT-based auth; most endpoints require authentication (see `SecurityConfiguration`).
- i18n: `MessageService` resolves localized messages by keys in `I18nConstants` and `messages*.properties`.
- OpenAPI: documented at `/swagger-ui.html` and `/v3/api-docs` with i18n descriptions resolved per request locale.
- Database: PostgreSQL; Flyway manages schema.

---

### System design

This codebase is organized around clean architecture and DDD-friendly boundaries:

- **Core (domain)** holds pure models (`Role`, `User`) and shared utilities.
- **Usecase** defines commands, ports (interfaces), and services that implement business rules.
- **Infra** provides framework integrations (JPA entities, Spring Data repositories, adapters, `PasswordEncoder`, and persistence config).
- **Application** exposes controllers and API DTOs/mappers that translate HTTP requests into use case commands.

Why this split:
- Keeps domain logic framework-agnostic and testable.
- Makes infrastructure replaceable (JPA ↔ other stores) via ports/adapters.
- Keeps controllers thin and consistent across features.

---

### Implementing a new feature (DDD flow)

Use this checklist when adding a new feature (e.g., `Project`):

1. **Domain model (core)**  
   - Add `Project` to `src/main/java/com/felipe/belo/mvp/core/domain/model`.
2. **Use case (usecase)**  
   - Add commands like `CreateProjectCommand`, ports like `ProjectRepository`, and a service implementing the use cases.
3. **Infrastructure (infra)**  
   - Add `ProjectEntity` (`@Entity`) and `ProjectJpaRepository`.  
   - Add an adapter implementing the use case port, mapping between domain and entity.
4. **Application (application)**  
   - Add controller, request/response DTOs, and a mapper to convert to/from domain.
5. **Permissions**  
   - Add new permissions in `src/main/java/com/felipe/belo/mvp/core/utils/permissions/Permissions.java` grouped by entity (e.g., `PROJECT_READ`, `PROJECT_CREATE`, ...).  
   - Update any seed data or roles if needed (`DataInitializer`).
6. **Tests**  
   - Use case tests for business rules (`usecase` service).  
   - Infra tests for repository behavior (`infra` + Testcontainers).  
   - Controller tests for API contracts (`application`).

When in doubt, mirror the existing `role` and `user` implementations for structure and naming.

---

### Security and permissions

- Security is configured in `application.config.security.SecurityConfiguration`.
  - Stateless sessions, CSRF disabled for APIs
  - JWT Resource Server using a symmetric secret (HS256)
  - Open routes: Swagger and `/api/v1/auth/**` (see `AuthController`)
  - Everything else requires authentication
- Users are loaded from the database via `UserDetailsService` (by email).
- `Permissions` enum (in `utils.permissions`) centralizes known permissions. The `Role` entity maps `Set<Permissions>` as an element collection (`role_permission` table). You can extend the enum as your domain grows.

Tip: Tie controller access to roles/permissions using Spring Security method-level annotations (`@PreAuthorize`) if/when needed.

---

### Permissions checklist template

Use this template to keep permissions consistent and discoverable for every new feature:

```
// <ENTITY> permissions
<ENTITY>_READ,
<ENTITY>_CREATE,
<ENTITY>_UPDATE,
<ENTITY>_DELETE,
<ENTITY>_LIST,
```

Where to add:
- `src/main/java/com/felipe/belo/mvp/core/utils/permissions/Permissions.java`
- Seed default roles in `DataInitializer` if needed (e.g., add permissions to `SUPER_ADMIN`)

Why this helps:
- Enforces a consistent CRUD + LIST permission model.
- Keeps `@PreAuthorize("hasAuthority('<ENTITY>_<ACTION>')")` checks predictable.
- Makes it obvious what to add when creating a new feature.

Example usage in controllers:
- `@PreAuthorize("hasAuthority('PROJECT_READ')")`
- `@PreAuthorize("hasAuthority('USER_LIST')")`

Permissions endpoints:
- `GET /api/v1/permissions` returns all permissions grouped by entity.

Example response:
```
{
  "ROLE": ["READ", "CREATE", "UPDATE", "DELETE", "LIST"],
  "USER": ["READ", "CREATE", "UPDATE", "DELETE", "LIST"]
}
```

### Auditing and soft delete

- `@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")` is enabled in `MvpApplication`.
- `AudityEntity` provides `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate` fields auto-populated by Spring Data.
- Soft delete pattern:
  - Entities include `deletedAt` and `deletedBy` fields
  - `@SQLRestriction("deleted_at is null")` transparently hides deleted rows
  - Services set `deletedAt` and `deletedBy` on delete

---

### Database & migrations

- Flyway is enabled (`spring.flyway.enabled=true`). Place versioned SQL in `src/main/resources/db/migration`.
- PostgreSQL specifics:
  - Migrations enable useful extensions (e.g., `unaccent`) and create required tables/constraints.
  - Example: `RoleRepository.findByNormalizedName` uses `unaccent` for accent-insensitive matching.
- Local dev database settings are in `application.properties`.
- `compose.yaml` provides a ready Postgres service for local development.

---

### Internationalization (i18n)

- Messages are defined under `src/main/resources/i18n/messages*.properties`.
- Use `I18nConstants` and `MessageService` to produce localized, stable error codes/messages returned by `ApiExceptionHandler`.
- Default locale and locale resolution are configured in `application.properties`.

---

### How to implement a new entity (step-by-step)

The fastest way is to mirror the `role` module structure and naming.

Assume your new entity is `Product`, with packages split by module:
- `com.felipe.belo.mvp.infra.product` for entity/repository
- `com.felipe.belo.mvp.usecase.product` for DTOs/mapper/service
- `com.felipe.belo.mvp.application.product` for controllers

1) Create the database table via Flyway
- Add `V<N>__Create_product_table.sql` under `src/main/resources/db/migration`.
- Include columns for auditing and soft delete similar to `role`:
  - `created_by UUID`, `created_date TIMESTAMP`, `last_modified_by UUID`, `last_modified_date TIMESTAMP`
  - `deleted_by UUID`, `deleted_at TIMESTAMP`
- Add constraints and useful indexes (unique, lookups).

2) Create the JPA Entity
- File: `src/main/java/com/felipe/belo/mvp/infra/product/entity/Product.java`
```
@Entity
@Table(name = "product", uniqueConstraints = {@UniqueConstraint(name = "uk_product_name", columnNames = "name")})
@SQLRestriction("deleted_at is null")
public class Product extends AudityEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // getters/setters
}
```

3) Define DTOs
- Folder: `src/main/java/com/felipe/belo/mvp/usecase/product/dto`
- Suggested records:
  - `CreateProductDto(name, ...)` — request body for POST
  - `UpdateProductDto(name, ...)` — request body for PUT
  - `ProductDto(id, name, ...)` — response for detail
  - `ProductListDto(id, name, ...)` — response for list

4) Create MapStruct mapper
- File: `src/main/java/com/felipe/belo/mvp/usecase/product/mapper/ProductMapper.java`
```
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toEntity(CreateProductDto dto);
    Product toEntity(UpdateProductDto dto);
    Product toEntity(ProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(UpdateProductDto updateDto, @MappingTarget Product entity);

    ProductDto toDto(Product entity);
    List<ProductListDto> toDtoList(Iterable<Product> entities);
}
```

5) Repository
- File: `src/main/java/com/felipe/belo/mvp/infra/product/repository/ProductRepository.java`
```
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByDeletedAtIsNull();
    // Optional: normalized name search like RoleRepository
}
```

6) Service (business rules + soft delete)
- File: `src/main/java/com/felipe/belo/mvp/usecase/product/service/ProductService.java`
```
@Service
public class ProductService {
    private final ProductRepository repo;
    private final ProductMapper mapper;
    private final CurrentUserService currentUserService;

    public ProductService(ProductRepository repo, ProductMapper mapper, CurrentUserService currentUserService) { ... }

    @Transactional
    public ProductDto create(CreateProductDto dto) { /* check constraints, save, return */ }

    @Transactional
    public ProductDto update(UUID id, UpdateProductDto dto) { /* load, validate, partialUpdate, save */ }

    public ProductDto findById(UUID id) { /* load or throw BusinessException */ }

    public List<ProductListDto> findAll() { return mapper.toDtoList(repo.findAllByDeletedAtIsNull()); }

    @Transactional
    public void delete(UUID id) {
        Product entity = repo.findById(id).orElse(null);
        if (entity == null) throw new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_PRODUCT_NOT_FOUND);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy(currentUserService.getCurrentUser().map(UserEntity::getId).orElse(null));
        repo.save(entity);
    }
}
```

7) Controller
- File: `src/main/java/com/felipe/belo/mvp/application/product/controller/ProductController.java`
```
@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {
  private final ProductService service;
  @PostMapping public ResponseEntity<ProductDto> create(@RequestBody @Validated CreateProductDto dto) { ... }
  @GetMapping public ResponseEntity<List<ProductListDto>> list() { ... }
  @GetMapping("/{id}") public ResponseEntity<ProductDto> get(@PathVariable UUID id) { ... }
  @PutMapping("/{id}") public ResponseEntity<ProductDto> update(@PathVariable UUID id, @RequestBody @Validated UpdateProductDto dto) { ... }
  @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { ... }
}
```

8) Messages (i18n)
- Add keys to `i18n/messages.properties` (and localized files):
```
message.product.not.found=Product not found
message.product.name.exists=Product name already exists
```
- Consider adding constants to `I18nConstants` for type-safety.

9) Permissions (optional)
- Extend `utils.permissions.Permissions` with actions for your entity if you use granular checks:
```
PRODUCT_READ, PRODUCT_CREATE, PRODUCT_UPDATE, PRODUCT_DELETE, PRODUCT_LIST
```
- If you enforce permissions per endpoint, use `@PreAuthorize` or `AuthorizationManager` strategies accordingly.

10) Tests
- Unit tests for mapper and service
- Repository integration tests with Testcontainers (see existing `role` tests for patterns)
- Controller tests covering happy-path and error cases

11) Wire OpenAPI
- Endpoints will appear automatically in Swagger UI at `/swagger-ui.html`.

Checklist summary:
- [ ] Flyway migration created
- [ ] Entity + soft delete + auditing
- [ ] DTOs and MapStruct mapper
- [ ] Repository
- [ ] Service with business rules and `BusinessException`
- [ ] Controller with `@Validated`
- [ ] i18n messages
- [ ] Optional permissions/authorization rules
- [ ] Tests

---

### Creating a new Flyway migration

Follow these steps to create safe, reproducible schema changes for all developers and environments.

1) Choose the correct filename and version
- Location: `src/main/resources/db/migration`
- Versioned migrations use the pattern `V<N>__Description.sql` (double underscore before the description).
- Check the latest version in the repo. Currently present: `V1` … `V6`. Your next file should be `V7__<your_change>.sql` (e.g., `V7__Create_product_table.sql`).
- Keep descriptions short, lowercase, and snake_case. Examples: `add_soft_delete_to_order`, `create_order_item_table`.

2) Write migration SQL that is idempotent in spirit
- Prefer explicit `CREATE TABLE IF NOT EXISTS` only for extensions or helper objects; for tables use `CREATE TABLE` with clear constraints. Avoid destructive changes unless absolutely required.
- Add constraints, indexes, and comments in the same file to keep schema cohesive.
- Example snippet:
```
-- Enable extension only once (ok to be repeatable across envs)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE product (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    created_by UUID,
    created_date TIMESTAMP,
    last_modified_by UUID,
    last_modified_date TIMESTAMP,
    deleted_by UUID,
    deleted_at TIMESTAMP
);

ALTER TABLE product
  ADD CONSTRAINT uk_product_name UNIQUE (name);
```

3) When to use repeatable migrations
- Repeatable migrations are named `R__<Name>.sql` and re-run when their checksum changes (e.g., for reference data).
- In this template, schema changes should use versioned migrations (`V...`). Use `R__seed_reference_data.sql` only for safe, re-runnable seeds.

4) Ordering, out-of-order, and conflicts
- Flyway executes migrations by version order. Never reuse or renumber published versions.
- If your branch diverges and both add `V7`, bump yours to `V8` after pulling main. Do not edit an already-merged migration to “fix the number”.
- If you must apply a migration that is numerically lower than the current baseline, enable out-of-order locally with `-Dflyway.outOfOrder=true` only for recovery; do not commit this setting.

5) Baseline and checksums
- The project sets `spring.flyway.baseline-on-migrate=true`, allowing migration on an existing DB without `V1` applied.
- If a migration’s SQL changes after someone already ran it, Flyway will report a checksum mismatch. Do not “repair” blindly. Prefer creating a new follow-up migration (e.g., `V8__Fix_product_index.sql`). Use `flyway repair` only when you are absolutely sure and in a controlled environment.

6) Local workflow for new developers
- Start PostgreSQL: `docker compose -f compose.yaml up -d`.
- Run the app: Gradle will connect to the DB and Flyway will apply pending migrations on startup.
- Verify with logs or inspect the DB using any client. Testcontainers-based tests will also apply migrations automatically in isolated containers.

7) Production safety tips
- Never drop columns/tables without a prior deprecation period. Prefer: add new column -> backfill -> switch reads/writes -> drop in a later migration.
- Keep migrations small and frequent. One logical change per file is easier to review and revert.
- Always include indexes and constraints to maintain integrity and performance.

Checklist:
- [ ] Version number is the next available
- [ ] Naming is clear and concise (uses double underscore)
- [ ] Includes all related constraints and indexes
- [ ] Tested locally against Docker Postgres and via tests
- [ ] No retroactive edits to existing migrations

---

### Automatically generating migrations (plugins & tools)

Goal: reduce manual SQL errors by generating migration scripts from model or database diffs. Pick the option that best fits your workflow and always review the output before committing.

1) IntelliJ IDEA Database tools (schema diff → SQL)
- When to use: you have a local dev database and want to generate SQL from changes you made manually in DB or from a reference schema.
- Steps:
  1. Open View → Tool Windows → Database. Add a data source for your local Postgres (see `application.properties` / `compose.yaml`).
  2. In Database view, right‑click your database → Jump to Schema → select the target schema (usually `public`).
  3. Right‑click the schema → SQL Scripts → SQL Generator OR use “Compare” to diff against another data source or a DDL file.
  4. Choose objects to include (tables, indexes, constraints) and set “Dialect: PostgreSQL”.
  5. Generate the SQL, review, then copy it into a new `src/main/resources/db/migration/V<N>__description.sql`.
- Tips:
  - Keep only forward‑moving statements (CREATE/ALTER/ADD). Avoid DROP in the same file unless safe as per the migration guidelines.
  - Use the same naming/versioning rules described in the previous section.

2) JPA Buddy (IntelliJ plugin) → Flyway scripts from Entity changes
- When to use: you primarily evolve the schema via JPA entities and want the plugin to produce Flyway SQL.
- Steps:
  1. Install “JPA Buddy” plugin (File → Settings → Plugins → Marketplace).
  2. In the project view, right‑click the module → JPA Structure. Use “Generate Migration” (select Flyway + PostgreSQL).
  3. JPA Buddy will analyze entity differences and produce an SQL script. Save it as `V<N>__description.sql` under `db/migration`.
- Notes:
  - Review the script for column types, defaults, and constraints; adjust to match project conventions (auditing/soft‑delete, naming, indexes).
  - Great for typical add/rename column/table flows. Complex data migrations may still need hand‑written SQL.

3) Redgate Flyway Desktop (GUI) — model and generate versioned scripts
- When to use: you want a dedicated Flyway workflow with visual diffing and drift detection.
- Steps (high‑level):
  1. Install Flyway Desktop from Redgate. Connect it to your local database.
  2. “Baseline” from your current schema so it matches the existing `V1..Vn` state.
  3. Apply changes to the connected DB (or update a model), then let the tool “Generate Migrations”.
  4. Configure the output folder to `src/main/resources/db/migration` and commit the generated `V<N>__...sql`.
- Notes:
  - Works well on teams who prefer a visual DB‑first approach but still output standard Flyway SQL files.

4) Ariga Atlas (CLI) — diff DB ↔ HCL and emit Flyway‑compatible SQL
- When to use: you want a repeatable, scriptable diff engine that can target Postgres and output SQL designed for migration directories.
- Install: https://atlasgo.io (Windows via `winget install ariga.atlas`).
- Common flows:
  - DB → migration (diff current DB to desired schema):
    ```
    atlas schema inspect -u "postgres://mvp:secret123@localhost:5432/mvpdatabase?sslmode=disable" > schema.hcl
    atlas migrate diff \
      --dir "file://src/main/resources/db/migration" \
      --to "file://schema.hcl" \
      --format "{{ sql . }}"
    ```
  - Directory validation (ensure migrations replay cleanly):
    ```
    atlas migrate validate --dir "file://src/main/resources/db/migration" \
      -u "postgres://mvp:secret123@localhost:5432/mvpdatabase?sslmode=disable"
    ```
- Notes:
  - Atlas can also manage a declarative HCL schema under version control.
  - Ensure the SQL matches project conventions (names, indexes, auditing columns).

5) migra (Python) — DB‑to‑DB diff for PostgreSQL
- When to use: you have two Postgres instances (source/target) and want a SQL patch between them.
- Install: `pipx install migra` (or `pip install migra`), and `pipx install psycopg[binary]`.
- Example:
  ```
  migra --unsafe \
    postgresql://mvp:secret123@localhost:5432/mvpdatabase \
    postgresql://mvp:secret123@localhost:5432/clean_reference_db \
    > src/main/resources/db/migration/V7__sync_changes.sql
  ```
- Notes:
  - “--unsafe” allows destructive changes; prefer avoiding it unless you understand the impact.
  - You can spin up a “clean reference” DB by restoring only up‑to‑V<N-1> migrations and diffing against your changed DB.

6) Hibernate DDL export (code‑first) — generate SQL, then copy to a Flyway file
- When to use: entities are the source of truth and you want Hibernate to output the “update” SQL.
- Approach:
  - Temporarily enable SQL generation in a one‑off run configuration or via a Gradle task that boots Spring with Hibernate schema update in “script” mode (do not commit `ddl-auto=update`).
  - Capture the SQL output and paste it into `V<N>__description.sql`.
- References:
  - Hibernate tooling docs: https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#schema-generation
  - Spring Boot properties for DDL generation (use only locally, not committed):
    - `spring.jpa.hibernate.ddl-auto=none` (default here)
    - `spring.jpa.properties.hibernate.hbm2ddl.auto=update` (local only)
    - `spring.jpa.properties.hibernate.hbm2ddl.schema_generation.script.append=true`
    - `spring.jpa.properties.hibernate.hbm2ddl.schema_generation.scripts.action=update`
    - `spring.jpa.properties.hibernate.hbm2ddl.schema_generation.scripts.create-target=build/schema-create.sql`
    - `spring.jpa.properties.hibernate.hbm2ddl.schema_generation.scripts.drop-target=build/schema-drop.sql`

Review & safety checklist (for all tools):
- [ ] Version and file name follow the project convention (`V<N>__snake_case.sql`).
- [ ] Only forward, safe changes are included (avoid accidental drops in the same migration).
- [ ] All required constraints and indexes are present.
- [ ] Auditing and soft‑delete columns are preserved/added when introducing new tables.
- [ ] Script tested locally against Docker Postgres and via tests (Testcontainers).

Tip: Even when auto‑generated, migrations are source code. Keep them small, readable, and reviewed like any other PR.

---

### Javadoc: writing and generating API docs

1) Writing Javadoc in code
- Add class-level Javadoc to public classes/interfaces to describe their purpose and how they are used. Example (see existing `RoleService` and `AudityEntity` styles):
```
/**
 * Explains the responsibility of the class and key behaviors.
 * Include notes about transactions, exceptions, and usage.
 */
public class ProductService { ... }
```
- Add method-level Javadoc for non-trivial methods, public APIs, and overrides where behavior deviates.
- Useful tags:
  - `@param` describe each parameter
  - `@return` describe the return value
  - `@throws` list exceptions and when they occur
  - `@since` to mark when an API was introduced
  - `@implNote` for important implementation details
  - `@see` references to related types/methods

2) Style guidelines
- Keep comments concise and up to date; avoid restating code. Focus on intent, invariants, and side effects.
- Mirror the existing tone used in this project’s classes. English is preferred for cross-team readability.

3) Generating Javadoc with Gradle
- Generate docs:
  - Windows: `gradlew.bat :application:javadoc`
  - Unix/macOS: `./gradlew :application:javadoc`
- Output location: `application/build/docs/javadoc/index.html`.
- You can open this file in your browser to browse the generated API documentation.

4) IDE assistance
- Most IDEs (IntelliJ IDEA) auto-template Javadoc and show real-time previews. Use inspections to spot missing or malformed Javadoc.

Checklist:
- [ ] Public classes and key methods have meaningful Javadoc
- [ ] Tags added for params/returns/exceptions where relevant
- [ ] Docs generated successfully via Gradle

### Running locally

Java:
- Use JDK 21 (project targets Java 21). If your IDE uses another JDK, update the run configuration and reimport Gradle.

Database (Docker):
- Ensure Docker is running. From the project root:
```
docker compose -f compose.yaml up -d
```

Application:
- Windows: `gradlew.bat :application:bootRun`
- Unix/macOS: `./gradlew :application:bootRun`
- If the app can't find `compose.yaml` in your IDE, ensure the run working directory is the repo root or set:
  - `-Dspring.docker.compose.file=<repo-root>/compose.yaml`

Swagger UI:
- http://localhost:8080/swagger-ui.html

JWT secret & dev users:
- Configured via `application.properties`. Update `security.jwt.secret` as needed.

---

### Testing

- Run all tests:
  - Windows: `gradlew.bat :application:test`
  - Unix/macOS: `./gradlew :application:test`
- Integration tests use Testcontainers (PostgreSQL). Docker must be available.

---

### Contributing

- Start from `main` and keep changes focused per PR.
- Follow the module layout under `src/main/java/com/felipe/belo/mvp/{core,usecase,infra,application}`.
- Use the existing naming patterns:
  - DTOs: `CreateXDto`, `UpdateXDto`, `XDto`, `XListDto`
  - Tests: `*Test` for unit tests, `*IT` for integration tests
  - Migrations: `V<number>__<desc>.sql`
- Run tests locally before opening a PR:
  - Windows: `gradlew.bat :application:test`
  - Unix/macOS: `./gradlew :application:test`
- When adding DB changes, include a Flyway migration and mention it in the PR.
- Keep commit messages short and imperative, describing the module or feature.
- PRs should include: purpose, key changes, tests run, and any API or migration impact.

See `AGENTS.md` for a compact contributor guide and repository expectations.

---

### Troubleshooting

- Flyway errors at startup
  - Ensure the database exists and credentials in `application.properties` match Postgres from `compose.yaml`.
  - Check migration version ordering and naming (`V1__...`, `V2__...`).
- JWT validation failures
  - Ensure client sends `Authorization: Bearer <token>`
  - Confirm the token is HS256-signed with the configured secret (`security.jwt.secret`).
- 404 or 409 business responses
  - These come from `BusinessException` with i18n messages. See `ApiExceptionHandler` output and `messages.properties`.

---

### License

Apache-2.0 (see headers in OpenAPI config). Update as appropriate for your project.
