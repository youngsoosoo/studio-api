FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradle ./gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-jammy

RUN groupadd --system studio \
    && useradd --system --gid studio --home-dir /app studio \
    && mkdir -p /app/uploads \
    && chown -R studio:studio /app

WORKDIR /app

COPY --from=builder --chown=studio:studio \
    /workspace/build/libs/studio-api-0.0.1-SNAPSHOT.jar /app/app.jar

USER studio

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
