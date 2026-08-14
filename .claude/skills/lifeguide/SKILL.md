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
- Spring Security (`spring-boot-starter-security`) — `common.config.SecurityConfig`
  now exists (a `SecurityFilterChain` bean). CSRF disabled (stateless JSON
  API). `/api/register` is `permitAll()`; everything else `authenticated()`.
  This is a placeholder shape, not a real auth mechanism yet — no login,
  no JWT/session. Add new public endpoints to the `permitAll()` matcher list
  as they're built; default posture for anything new is authenticated.
  `common.config.SecurityErrorHandlers` is wired in as both the
  `authenticationEntryPoint` and `accessDeniedHandler` — see
  [Security filter-chain errors](#security-filter-chain-errors-confirmed)
  below for why that's needed.
- Jackson 3 (`tools.jackson.*`), not Jackson 2 (`com.fasterxml.jackson.*`) —
  Spring Boot 4 / Spring 7 moved to Jackson 3, which renamed the group ID and
  every package from `com.fasterxml.jackson` to `tools.jackson`. Watch this
  when importing `ObjectMapper` or anything Jackson-related by hand — the old
  `com.fasterxml.jackson.databind.ObjectMapper` import will compile-fail with
  "package does not exist" even though it's second nature muscle memory from
  older Spring Boot versions.
- No Lombok — removed by owner's request (2026-08-14). Write getters,
  constructors, etc. by hand ("traditional way"). Don't reintroduce it.
- More Boot 4 package relocations to watch for (same spirit as the Jackson 3
  note above — old Spring Boot 2/3 muscle memory breaks here):
  - `@WebMvcTest`/`@AutoConfigureMockMvc` moved from
    `org.springframework.boot.test.autoconfigure.web.servlet` to
    `org.springframework.boot.webmvc.test.autoconfigure` (now shipped in a
    dedicated `spring-boot-webmvc-test` module, pulled in transitively by
    `spring-boot-starter-webmvc-test`).
  - `@MockBean`/`@SpyBean` (`org.springframework.boot.test.mock.mockito`) are
    gone. Use `@MockitoBean`/`@MockitoSpyBean` from
    `org.springframework.test.context.bean.override.mockito` instead.

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

A `common` package now exists: global error-handling (see
[Error handling](#error-handling-confirmed)), a `PasswordEncoder` bean, and
`SecurityConfig` (see stack note above).

`feature.auth` has its first full vertical slice — user registration,
`POST /api/register`:
- `domain.User` (aggregate, invariants in the constructor, `register()` /
  `existing()` factory methods) + `domain.UserStatus` (`ACTIVE`/`INACTIVE`).
- `application.UserRepository` (port) + `application.RegisterUserUseCase`
  (rejects duplicate email/username via `AppException(ErrorCode.CONFLICT, ...)`,
  hashes the password via `PasswordEncoder`, saves).
- `infra.UserEntity` (JPA, maps to `users`), `infra.UserJpaRepository`
  (Spring Data), `infra.UserRepositoryAdapter` (implements the port, maps
  entity ↔ domain).
- `presentation.RegisterRequest`/`RegisterResponse` (records; Bean Validation
  annotations on the request, response never carries the password/hash) and
  `AuthController` (`@RequestMapping("/api")`, replaced the old placeholder
  stub that lived at `/auth`).

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
  Security's `AuthenticationException`/`AccessDeniedException`,
  `HttpRequestMethodNotSupportedException` (wrong HTTP verb → 405, not a
  server bug), and a catch-all `Exception` fallback, all through
  `ErrorResponse`. Nothing leaks a raw stack trace or Spring's default error
  page to the frontend.

  > Only the catch-all `Exception` handler calls `log.error(...)`. Every
  > specific handler above it is a routine client-side condition (wrong
  > method, bad input, missing auth) — logging those at `ERROR` would be
  > noise, not a signal something's actually broken. When adding a new
  > `@ExceptionHandler` for another expected Spring/framework exception
  > (e.g. `HttpMediaTypeNotSupportedException`, malformed-JSON body), follow
  > the same rule: map it to the right `ErrorCode`/status, don't log it as
  > an error.

Feature code should throw `AppException`, not invent per-feature exception
types or handlers, unless/until a feature has a real reason to diverge.

## Security filter-chain errors (confirmed)

`@RestControllerAdvice` (`GlobalExceptionHandler`) only catches exceptions
thrown *inside* the `DispatcherServlet` — i.e. once a request has reached a
controller. Spring Security's `authorizeHttpRequests()` matchers reject
requests earlier, in the filter chain, before the request ever gets there —
so a plain `permitAll()`/`authenticated()` denial never reaches
`GlobalExceptionHandler`'s `AuthenticationException`/`AccessDeniedException`
handlers. Left unconfigured, Spring Security's own default entry
point/handler just sets the status code (401/403) and writes an **empty
body** — no `ErrorResponse` JSON, breaking the "always the same envelope"
guarantee for the frontend.

Fixed via `common.config.SecurityErrorHandlers`, a single `@Component`
implementing both `AuthenticationEntryPoint` (401 — not authenticated) and
`AccessDeniedHandler` (403 — authenticated but not allowed), writing the
same `ErrorResponse` JSON shape directly to the servlet response (using the
Jackson 3 `ObjectMapper` bean — see the Jackson note in
[Stack](#stack)). Wired into `SecurityConfig` via
`.exceptionHandling(ex -> ex.authenticationEntryPoint(...).accessDeniedHandler(...))`.

`GlobalExceptionHandler`'s `AuthenticationException`/`AccessDeniedException`
handlers are still meaningful — they'd catch a security exception thrown
from *inside* app code (e.g. method-level `@PreAuthorize`) — but plain
URL-matcher denials are now handled by `SecurityErrorHandlers` instead.

## Testing conventions (confirmed)

`spring-boot-starter-webmvc-test` (already a `test`-scope dependency)
transitively brings in `spring-boot-starter-test` (JUnit 5, Mockito,
AssertJ) plus MockMvc and REST test client support — nothing extra to add
for the tests described here.

Pyramid, mapped onto the feature layers — write most tests as far down this
list as the layer allows, since each step down adds Spring context startup
cost:

- **domain** — plain JUnit, no Spring, no mocks. `domain` has no framework
  dependencies, so it's tested like a plain Java object
  (`feature.auth.domain.UserTest`).
- **application** (use cases) — plain JUnit + Mockito
  (`@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`), mocking
  the `application` port interfaces (e.g. `UserRepository`) and any
  injected framework interface (e.g. `PasswordEncoder`). No Spring context
  (`feature.auth.application.RegisterUserUseCaseTest`).
- **presentation** — `@WebMvcTest(SomeController.class)`, `MockMvc`, the
  use case mocked via `@MockitoBean`. `GlobalExceptionHandler` is picked up
  automatically (it's a `@RestControllerAdvice`, one of `@WebMvcTest`'s
  default-scanned types), so error-mapping is exercised for free. Security
  filters are disabled for these (`@AutoConfigureMockMvc(addFilters =
  false)`) since `SecurityConfig`/`SecurityErrorHandlers` aren't
  component-scanned into a `@WebMvcTest` slice by default and these tests
  aren't about security anyway
  (`feature.auth.presentation.AuthControllerTest`).
- **infra** — `@DataJpaTest` against a real/embedded database. **Not yet
  written for any feature** — this sandbox has no Docker access to run one
  against Postgres and no embedded DB (H2 etc.) dependency has been added.
  Add this once there's an environment that can actually run it.
- **full integration** (`@SpringBootTest`) — sparingly, one true
  end-to-end-over-HTTP test per feature at most, not a substitute for the
  layers above.

> Compiling/running tests in this sandbox needs the same JDK 21 override as
> main-source compiles (see git history / prior session notes — no `javac`
> on the sandbox's default JRE). Maven must run online (not `-o`) the first
> time surefire's plugin dependencies aren't already cached locally.

## Password hashing (confirmed)

`common.config.PasswordEncoderConfig` exposes a `PasswordEncoder` bean
(`BCryptPasswordEncoder` under the hood). Inject `PasswordEncoder`
(the interface), never `BCryptPasswordEncoder` directly. Hash on write
(`encode`), never store/compare raw passwords, compare on login via
`matches(raw, storedHash)` — it's a one-way hash, not reversible encryption.

## Open / pending conventions

Track decisions here as the owner provides them, so future work doesn't
re-litigate them.

- [x] DTO / mapper convention — manual, record-based. Response records carry
      a static `from(domainObject)` factory colocated with the DTO
      (`RegisterResponse.from(User)`); no MapStruct. Not yet exercised: how a
      mapper looks when it needs more than one input or feature-specific
      logic — revisit if that comes up.
- [x] Use-case naming — `XyzUseCase` (e.g. `RegisterUserUseCase`), a
      `@Service` in `application`. Simple use cases take plain method
      parameters (see `register(email, username, rawPassword)`); introduce
      an application-level Command record only once a use case's parameter
      list grows unwieldy — no precedent for that yet.
- [x] Exception handling strategy — global `@RestControllerAdvice` in
      `common`, see [Error handling](#error-handling-confirmed) above.
- [ ] API response shape for success responses (raw DTOs vs wrapped envelope)
- [x] Testing conventions per layer — see
      [Testing conventions](#testing-conventions-confirmed) above.
      `infra`/`@DataJpaTest` still has no example (no Docker in this
      sandbox); revisit once one can actually run.
- [~] Security/auth approach for the `auth` feature — registration is public
      (`permitAll`), everything else defaults to `authenticated()`. No real
      login/token issuance yet; still open which mechanism (JWT? session?)
      backs actual authentication.

## Updating this skill

When the project owner gives new architecture direction, edit this file
directly (`.claude/skills/lifeguide/SKILL.md`) rather than creating a second
doc: replace the assumptions in [Layer responsibilities](#layer-responsibilities-default-assumption--confirmoverride-as-the-owner-clarifies)
with confirmed rules, tick off items in [Open / pending conventions](#open--pending-conventions),
and keep [Current state](#current-state) accurate as features get built out.
