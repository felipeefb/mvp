# Notion setup for MVP

This guide explains how we will use a free Notion workspace to track decisions, runbooks, quality metrics, releases, and incidents for this Spring Boot API. It includes structure, templates, cadences, and automation hooks, plus quick context on Notion features.

## Table of contents
1. [What Notion provides](#what-notion-provides)
2. [Prerequisites](#prerequisites)
3. [Running the local Notion MCP service](#running-the-local-notion-mcp-service)
4. [Current workspace state](#current-workspace-state-aligned-to-this-codebase)
5. [Workspace structure](#workspace-structure)
6. [Databases and why they exist](#databases-and-why-they-exist)
7. [Templates](#templates)
8. [What the app ships today](#what-the-app-ships-today-mirror-in-notion)
9. [Initial seeding from this repo](#initial-seeding-from-this-repo)
10. [Automation plan](#automation-plan)
11. [Cadence and operating rhythm](#cadence-and-operating-rhythm)
12. [Using Codex Notion skills](#using-codex-notion-skills)

---

## What Notion provides
Notion combines documents and relational databases. Databases can hold structured properties (selects, numbers, relations) and rich-text pages. Templates make new entries consistent, and relations let us connect decisions → runbooks → releases → incidents → backlogs. The free plan supports the features we need; large binary files (>5 MB) should be linked externally.

[↑ Back to TOC](#table-of-contents)

## Prerequisites
- Any free Notion workspace (already connected).
- Codex Notion MCP enabled. Two options:
  - Local container (recommended for this repo): `docker compose -f compose.yaml up -d notion-mcp`, then `codex mcp add notion --url http://127.0.0.1:3000/mcp --bearer-token-env-var NOTION_MCP_AUTH_TOKEN --transport http` and ensure `rmcp_client` is enabled. No additional login prompt is required because the MCP server injects `NOTION_TOKEN`; the container sets `OPENAPI_MCP_HEADERS` so Authorization/Notion-Version headers are always sent.
  - Hosted: `codex mcp add notion --url https://mcp.notion.com/mcp`, enable `rmcp_client`, then `codex mcp login notion` and grant workspace access.
- No paid features required; store large artifacts outside Notion and link them.

## Running the local Notion MCP service
1) Copy `.env.example` to `.env` and set:
```
NOTION_TOKEN=your_internal_integration_token
NOTION_MCP_AUTH_TOKEN=shared_secret_between_mcp_and_client
```
(placeholders are already present; replace with workspace-specific values). `.env` is gitignored and must not be committed.
2) Start the service: `docker compose -f compose.yaml up -d notion-mcp`.
3) Verify health: `curl -H "Authorization: Bearer $NOTION_MCP_AUTH_TOKEN" http://localhost:3000/health` (expects JSON with `"status":"healthy"`).
4) Connect Codex: `codex mcp add notion --url http://127.0.0.1:3000/mcp --bearer-token-env-var NOTION_MCP_AUTH_TOKEN --transport http` and restart Codex if prompted.
5) Create the landing page in Notion (e.g., **MVP Platform**) and invite the integration `mvp-codex` with full page/database access.
6) Seed the databases/templates below from this guide.
*Note: `notion-mcp` does not need GPU; the only GPU consumer in `compose.yaml` is `ollama`.*

[↑ Back to TOC](#table-of-contents)

## Current workspace state (aligned to this codebase)
- Parent page: **MVP Plataform** (`2f54326483a78035a283dde7f938c5b3`).
- Databases (already created):
  - **Decisions / ADRs** — Status (Proposed/Accepted/Deprecated), Component (Application/Core/Usecase/Infra/DevOps), Date, Outcome, PR Link, Owner, relations to Runbook/Release/Backlog.
  - **Runbooks** — Category (Build/Deploy/Rollback/Infra), Environment (Local/Dev/Staging/Prod/CI), Last Verified, Steps, Scripts/Links, Dependencies (ADRs).
  - **Releases / Deploys** — Environment (Dev/Staging/Prod), Status (Planned/Deployed/Rolled Back), Date, Checklist Done, Links, relations to Runbook + ADRs.
  - **Tests & Flakes** — Tag (unit/integration/container), Failure Rate %, Last Failure Log, Related ADR, Owner.
  - **Quality Dashboard** — Coverage Target/Actual, Sonar Gate Status (Pass/Warn/Fail/Not Connected), Must-fix Issues (relation to Tests & Flakes), Owner, Due Date, Links.
  - **Incidents / Postmortems** — Severity (SEV-1..3), Impact, Timeline, Root Cause, relations to Release, Runbook, Backlog.
  - **Backlog / Roadmap** — Type (Refactor/Infra/Feature), Priority (P0–P3), Effort (S/M/L), Status, Due Date, relations to ADRs, Quality Item, Incident.
- Seeded pages:
  - ADRs: HS256 resource server choice; modulith boundaries; Flyway migration immutability; Javadoc publishing; Testcontainers strategy; Coverage target ≥90%.
  - Runbooks: Local dev start; Test suite (unit+integration with Testcontainers); Javadoc workflow steps; Placeholder Sonar scan steps; Seeded dev accounts (SUPER_ADMIN/USER).
  - Quality: Coverage baseline (target 90%, actual 0, Sonar “Not Connected”).


[↑ Back to TOC](#table-of-contents)

## Workspace structure
Create a parent page **“MVP Platform”**. Under it, add the linked databases listed below, plus a short landing blurb that links to each DB view (Board/List/Timeline) and common filters (e.g., “Active ADRs”, “Pending Releases”, “Must-fix quality issues”).

[↑ Back to TOC](#table-of-contents)

## Databases and why they exist
1) **Decisions / ADRs** — captures architectural choices so we stop rediscovering context; links to PRs and releases.
   - Key properties: Status (Proposed/Accepted/Deprecated), Component/Module, Date, Summary, Outcome, Owner, Links (PR, Release, Runbook).
2) **Runbooks** — repeatable steps for build/deploy/rollback; drives reliability.
   - Properties: Category (Build/Deploy/Rollback/Infra), Environment, Last Verified (date), Owner, Steps, Dependencies (ADR relation), Scripts/Links.
3) **Quality Dashboard** — single view of coverage, Sonar gate status, and must-fix items.
   - Properties: Coverage Target, Coverage Actual, Sonar Gate Status, Must-fix Issues (relation), Owner, Due Date, Links (coverage report, Sonar URL).
4) **Tests & Flakes** — tracks flaky/slow tests so we can reach and keep >90% coverage without instability.
   - Properties: Test Id (module/class#method), Tag (unit/integration/container), Failure Rate %, Last Failure Log (CI link), Owner, Related ADR/Issue.
5) **Releases / Deploys** — records what shipped, where, and with which checks; links to runbooks and ADRs.
   - Properties: Version/Tag (or commit), Environment, Date, Status, Checklist Done, Links (PRs, Runbook, ADRs, Artifact URL).
6) **Incidents / Postmortems** — captures issues, root causes, and actions tied to releases and runbooks.
   - Properties: Severity, Impact, Timeline, Root Cause, Actions (relation to Backlog), Related Release/Runbook, Owner.
7) **Backlog / Roadmap** (optional but useful) — holds refactors/infra tasks that come out of incidents and ADRs.
   - Properties: Type (Refactor/Infra/Feature), Priority, Effort (S/M/L), Status, Links to ADRs/Quality items.

Relations:
- Releases ↔ ADRs ↔ Runbooks; Incidents ↔ Release + Runbook; Quality items ↔ Tests/Flakes; Backlog tasks ↔ ADRs and Quality actions.

[↑ Back to TOC](#table-of-contents)

## Templates
- **ADR template**: Context → Decision → Consequences → Links.
- **Runbook template**: Purpose, Preconditions, Steps, Verification, Rollback, Last Verified.
- **Release checklist**: Pre-checks, CI status, Migration applied, Rollback ready, Smoke tests.
- **Incident template**: Summary, Timeline, Impact, Root cause, Actions, Lessons, Links.
- **Coverage review**: Target vs Actual, Top issues, Owners, Due dates.

[↑ Back to TOC](#table-of-contents)

## What the app ships today (mirror in Notion)
- Seed data (`DataInitializer`): creates roles `SUPER_ADMIN` (all permissions) and `USER` (ROLE_LIST, USER_LIST) plus users `admin@admin.com` / `admin` and `user@user.com` / `user` with those roles.
- Auth: `/api/v1/auth/login` issues HS256 JWTs (claims: `sub` email, `uid`, `role`, `permissions`); `/api/v1/auth/refresh` exchanges refresh tokens. Resource server secret in `application.properties` (`security.jwt.secret`), access token 1440 min, refresh token 10080 min. `JwtRefreshFilter` accepts expired access tokens if a valid `X-Refresh-Token` is present and returns rotated tokens in `X-New-Access-Token` / `X-New-Refresh-Token`.
- Security: Swagger (`/swagger-ui/**`) and `/api/v1/auth/**` are public; everything else requires auth and maps permissions from token claims.
- Persistence: Flyway migrations V1–V6 create role/user tables, session tables, auditing and soft-delete columns, and enable `unaccent`.
- CI/CD: `.github/workflows/ci.yml` runs `./gradlew test jacocoTestReport jacocoTestCoverageVerification` (coverage gate 90%), uploads Jacoco HTML, and (when `SONAR_HOST_URL`/`SONAR_TOKEN` secrets are set) runs SonarCloud via the standalone scanner with `-Dsonar.qualitygate.wait=true` so PRs fail if the Sonar quality gate fails.
- Sonar: configured for `felipeefb_mvp` (org `felipeefb`). Add secrets `SONAR_HOST_URL=https://sonarcloud.io` and `SONAR_TOKEN` to enable analysis; coverage fed via Jacoco XML (`**/build/reports/jacoco/test/jacocoTestReport.xml`).

[↑ Back to TOC](#table-of-contents)

## Initial seeding from this repo
- ADRs: JWT HS256 resource server choice; modulith boundaries; Flyway migration ordering; Javadoc publishing via GH Actions; Testcontainers strategy for integration tests; coverage target ≥90%.
- Runbooks: local dev start (`./gradlew :application:bootRun` + `docker compose -f compose.yaml up -d` for Postgres), test suite (`./gradlew :application:test`), Javadoc workflow steps, placeholder Sonar scan steps (to be filled when Sonar is wired), seeded dev accounts (SUPER_ADMIN with all permissions; USER with ROLE_LIST/USER_LIST).
- Quality Dashboard: target coverage 90%, keep actual above target (see Jacoco report), Sonar “Connected” when CI secrets are set.
- Releases: log future main/development deploys (AWS target TBD).
- Incidents: empty until first CI/deploy issue; template ready.

[↑ Back to TOC](#table-of-contents)

## Automation plan
- GitHub Action step to push coverage and Sonar gate status into the Quality DB after CI (Jacoco XML + Sonar API).
- Optional: action to append release records on successful deploy (env, commit, artifacts).
- Optional: job to file flake entries when tests fail intermittently (parse CI logs).

[↑ Back to TOC](#table-of-contents)

## Cadence and operating rhythm
- Weekly: review Quality dashboard, assign/track must-fix items; verify runbook “Last Verified” dates.
- Monthly: close/refresh ADRs; archive deprecated decisions.
- Per release: fill Release checklist, link runbook + ADRs, create post-release note.

[↑ Back to TOC](#table-of-contents)

## Using Codex Notion skills
- `notion-spec-to-implementation`: build plans from specs and link tasks.
- `notion-research-documentation`: summarize Sonar rules/quality findings into briefs.
- `notion-meeting-intelligence`: generate agendas/pre-reads for release/quality reviews.
- `notion-knowledge-capture`: log decisions/notes quickly into the right DB.

[↑ Back to TOC](#table-of-contents)
