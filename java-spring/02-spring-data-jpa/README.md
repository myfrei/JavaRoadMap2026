# Модуль 2. Spring Data JPA и базы данных — оглавление

> Формат модуля — серия лонгридов (лекций). Читай по порядку: каждая статья опирается на предыдущую и связана с ней по смыслу. Это финальный модуль трека по работе с данными — от репозиториев «из коробки» до выбора между Hibernate и голым SQL.

## Содержание

1. [Spring Data JPA: репозитории из коробки](01-spring-data-jpa-intro.md)
2. [Сущности и репозитории](02-entities-and-repositories.md)
3. [Связи между сущностями](03-entity-relationships.md)
4. [Версионирование базы данных с Liquibase](04-liquibase-migrations.md)
5. [Hibernate изнутри: сущности и их особенности](05-hibernate-internals.md)
6. [JDBC Template vs Hibernate: когда нужно быстро](06-jdbc-template-vs-hibernate.md)

## 💻 Код модуля

Рядом со статьями живёт запускаемый код (Gradle-подпроект `:java-spring:02-spring-data-jpa`,
домен — библиотека: автор → книги, книги ↔ жанры):

- [`src/main/java/...`](src/main/java/com/javaroadmap/spring/s02/) — сущности со связями
  `@OneToMany`/`@ManyToMany`, репозитории (derived queries, `join fetch` против N+1),
  Liquibase-миграции на H2, отчёт через `JdbcTemplate`.
- [`src/test/java/...`](src/test/java/com/javaroadmap/spring/s02/) — `@DataJpaTest` (каскады,
  orphanRemoval, `LazyInitializationException`), тест миграций, JPA + чистый SQL в одной транзакции.
- [`homework/`](homework/README.md) — домашка: 6 заданий с «красными» тестами
  (`./gradlew :java-spring:02-spring-data-jpa:homeworkTest`).

```bash
./gradlew :java-spring:02-spring-data-jpa:test          # тесты примеров
./gradlew :java-spring:02-spring-data-jpa:homeworkTest  # «красные» задания
```

---

[📚 К треку Java Spring](../README.md)
