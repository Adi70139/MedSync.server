Docker Compose for Prescripto (local dev)

This project includes a Dockerfile and a docker-compose.yaml to run the Postgres (pgvector), Redis, and the Spring Boot app.

Files added/updated
- Dockerfile - multi-stage build to produce a runnable JAR
- .dockerignore - omits target/, uploads/ etc.
- docker-compose.yaml - services: pgvector (postgres+pgvector), redis, app

How it works
1. docker-compose build will build the `app` image using the Dockerfile. The Dockerfile runs `./mvnw package` inside the image and copies the resulting JAR into the runtime image.
2. docker-compose up will start Postgres (pgvector), Redis, and then the `app` service.
3. The app is configured via environment variables provided in `docker-compose.yaml`:
   - SPRING_DATASOURCE_URL -> jdbc:postgresql://pgvector:5432/postgres
   - SPRING_DATASOURCE_USERNAME -> postgres
   - SPRING_DATASOURCE_PASSWORD -> 1234
   - SPRING_REDIS_HOST, SPRING_REDIS_PORT -> redis:6379

Notes about configuration
- The project `application.properties` currently points to a hosted Redis (Upstash) and a local Postgres on localhost:5433. When running with docker-compose, the environment variables above override the properties at runtime.
- If you need to enable PGVector support (spring.ai.vectorstore.pgvector.enabled), configure it via env var SPRING_AI_VECTORSTORE_PGVECTOR_ENABLED=true (and set appropriate DB schema/table access). For now it's disabled in the compose to avoid changing DB schema.

Commands
- Build images and start services (foreground):

```bash
docker-compose build --parallel
docker-compose up
```

- Start in detached mode:

```bash
docker-compose up -d --build
```

- Tail logs for the app:

```bash
docker-compose logs -f app
```

- Stop and remove containers and volumes (local dev):

```bash
docker-compose down -v
```

Troubleshooting
- If the app cannot connect to Postgres, ensure `pgvector` container is healthy and that the app container is using the correct JDBC URL above.
- If you keep an external Redis configured in `application.properties`, ensure env var `SPRING_REDIS_URL` or `SPRING_REDIS_HOST/PORT` override it.
- If builds are slow you can `./mvnw -DskipTests package` locally and then use a simpler Dockerfile that only copies the local target/*.jar into the runtime image.

Security
- The Docker Compose file contains cleartext DB credentials for local dev only. Do not use this in production.

Let me know if you want:
- PGVector enabled and schema migrations applied automatically,
- A separate profile for Docker (application-docker.properties) so you don't override local dev properties,
- A Makefile to simplify compose commands.

