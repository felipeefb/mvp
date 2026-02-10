# CRUD API template (controllers + tests)

Use this as a checklist when adding a new CRUD resource (e.g., Project, Product).

## Endpoints
- `POST /api/v1/<resource>` — create
- `GET /api/v1/<resource>` — list (paged)
- `GET /api/v1/<resource>/{id}` — detail
- `PUT /api/v1/<resource>/{id}` — update
- `DELETE /api/v1/<resource>/{id}` — soft delete (if applicable)

## Application layer
- DTOs: `CreateXDto`, `UpdateXDto`, `XDto`, `XListDto`.
- Mapper: `<X>DtoMapper` converts to/from use case commands and domain models.
- Controller: `<X>Controller` (annotated with `@RestController`, `@RequestMapping("/api/v1/<resource>")`), thin delegation to use cases.
- Validation: annotate DTOs with javax/jakarta constraints; expect `ApiExceptionHandler` to translate `BusinessException` to JSON.

## Use case layer
- Commands: `CreateXCommand`, `UpdateXCommand`.
- Ports: `<X>Repository` (interfaces) for persistence.
- Service: `<X>Service` implements create/update/find/list/delete, throws `BusinessException` for conflicts/not-found.

## Infra layer
- Entity: `<X>Entity` extends `AudityEntity` if auditing/soft-delete needed.
- Adapter: maps domain ↔ entity; implements `<X>Repository`.
- Migration: add `V<next>__Create_<resource>_table.sql`.

## Tests (minimum bar)
1) **Controller slice** (`@WebMvcTest` or standalone MockMvc):
   - create returns 201, list returns page, get returns 200, update returns 200, delete returns 204.
   - not-found/conflict paths return correct codes via `BusinessException`.
2) **Service unit**:
   - create fails on duplicate name/id, update fails on conflict/not-found, delete sets soft-delete fields.
3) **Mapper tests**:
   - to/from DTOs and commands.
4) **Integration (Testcontainers)**:
   - repository saves/queries, soft-delete behavior, unique constraints enforced.
5) **Security (happy-path auth)**:
   - one test hitting a secured endpoint with a valid JWT to ensure security chain accepts it.

## Coverage expectation
- Keep module coverage ≥90% (Jacoco gate enforced).
- Prefer small focused tests over brittle end-to-end tests; use stubs/mocks for application layer, Testcontainers for infra.

## Javadoc
- Add brief class-level Javadoc for controllers, mappers, DTOs, and services to align with project style.
