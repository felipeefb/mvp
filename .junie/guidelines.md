# Project Guidelines for JetBrains AI (Junie) and Plugins

These guidelines describe how this project is organized and how JetBrains AI assistants and related IDE plugins should work with it. Follow this document whenever proposing changes, running tests, or generating code.

## 1. Project Overview
- Name: MVP (Spring Boot Java service)
- Purpose: Basic user/role management with i18n, security, REST APIs, and database migrations.
- Tech stack:
  - Java 17+ (Gradle build)
  - Spring Boot (Web, Security, Data JPA)
  - PostgreSQL (via Testcontainers in tests)
  - Flyway for DB migrations in `src/main/resources/db/migration`
  - OpenAPI/Swagger (`OpenApiConfig`)

## 2. Repository Structure (key paths)
- App source: `src/main/java/com/felipe/belo/mvp`
  - `core`: config, security, exceptions, pagination, auditing
  - `role`: CRUD role domain (controller, service, repository, entity, dto, mapper)
  - `user`: user domain (dto, entity, mapper, repository)
  - `utils`: shared helpers, permissions, i18n constants
- Resources: `src/main/resources`
  - `application.properties`
  - i18n bundles: `i18n/messages*.properties`
  - Flyway: `db/migration/V*.sql`
- Tests: `src/test/java/com/felipe/belo/mvp`
  - Unit, integration, and controller tests (JUnit + Testcontainers)
- Build files: `build.gradle`, `settings.gradle`, Gradle wrapper scripts
- Docker/compose: `compose.yaml` (if applicable for local DB)

## 3. How to Build and Run
- Build: `./gradlew build` (Linux/macOS) or `gradlew.bat build` (Windows)
- Run app: `./gradlew bootRun` or run `MvpApplication` from IDE
- Swagger UI: typically available at `/swagger-ui.html` (depends on springdoc setup)

## 4. Database & Migrations
- DB schema managed by Flyway. Migrations are ordered SQL files under `src/main/resources/db/migration`.
- Do not hand-edit schema outside migrations. For schema changes, add a new `V{N}__Description.sql` file.

## 5. Configuration
- Main properties: `src/main/resources/application.properties`
- Security settings live under `core/config/security/` (e.g., `SecurityConfiguration`, `SecurityProps`).

## 6. Tests: When and How to Run
- Always run tests when changing application code. If only documentation or comments change, skip tests/build.
- Full test suite: `./gradlew test`
- Run a specific test class:
  - `./gradlew test --tests com.felipe.belo.mvp.role.RoleServiceTest`
- Testcontainers: Integration tests spin up PostgreSQL automatically; no manual DB needed.
- In Windows PowerShell, prefer: `gradlew.bat test --tests "com.felipe.belo.mvp.role.RoleServiceTest"`

## 7. Expectations for AI-Assisted Changes
- Prefer minimal, scoped changes; avoid sweeping refactors unless explicitly requested.
- Update or add tests relevant to the code you modify; do not delete tests to make builds pass.
- Follow existing package/module boundaries and naming patterns.
- For REST changes, keep DTOs, mappers, and validation consistent with existing conventions in `role` and `user` modules.
- If a change impacts DB schema, add a new Flyway migration and update affected entities and tests.

## 8. Code Style & Conventions
- Language: Java (17+). Use the same formatting and import order already present in the module you edit.
- Annotations and conventions mirror existing classes nearby. Copy patterns from the most similar existing file.
- Keep comments sparse and purposeful, matching the current codebase style. Use the same language as surrounding code (English/Portuguese as present).
- DTO, Mapper, Service, Repository layering as seen in `role`/`user` packages.

## 9. API & Security
- Controllers under `role` and other domains expose REST endpoints. Consult existing controllers (e.g., `RoleController`) for URI patterns and pagination conventions (`PageResponse`).
- Security: Changes must respect `SecurityConfiguration` and `Permissions` helpers.

## 10. Internationalization (i18n)
- Messages are resolved via `MessageService` and property bundles in `src/main/resources/i18n`.
- Add new keys to all locale files (`messages.properties`, `messages_en_US.properties`, `messages_pt_BR.properties`) when introducing new user-facing messages.

## 11. Pull Requests & Commit Guidelines (for generated changes)
- Keep commits atomic and descriptive.
- Include rationale in PR description, outline tests added/updated, and mention any migration or config changes.

## 12. What JetBrains AI Should Do by Default
- For simple questions: explain without modifying code.
- For code changes:
  - Make the smallest viable edit.
  - Run relevant tests (`gradlew.bat test`) if source code changed.
  - If only docs changed, do not build.
  - On failing tests, assume your change caused it. Fix or revert.
- Use Windows-style paths and PowerShell syntax in commands in this repo.

## 13. Useful Gradle Tasks
- `build` – compile, run tests, package
- `test` – run unit and integration tests
- `bootRun` – run the application

## 14. Contacts & Ownership
- Package root: `com.felipe.belo.mvp`. Use this base for new modules.
- If unsure about domain rules or security implications, prefer asking for clarification before implementing.

Last updated: 2025-12-09