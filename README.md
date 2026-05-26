# studio-api

Backend API for the studio portfolio. Built with **Spring Boot 3.5 + Java 17 + Gradle + JPA + PostgreSQL**.

## Requirements

- Java 17 (toolchain enforced by Gradle)
- PostgreSQL 14+ reachable at the configured `DB_URL` (only required to `bootRun`; tests use H2)

## Configuration

Runtime config is driven by environment variables. Defaults are wired in
`src/main/resources/application.yml`:

| Variable      | Default                                        | Notes                             |
|---------------|------------------------------------------------|-----------------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/studio`      | JDBC URL                          |
| `DB_USERNAME` | `studio`                                       |                                   |
| `DB_PASSWORD` | _(empty)_                                      | Set via env, never commit secrets |
| `SERVER_PORT` | `8080`                                         |                                   |

For local overrides create an **uncommitted** `application-local.yml` next to
`application.yml` and activate with `--spring.profiles.active=local`. The
`.gitignore` blocks `.env*` and `application-local.*` from being checked in.

## Run

```bash
./gradlew bootRun                    # starts on :8080 against the configured DB
./gradlew test                       # runs unit + slice tests against H2 (test profile)
./gradlew build                      # full build incl. tests
```

## Endpoints

| Method | Path          | Description                  |
|--------|---------------|------------------------------|
| GET    | `/api/health` | Liveness payload, no auth    |

Sample response:

```json
{
  "status": "success",
  "data": {
    "status": "UP",
    "service": "studio-api",
    "time": "2026-05-26T11:50:00Z"
  }
}
```

## Package layout

```
com.studio.api
├── StudioApiApplication.java   Spring Boot entry point
├── common/                     Cross-cutting types
│   ├── ApiResponse.java        Generic {status, data, error} envelope
│   └── ErrorPayload.java
├── config/                     Reserved for cross-cutting configuration
└── health/
    └── HealthController.java   GET /api/health
```

## Testing strategy

- `StudioApiApplicationTests` boots the full context against the `test` profile,
  which swaps in H2 + `ddl-auto=create-drop` (no Postgres required).
- `HealthControllerTest` is a `@WebMvcTest` slice that validates the envelope
  shape of the `/api/health` response.

## Conventions

- Branches: `feature/{TICKET_ID}-{short-description}` cut from `dev`.
- Commits: Conventional Commits (`chore:`, `feat:`, `fix:`, `docs:`, `test:`).
- Workflow lives in
  [studio-docs](https://github.com/youngsoosoo/studio-docs) — read the
  collaboration doc before starting a new ticket.
