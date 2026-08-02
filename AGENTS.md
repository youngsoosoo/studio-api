# Repository Instructions

## Database safety

- Never connect to, inspect, query, migrate, or modify an actual local, development, staging, or production database.
- Never run database clients or database-changing commands, including `psql`, JDBC scripts, `docker compose exec postgres`, AWS RDS Data API calls, or migration tools against a live database.
- When a schema or data change is requested, write a reviewable SQL file under `db/changes/` and stop after static review. Execution is always left to the user or an explicitly authorized deployment system.
- Do not add application-startup seeders, `CommandLineRunner` data initialization, or other automatic production-data mutation.
- Tests must use mocks or synthetic, non-personal test fixtures only.
- Never store real credentials, production dumps, or personal portfolio content in this repository.
