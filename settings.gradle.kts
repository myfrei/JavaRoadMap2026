rootProject.name = "java-roadmap-2026"

plugins {
    // Авто-провижининг JDK для тулчейнов: если Java 25 не установлена локально,
    // Gradle скачает её сам (через Foojay Disco API) при первой сборке.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// ─── Код-скелеты модулей курса (1:1 с course/NN-*.md) ───────────────────────
include(
    "modules:m00-fundamentals",
    "modules:m01-syntax",
    "modules:m02-deep-java",
    "modules:m03-concurrency",
    "modules:m04-core-apis",
    "modules:m05-databases",
    "modules:m06-web-api",
    "modules:m07-testing",
    "modules:m08-microservices",
    "modules:m09-devops",
    "modules:m10-architecture",
    "modules:m11-performance",
    "modules:m12-interview",
    "modules:m13-jvm-internals",
    "modules:m14-distributed-systems",
    "modules:m15-observability",
    "modules:m16-security",
    "modules:m17-cloud-native",
    "modules:m18-ai-data",
    "modules:m19-parsing",
)
