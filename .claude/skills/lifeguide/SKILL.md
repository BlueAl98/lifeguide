---
name: "Lifeguide"
description: "Documents the Lifeguide Spring Boot project's feature-based, layered package architecture (domain/application/infra/presentation) and tech stack conventions. Use when creating a new feature, deciding which package/layer a class belongs in, scaffolding a controller/service/repository/entity, or reviewing whether existing code follows the project's structure."
---

# Lifeguide

Architecture reference for the `lifeguide` Spring Boot project. This is a **living
document** — the structure is intentionally light right now because the project is
young; append to it as conventions get decided rather than guessing ahead of the
project owner. See [Updating This Skill](#updating-this-skill) at the bottom.

## Stack

- Java 26, Spring Boot 4.1.0 (Spring 7)
- Maven (single module)
- Spring Data JPA / Hibernate
- PostgreSQL (via Docker Compose locally, `docker-compose.yml` at repo root)
- Flyway for migrations — requires **both** `spring-boot-starter-flyway` (the
  autoconfiguration module, split out of Spring Boot core as of Boot 4) and
  `flyway-database-postgresql` (the dialect). `flyway-core` alone is not enough
  to trigger migrations in Boot 4 — see git history for the incident where this
  bit us.
- Spring Security (`spring-boot-starter-security`) — dependency only so far;
  no `SecurityConfig`/filter chain wired up yet. Note: adding this starter
  alone locks every endpoint behind basic auth with a random generated
  password logged at startup, until a config overrides it.
- No Lombok — removed by owner's request (2026-08-14). Write getters,
  constructors, etc. by hand ("traditional way"). Don't reintroduce it.

## Package structure: feature-based + layered

Base package: `com.nayibit.lifeguide`

Each **feature** is a top-level package under `feature.<name>`. Inside each
feature, code is split into **layers**:

```
com.nayibit.lifeguide.feature.<feature>.domain
com.nayibit.lifeguide.feature.<feature>.application
com.nayibit.lifeguide.feature.<feature>.infra
com.nayibit.lifeguide.feature.<feature>.presentation
```

> Confirmed: the layer is named `infra`, not `infrastructure` — that's what's
> actually on disk under `feature.auth`. Use `infra` for new features too.

Cross-feature/shared code that isn't specific to one feature (error handling,
future cross-cutting concerns) lives in a top-level `common` package, sibling
to `feature.*`: `com.nayibit.lifeguide.common.<concern>`. See
[Error handling](#error-handling-confirmed) below for the first thing living
there.

### Layer responsibilities (default assumption — confirm/override as the owner clarifies)

- **domain** — core business entities, value objects, domain rules/invariants.
  No Spring, no JPA, no framework annotations. Pure Java.
- **application** — use cases / application services that orchestrate domain
  objects to fulfill a request. Defines the ports (interfaces) that
  `infra` implements (e.g. a repository port). This is where
  `@Service`-style orchestration lives, but shouldn't leak persistence or web
  concerns.
- **infra** — adapters: JPA entities/repositories, Flyway migrations,
  external API clients, anything implementing an `application` port against a
  concrete technology (Postgres, HTTP, etc).
- **presentation** — the inbound edge: `@RestController`s, request/response
  DTOs, mappers between DTOs and application/domain types, validation
  annotations on inputs.

Dependency direction: `presentation` → `application` → `domain`, and
`infra` → `application`/`domain` (implements application ports).
`domain` depends on nothing else in the app.

> ⚠️ Not yet confirmed with the project owner: exact naming inside each layer
> (e.g. `UseCase` vs `Service` suffix, DTO naming, mapper approach — manual vs
> MapStruct, exception hierarchy, response envelope conventions, testing
> layout). Ask or wait for direction before inventing these; note the answer
> here once given.

## Current state (as of 2026-08-14)

Only one feature is scaffolded, and it's a stub:

```
com.nayibit.lifeguide.feature.auth.presentation.AuthController   (empty)
```

No `domain`, `application`, or `infra` packages exist yet for `auth`
or any other feature. No JPA entities exist yet. Two migrations exist:
- `V1__create_users.sql` — creates `users` (id, email, username, password,
  status, created_at). This previously had a filename/content mismatch (it
  created a table literally named `roles` with user columns); fixed in place
  since it had never been applied to any real database.
- `V2__create_roles_and_user_roles.sql` — creates `roles` (id, name) and the
  `user_roles` join table (composite PK `user_id`+`role_id`, both FKs
  `ON DELETE CASCADE`) for a many-to-many user↔role relationship.
- `V3__seed_roles.sql` — seeds `roles` with `ADMIN` and `USER`
  (`ON CONFLICT DO NOTHING`, safe to re-run/rerun on a DB that already has
  them).

`spring-boot-starter-security` was added to `pom.xml` (dependency only, no
config yet — see stack note above on the basic-auth default). A `common`
package now exists with a global error-handling setup — see
[Error handling](#error-handling-confirmed).

## Adding a new feature (scaffold pattern)

1. Create `com.nayibit.lifeguide.feature.<name>` with `domain`, `application`,
   `infra`, `presentation` sub-packages (only the ones actually
   needed for that feature — don't pre-create empty packages).
2. Domain types first (if the feature has real business rules), then the
   application port(s) + use case(s), then the infra adapter(s)
   implementing those ports, then the presentation controller wiring it up.
3. If the feature needs new tables, add a new Flyway migration
   `V<next>__<description>.sql` under `src/main/resources/db/migration/`,
   named for what it actually creates.

## Error handling (confirmed)

Global, not per-feature. Lives in `com.nayibit.lifeguide.common`:

- `common.exception.ErrorCode` — enum of every error code; each entry owns
  its `HttpStatus` and a default message.
- `common.exception.AppException` — the exception features throw. Takes an
  `ErrorCode` alone (uses the code's default message) or `ErrorCode` +
  a custom message — that custom-message overload is how a specific
  endpoint/call-site overrides the wording without inventing a new code or
  a new exception subclass.
- `common.presentation.ErrorResponse` — the one JSON shape returned for
  every error: `code`, `message`, `status`, `path`, `timestamp`.
- `common.presentation.GlobalExceptionHandler` — single `@RestControllerAdvice`
  that maps `AppException`, Bean Validation errors
  (`MethodArgumentNotValidException`, `ConstraintViolationException`), Spring
  Security's `AuthenticationException`/`AccessDeniedException`, and a
  catch-all `Exception` fallback, all through `ErrorResponse`. Nothing leaks
  a raw stack trace or Spring's default error page to the frontend.

Feature code should throw `AppException`, not invent per-feature exception
types or handlers, unless/until a feature has a real reason to diverge.

## Open / pending conventions

Track decisions here as the owner provides them, so future work doesn't
re-litigate them.

- [ ] DTO / mapper convention (manual mapping vs MapStruct vs record-based)
- [ ] Use-case naming (`XyzUseCase`, `XyzService`, `XyzCommand`/`XyzHandler`?)
- [x] Exception handling strategy — global `@RestControllerAdvice` in
      `common`, see [Error handling](#error-handling-confirmed) above.
- [ ] API response shape for success responses (raw DTOs vs wrapped envelope)
- [ ] Testing conventions per layer (unit vs slice vs integration test placement)
- [ ] Security/auth approach for the `auth` feature (dependency added,
      no `SecurityConfig` yet)

## Updating this skill

When the project owner gives new architecture direction, edit this file
directly (`.claude/skills/lifeguide/SKILL.md`) rather than creating a second
doc: replace the assumptions in [Layer responsibilities](#layer-responsibilities-default-assumption--confirmoverride-as-the-owner-clarifies)
with confirmed rules, tick off items in [Open / pending conventions](#open--pending-conventions),
and keep [Current state](#current-state) accurate as features get built out.
