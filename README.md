# ☕ Java Roadmap 2026 — продвинутый курс на русском

![Java](https://img.shields.io/badge/Java-25%20LTS-orange?logo=openjdk&logoColor=white)
![Build](https://img.shields.io/badge/Gradle-9.5-02303A?logo=gradle&logoColor=white)
![Level](https://img.shields.io/badge/Грейд-Junior%20%E2%86%92%20Staff-blue)
![Duration](https://img.shields.io/badge/Срок-12--18%20месяцев-success)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Lang](https://img.shields.io/badge/Язык-Русский-red)

Бесплатный open-source роадмап по **Java и JVM**: путь от полного нуля до уровня **Senior**
за 12–18 месяцев при темпе 10–15 часов в неделю.

> **Главный принцип курса — практика > теория.** Каждую тему ты закрываешь кодом: пишешь, ломаешь, чинишь,
> рефакторишь. Любой модуль завершается мини-проектом, который можно положить в портфолио.

Это не просто список тем, а **репозиторий с запускаемым кодом**: на каждый модуль курса есть Gradle-подпроект
со скелетом, который ты наполняешь своим решением (`./gradlew :modules:mNN-slug:run`).

---

## 📑 Содержание

- [О курсе](#-о-курсе)
- [Программа курса](#-программа-курса)
- [Трек для новичков](#-трек-для-новичков)
- [Грейды и сроки](#-грейды-и-сроки)
- [Как проходить курс](#-как-проходить-курс)
- [Рекомендованный ритм недели](#-рекомендованный-ритм-недели)
- [Структура репозитория](#-структура-репозитория)
- [Запуск кода](#-запуск-кода)
- [Домашние задания](#-домашние-задания)
- [Топ бесплатных ресурсов](#-топ-бесплатных-ресурсов)
- [Красные флаги](#-красные-флаги)
- [3 железных правила](#-3-железных-правила)
- [FAQ](#-faq)

---

## 📌 О курсе

- **Бесплатно и навсегда.** Только открытые ресурсы: официальная документация, бесплатные книги, доклады, блоги.
- **20 модулей** (00–19) — от терминала и синтаксиса до JVM-внутренностей, распределённых систем и AI.
- **Документация + код.** Теория в `course/`, запускаемый скелет в `modules/` (Java 25, Gradle, JUnit 5).
- **Проектный подход.** Каждый модуль = мини-проект на GitHub. К концу курса — портфолио из 15+ проектов.
- **Современный стек 2026.** Java 25 LTS, virtual threads, Spring Boot 3, GraalVM, OpenTelemetry, Spring AI.

Программа собрана так, чтобы провести тебя от **полного нуля до Senior/Staff** последовательно: каждый модуль
опирается на предыдущие.

---

## 🗺 Программа курса

| Модуль | Ур. | Темы | Код |
|--------|-----|------|-----|
| [00. Фундамент](course/00-fundamentals.md) | 🟢 | JDK/JVM, CLI, git, HTTP, сеть, первая программа, JShell, build-tool, IDE | [m00](modules/m00-fundamentals) |
| [01. Синтаксис и ООП](course/01-syntax.md) | 🟢 | Типы, классы/интерфейсы/наследование, enum, records, дженерики (intro), исключения | [m01](modules/m01-syntax) |
| [02. Глубокая Java](course/02-deep-java.md) | 🟢 | Дженерики, Collections, Stream API, лямбды, Optional, sealed, pattern matching, рефлексия | [m02](modules/m02-deep-java) |
| [03. Конкурентность](course/03-concurrency.md) | 🟡 | Threads, `java.util.concurrent`, ExecutorService, CompletableFuture, virtual threads, structured concurrency | [m03](modules/m03-concurrency) |
| [04. Core APIs](course/04-core-apis.md) | 🟢 | `java.lang`/`util`, IO/NIO.2, `java.time`, `java.net.http`, regex, text blocks, сериализация | [m04](modules/m04-core-apis) |
| [05. Базы данных](course/05-databases.md) | 🟡 | JDBC, HikariCP, JPA/Hibernate, Spring Data, Flyway/Liquibase, транзакции, Redis | [m05](modules/m05-databases) |
| [06. Web и API](course/06-web-api.md) | 🟡 | Spring Boot, Spring MVC, REST, валидация, WebFlux, gRPC, WebSocket, JWT, OpenAPI | [m06](modules/m06-web-api) |
| [07. Тестирование](course/07-testing.md) | 🟢 | JUnit 5, Mockito, AssertJ, Testcontainers, MockMvc, parameterized, coverage, mutation | [m07](modules/m07-testing) |
| [08. Микросервисы](course/08-microservices.md) | 🟡 | Spring Cloud, discovery, config, API gateway, Resilience4j, Kafka, RabbitMQ, saga | [m08](modules/m08-microservices) |
| [09. DevOps](course/09-devops.md) | 🟡 | Maven и Gradle глубоко, Docker, multi-stage, GitHub Actions, Kubernetes, Helm | [m09](modules/m09-devops) |
| [10. Архитектура](course/10-architecture.md) | 🟡 | SOLID, GoF-паттерны, DDD, hexagonal/clean, CQRS, event sourcing, модульный монолит | [m10](modules/m10-architecture) |
| [11. Производительность](course/11-performance.md) | 🔴 | JMH, async-profiler, JFR, GC-тюнинг, JIT, escape analysis, память, GraalVM | [m11](modules/m11-performance) |
| [12. Собеседование](course/12-interview.md) | 🟡 | Java-вопросы, DSA на Java, system design, поведенческое интервью | [m12](modules/m12-interview) |
| [13. Внутренности JVM](course/13-jvm-internals.md) | 🔴 | Байткод, class loading, JIT (C1/C2), GC (G1/ZGC/Shenandoah), JMM, layout объектов | [m13](modules/m13-jvm-internals) |
| [14. Распределённые системы](course/14-distributed-systems.md) | 🔴 | Консенсус (Raft), распределённые транзакции, saga, CAP, идемпотентность, локи | [m14](modules/m14-distributed-systems) |
| [15. Наблюдаемость](course/15-observability.md) | 🔴 | Micrometer, OpenTelemetry, Prometheus, Grafana, трейсинг, SLF4J/Logback | [m15](modules/m15-observability) |
| [16. Безопасность](course/16-security.md) | 🔴 | Spring Security, OAuth2/OIDC, JWT, OWASP Top 10, secure coding, JCA, secrets | [m16](modules/m16-security) |
| [17. Cloud Native](course/17-cloud-native.md) | 🔴 | Kubernetes глубоко, GraalVM native image, Spring AOT/Native, serverless, service mesh | [m17](modules/m17-cloud-native) |
| [18. AI и данные](course/18-ai-data.md) | 🔴 | Spring AI, LangChain4j, LLM/RAG, embeddings, vector DB, Spark, Kafka Streams | [m18](modules/m18-ai-data) |
| [19. Парсинг и компиляторы](course/19-parsing.md) | 🔴 | Парсеры, ANTLR4, грамматики, DSL, манипуляция байткодом (ASM, ByteBuddy), annotation processors | [m19](modules/m19-parsing) |

Дополнительно: [03-concurrency-examples](course/03-concurrency-examples.md) и
[06-web-examples](course/06-web-examples.md) — companion-файлы с примерами кода от простого к продвинутому.

---

## 🐣 Трек для новичков

Если Java для тебя — первый серьёзный язык, начни с быстрого практического трека:
**[java-for-beginners](java-for-beginners/README.md)** — 12 коротких уроков с кодом (от первой программы
до JUnit), завершается CLI-утилитой. После него возвращайся в Модуль 00 и иди по основной программе.

---

## ⏱ Грейды и сроки

| Грейд | Срок | Модули | Что умеешь |
|-------|------|--------|------------|
| 🟢 **Junior** | 3–4 мес | 00–04, 07 | Идиоматичный Java-код, ООП, коллекции/стримы, базовая конкурентность, тесты |
| 🟡 **Middle** | 7–9 мес | 05, 06, 08, 09, 10 | БД, Spring Boot REST/gRPC, микросервисы, Docker/CI, чистая архитектура |
| 🔴 **Senior** | 12–14 мес | 11, 13, 14, 15, 16 | Перформанс, JVM-внутренности, распределённые системы, observability, безопасность |
| 🟣 **Staff** | 16–18 мес | 17, 18, 19 | Cloud-native, native image, AI-интеграции, компиляторы/DSL |

> Модуль **12 (Собеседование)** — сквозной: проходи его перед сменой работы на любом грейде.

---

## 🧭 Как проходить курс

1. **Установи JDK 25 и Gradle-wrapper уже в репозитории** — `./gradlew build` проверит окружение.
2. **Иди по модулям последовательно** — они зависят друг от друга.
3. **Практика важнее видео.** Посмотрел тему → сразу пишешь код в `modules/mNN-slug/`.
4. **Каждый проект — на GitHub** с README, лицензией и осмысленными коммитами.
5. **Веди заметки** по каждому модулю: что понял, где затык, что перечитать.
6. **Не застревай в туториал-аду** — после часа борьбы подсматривай, но решай сам.

---

## 📆 Рекомендованный ритм недели

| День | Что делаем | Время |
|------|------------|-------|
| Пн | Теория: читаешь доку/книгу, смотришь часть доклада | 1.5–2 ч |
| Вт | Практика: набираешь примеры руками, экспериментируешь | 2 ч |
| Ср | Теория + видео: глубже в тему | 1.5 ч |
| Чт | Своя реализация без подглядывания | 2 ч |
| Пт | Тесты + рефакторинг, чистка кода | 1.5 ч |
| Сб | Работа над проектом модуля | 3–4 ч |
| Вс | Чтение исходников JDK/библиотек, ревью своего кода, отдых | 1 ч |

---

## 🗂 Структура репозитория

```
JavaRoadMap2026/
├── README.md                 ← ты здесь
├── CLAUDE.md                 гайд по проекту и конвенциям
├── course/                   ДОКУМЕНТАЦИЯ: 20 модулей + companion-примеры
│   ├── 00-fundamentals.md
│   └── … 19-parsing.md
├── java-for-beginners/       быстрый трек для новичков
├── modules/                  КОД: Gradle-подпроекты 1:1 к модулям
│   ├── m00-fundamentals/
│   │   ├── src/main/java/com/javaroadmap/m00/Main.java
│   │   └── src/test/java/com/javaroadmap/m00/SmokeTest.java
│   └── … m19-parsing/
├── settings.gradle.kts       список модулей + авто-загрузка JDK (Foojay)
├── build.gradle.kts          общий конфиг: Java 25, JUnit 5
└── gradle/libs.versions.toml version catalog
```

Документация и код держатся в паритете: модуль курса `course/NN-slug.md` ↔ код-подпроект `modules/mNN-slug/`.

---

## ⚙️ Запуск кода

Нужен только JDK 17+ для запуска Gradle — **JDK 25 для компиляции скачается автоматически** (Foojay-резолвер).

```bash
./gradlew build                            # собрать и протестировать все модули
./gradlew :modules:m00-fundamentals:run    # запустить Main модуля 00
./gradlew :modules:m00-fundamentals:test   # smoke-тесты модуля 00 (зелёные)
./gradlew :modules:m00-fundamentals:homeworkTest  # домашка модуля 00 (красная, пока не решишь)
./gradlew javaToolchains                   # какие JDK найдены/скачаны
```

На Windows используй `gradlew.bat`. Подробнее о конвенциях — в [CLAUDE.md](CLAUDE.md).

---

## 📝 Домашние задания

У каждого модуля — папка `modules/mNN-slug/homework/` с **«красными» заданиями**: заготовки методов кидают
`UnsupportedOperationException`, а тесты падают, пока ты не реализуешь их сам. Это TDD-практика **red → green**:
запусти, увидь красное, сделай зелёным.

```bash
./gradlew :modules:m07-testing:homeworkTest   # красные тесты модуля → реализуй → зелёные
```

Задания идут по плану модуля (5–6 на модуль), пошаговое описание — в `homework/README.md` каждого модуля
(например, [домашка модуля 00](modules/m00-fundamentals/homework/README.md)). Домашка вынесена в отдельную
задачу `homeworkTest` и **не ломает** общий `./gradlew build` — он остаётся зелёным.

---

## 🌟 Топ бесплатных ресурсов

**Документация и спецификации**
- [Официальная документация Java](https://docs.oracle.com/en/java/javase/25/) и [Java Tutorials (Dev.java)](https://dev.java/learn/)
- [JLS — Java Language Specification](https://docs.oracle.com/javase/specs/) и [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/)
- [Spring Guides](https://spring.io/guides) и [Spring Boot Reference](https://docs.spring.io/spring-boot/index.html)

**Бесплатные книги**
- «Modern Java in Action» (главы доступны online), [Java by Comparison](https://java.by-comparison.com/) (примеры)
- [Effective Java — конспекты и доклады Joshua Bloch](https://www.youtube.com/results?search_query=effective+java+joshua+bloch) (бесплатно)
- [Optimizing Java (O'Reilly, фрагменты)](https://www.oreilly.com/) и блог [Inside Java](https://inside.java/)

**YouTube / доклады**
- [Java (официальный канал)](https://www.youtube.com/@java) и [Devoxx](https://www.youtube.com/@DevoxxForever)
- [Nicolai Parlog (nipafx)](https://www.youtube.com/@nipafx), [JEP Café](https://www.youtube.com/playlist?list=PLX8CzqL3ArzX8ZzPNjBgji7rznFFiqHpw)

**Практика**
- [Exercism — Java track](https://exercism.org/tracks/java), [LeetCode](https://leetcode.com/), [Codewars](https://www.codewars.com/)
- [Advent of Code](https://adventofcode.com/) — отличная разминка на алгоритмы

---

## ⚠️ Красные флаги

Типичные ошибки, которые тормозят рост:

- Пишешь «C#/Python на Java», игнорируя идиомы (стримы, `Optional`, records, sealed).
- Учишь Spring, не понимая чистую Java и JVM под ним.
- Копируешь код из туториалов, не понимая каждой строки.
- Ни одного проекта на GitHub — только просмотренные видео.
- `null` вместо `Optional`, ловля `Exception` «чтобы не падало», пустые `catch`.
- Не пишешь тесты («потом») и не запускаешь профайлер перед оптимизацией.
- Боишься читать исходники JDK и стектрейсы.
- Пропускаешь конкурентность и JVM-внутренности как «слишком сложное».

---

## 🔥 3 железных правила

1. **У каждого модуля — проект.** Нет проекта — модуль не закрыт.
2. **Каждый проект — на GitHub.** С README, лицензией и историей коммитов.
3. **Пиши заметки каждую неделю.** Через полгода это твой главный конспект.

---

## ❓ FAQ

**С чего начать?** С [Модуля 00](course/00-fundamentals.md). Совсем новичок — сначала
[трек для новичков](java-for-beginners/README.md).

**Нужен ли опыт в программировании?** Нет. Курс ведёт с нуля, но темп интенсивный.

**Maven или Gradle?** Репозиторий собран на **Gradle (Kotlin DSL)**; Maven подробно разбирается в Модуле 09 —
оба инструмента нужно знать.

**Почему Java 25?** Это актуальный LTS на 2026 год: стабильные virtual threads, structured concurrency,
современный синтаксис. JDK скачивается автоматически.

**Это официальный курс Oracle/Spring?** Нет, это независимый бесплатный community-роадмап.

---

## 📄 Лицензия

MIT — используй, форкай, улучшай. PR приветствуются.
