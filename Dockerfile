# ── Stage 1: Build ──────────────────────────────
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy ONLY pom.xml first
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download ALL dependencies — this layer is cached
# and only re-runs if pom.xml changes
RUN ./mvnw -B dependency:go-offline -DskipTests

# NOW copy source code (changes here won't re-download deps)
COPY src src

# Build — dependencies already cached above
RUN ./mvnw -B -DskipTests package

# ── Stage 2: Run ────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8090
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]