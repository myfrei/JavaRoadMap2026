# 🍃 Java Spring — углублённый трек

> Семь модулей лонгридов по Spring и многопоточности: от инверсии управления и бинов до
> реактивного стека, Kafka и Java Memory Model. Трек предполагает, что Java-синтаксис ты уже знаешь
> (если нет — сначала [основная программа](../README.md)).

> 📖 **Формат.** Каждая статья — лонгрид по [стилевому гайду](../LONGREAD_STYLE_GUIDE.md):
> от теории «зачем это нужно» к практике с разбором кода, сравнительными таблицами и схемами.
> Практика важнее теории — примеры рабочие, ресурсы только бесплатные.

**Легенда уровней:** 🟢 Junior · 🟡 Middle · 🔴 Senior/Staff.

---

## 🗺️ Программа трека

| #  | Модуль                                              | О чём                                                            | Уровень | Код и домашка |
|----|----------------------------------------------------|-----------------------------------------------------------------|---------|---------------|
| 1  | [IoC, DI и бины](01-ioc-di-beans/README.md)        | Инверсия управления, внедрение зависимостей, жизненный цикл бинов, контроллеры, сервисы, конфигурация | 🟢🟡 | [код](01-ioc-di-beans/src/main/java/com/javaroadmap/spring/s01/) · [домашка](01-ioc-di-beans/homework/README.md) |
| 2  | [Spring Data JPA](02-spring-data-jpa/README.md)    | Репозитории, сущности, связи, миграции Liquibase, Hibernate, JDBC Template | 🟡 | [код](02-spring-data-jpa/src/main/java/com/javaroadmap/spring/s02/) · [домашка](02-spring-data-jpa/homework/README.md) |
| 3  | [MVC и контроллеры](03-mvc-controllers/README.md)  | Паттерн MVC, виды контроллеров, REST, gRPC, WebSocket           | 🟡 | [код](03-mvc-controllers/src/main/java/com/javaroadmap/spring/s03/) · [домашка](03-mvc-controllers/homework/README.md) |
| 4  | [Конфигурация и безопасность](04-config-security/README.md) | Конфигурирование Spring Boot, Spring Security, CORS, OAuth2 | 🟡🔴 | [код](04-config-security/src/main/java/com/javaroadmap/spring/s04/) · [домашка](04-config-security/homework/README.md) |
| 5  | [Тестирование и Kafka](05-testing-kafka/README.md) | Тесты в Spring Boot, очереди и Event-Driven, KafkaTemplate/Listener | 🟡🔴 | [код](05-testing-kafka/src/main/java/com/javaroadmap/spring/s05/) · [домашка](05-testing-kafka/homework/README.md) |
| 6  | [Реактивный стек](06-reactive-webflux/README.md)   | Реактивный подход, WebFlux, R2DBC, WebClient, тесты реактивных компонентов | 🔴 | [код](06-reactive-webflux/src/main/java/com/javaroadmap/spring/s06/) · [домашка](06-reactive-webflux/homework/README.md) |
| 7  | [Многопоточность](07-concurrency/README.md)        | Потоки, синхронизация, ExecutorService/ForkJoin, CompletableFuture, JMM | 🔴 | [код](07-concurrency/src/main/java/com/javaroadmap/spring/s07/) · [домашка](07-concurrency/homework/README.md) |

---

## 📚 Как проходить

1. Читай модуль по порядку — статьи внутри идут от простого к сложному.
2. Запускай примеры модуля и решай домашку — каждый модуль трека это Gradle-подпроект
   с кодом примеров, тестами и «красными» заданиями:
   ```bash
   ./gradlew :java-spring:01-ioc-di-beans:test          # тесты примеров модуля
   ./gradlew :java-spring:01-ioc-di-beans:homeworkTest  # домашка (красная, пока не решишь)
   ```
3. После модуля возвращайся к разделу «Заключение» каждой статьи — там чек-лист «как применять на практике».

> 💡 Хочешь отдельную песочницу — [start.spring.io](https://start.spring.io) → зависимости `Spring Web`,
> `Spring Data JPA`, `PostgreSQL Driver`. Запуск: `./gradlew bootRun`.

---

[📚 К основному курсу](../README.md)
