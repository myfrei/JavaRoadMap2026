# syntax=docker/dockerfile:1
# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage сборка JVM-приложения (шаблон для проекта модуля 20).
# Контекст сборки — КОРЕНЬ репозитория (нужны ./gradlew и все модули):
#
#   docker build -f modules/m20-docker/docker/Dockerfile -t javaroadmap/app:dev .
#
# ─────────────────────────────────────────────────────────────────────────────

# ── Стадия 1: сборка дистрибутива через Gradle (application plugin -> installDist)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
# сначала только то, что нужно Gradle — лучше кэшируется
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
# затем исходники
COPY modules ./modules
RUN ./gradlew :modules:m20-docker:installDist --no-daemon

# ── Стадия 2: лёгкий runtime-образ (только JRE + дистрибутив, без исходников и JDK)
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
# не root — безопасность
RUN useradd --uid 1000 --create-home appuser
COPY --from=build /src/modules/m20-docker/build/install/m20-docker/ ./
USER appuser
EXPOSE 8080
# HEALTHCHECK показателен: для НАСТОЯЩЕГО сервиса (напр. Spring Boot из модуля 06)
# замени на проверку HTTP-эндпоинта, например:
#   HEALTHCHECK --interval=10s --timeout=3s CMD wget -q --spider http://localhost:8080/health || exit 1
ENTRYPOINT ["bin/m20-docker"]
