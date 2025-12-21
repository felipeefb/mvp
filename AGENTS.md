# Repository Guidelines

## Project Structure & Module Organization
This is a Spring Boot 3 (Java 21) API template split into Gradle subprojects but sharing a single source tree.
- `src/main/java/com/felipe/belo/mvp/application` hosts the Spring Boot entry point and web layer.
- `src/main/java/com/felipe/belo/mvp/core` contains cross-cutting concerns (security, auditing, exceptions, utils).
- `src/main/java/com/felipe/belo/mvp/usecase` holds commands, ports, and use case services.
- `src/main/java/com/felipe/belo/mvp/infra` contains JPA entities, repositories, and adapters.
- DTOs and mappers live under `src/main/java/com/felipe/belo/mvp/application`.
- `src/main/resources/db/migration` contains Flyway SQL migrations (`V<version>__<desc>.sql`).
- `src/main/resources/i18n` contains localized message bundles.
- Tests live under `src/test/java/...` with unit and integration coverage.

## Build, Test, and Development Commands
- `./gradlew :application:bootRun` (or `gradlew.bat :application:bootRun`) starts the API locally.
- `./gradlew :application:test` runs unit/integration tests (JUnit 5 + Testcontainers).
- `./gradlew :application:javadoc` generates API docs at `application/build/docs/javadoc/index.html`.
- `docker compose -f compose.yaml up -d` starts the local PostgreSQL database.

## Coding Style & Naming Conventions
- Java 21, Spring Boot conventions. Prefer clear, descriptive names.
- DTOs: `CreateXDto`, `UpdateXDto`, `XDto`, `XListDto`.
- Tests: `*Test` for unit tests, `*IT` for integration tests.
- Migrations: `V<number>__<Snake_or_title_case>.sql` (e.g., `V7__Create_product_table.sql`).
- Comments/Javadoc: keep concise, explain intent, and mirror existing tone.

## Testing Guidelines
- Use JUnit 5 with Spring Boot test starters.
- Integration tests use Testcontainers + PostgreSQL; Docker must be available.
- Follow existing patterns in `src/test/java/com/felipe/belo/mvp`.
- Prefer coverage across controller, service, and repository layers.

## Commit & Pull Request Guidelines
- Recent commit history uses short, imperative sentences with sentence case.
- Keep commits focused; include code context in the message (e.g., module name).
- PRs should include: purpose, key changes, testing performed, and any API impacts.
- If changes affect endpoints or migrations, mention them explicitly.

## Security & Configuration Notes
- JWT settings live in `src/main/resources/application.properties`.
- Flyway migrations must be applied in order; avoid editing old migrations.
- OpenAPI docs available at `/swagger-ui.html` when running locally.
