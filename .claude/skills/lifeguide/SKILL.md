---
name: "Lifeguide"
description: "Documents the Lifeguide Spring Boot project's feature-based, layered package architecture (domain/application/infrastructure/presentation) and tech stack conventions. Use when creating a new feature, deciding which package/layer a class belongs in, scaffolding a controller/service/repository/entity, or reviewing whether existing code follows the project's structure."
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
- Lombok

## Package structure: feature-based + layered

Base package: `com.nayibit.lifeguide`

Each **feature** is a top-level package under `feature.<name>`. Inside each
feature, code is split into **layers**:

```
com.nayibit.lifeguide.feature.<feature>.domain
com.nayibit.lifeguide.feature.<feature>.application
com.nayibit.lifeguide.feature.<feature>.infrastructure
com.nayibit.lifeguide.feature.<feature>.presentation
```

### Layer responsibilities (default assumption — confirm/override as the owner clarifies)

- **domain** — core business entities, value objects, domain rules/invariants.
  No Spring, no JPA, no framework annotations. Pure Java.
- **application** — use cases / application services that orchestrate domain
  objects to fulfill a request. Defines the ports (interfaces) that
  infrastructure implements (e.g. a repository port). This is where
  `@Service`-style orchestration lives, but shouldn't leak persistence or web
  concerns.
- **infrastructure** — adapters: JPA entities/repositories, Flyway migrations,
  external API clients, anything implementing an `application` port against a
  concrete technology (Postgres, HTTP, etc).
- **presentation** — the inbound edge: `@RestController`s, request/response
  DTOs, mappers between DTOs and application/domain types, validation
  annotations on inputs.

Dependency direction: `presentation` → `application` → `domain`, and
`infrastructure` → `application`/`domain` (implements application ports).
`domain` depends on nothing else in the app.

> ⚠️ Not yet confirmed with the project owner: exact naming inside each layer
> (e.g. `UseCase` vs `Service` suffix, DTO naming, mapper approach — manual vs
> MapStruct, exception hierarchy, response envelope conventions, testing
> layout). Ask or wait for direction before inventing these; note the answer
> here once given.

## Current state (as of 2026-08-13)

Only one feature is scaffolded, and it's a stub:

```
com.nayibit.lifeguide.feature.auth.presentation.AuthController   (empty)
```

No `domain`, `application`, or `infrastructure` packages exist yet for `auth`
or any other feature. No JPA entities exist yet. One migration exists:
`src/main/resources/db/migration/V1__create_users.sql` (currently creates a
`roles` table — filename/content mismatch flagged but not yet resolved).

## Adding a new feature (scaffold pattern)

1. Create `com.nayibit.lifeguide.feature.<name>` with `domain`, `application`,
   `infrastructure`, `presentation` sub-packages (only the ones actually
   needed for that feature — don't pre-create empty packages).
2. Domain types first (if the feature has real business rules), then the
   application port(s) + use case(s), then the infrastructure adapter(s)
   implementing those ports, then the presentation controller wiring it up.
3. If the feature needs new tables, add a new Flyway migration
   `V<next>__<description>.sql` under `src/main/resources/db/migration/`,
   named for what it actually creates.

## Open / pending conventions

Track decisions here as the owner provides them, so future work doesn't
re-litigate them.

- [ ] DTO / mapper convention (manual mapping vs MapStruct vs record-based)
- [ ] Use-case naming (`XyzUseCase`, `XyzService`, `XyzCommand`/`XyzHandler`?)
- [ ] Exception handling strategy (global `@ControllerAdvice`? per-feature?)
- [ ] API response shape (raw DTOs vs wrapped envelope)
- [ ] Testing conventions per layer (unit vs slice vs integration test placement)
- [ ] Security/auth approach for the `auth` feature

## Updating this skill

When the project owner gives new architecture direction, edit this file
directly (`.claude/skills/lifeguide/SKILL.md`) rather than creating a second
doc: replace the assumptions in [Layer responsibilities](#layer-responsibilities-default-assumption--confirmoverride-as-the-owner-clarifies)
with confirmed rules, tick off items in [Open / pending conventions](#open--pending-conventions),
and keep [Current state](#current-state) accurate as features get built out.
