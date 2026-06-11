# Модуль 6. Реактивный стек (WebFlux) — оглавление

> 📖 **Формат.** Каждая статья — лонгрид по [стилевому гайду](../../LONGREAD_STYLE_GUIDE.md):
> от теории «зачем это нужно» к практике с разбором кода, сравнительными таблицами и схемами.
> Реактивность — мощный инструмент, но не серебряная пуля: разбираем и где она выигрывает, и где вредит.

Финальный модуль реактивного блока: от идеи неблокирующего I/O до тестов реактивных компонентов.
Статьи идут по порядку — каждая опирается на предыдущую (1 → 2 → 3 → …).

- [Особенности реактивного подхода](01-reactive-approach.md)
- [Reactor: Mono, Flux и операторы](02-reactor-mono-flux.md)
- [Реактивный подход: Spring WebFlux](03-spring-webflux.md)
- [Реактивные репозитории и клиенты](04-reactive-repositories-clients.md)
- [Backpressure и управление потоком](05-backpressure.md)
- [Тестируем реактивные компоненты](06-testing-reactive.md)

## 💻 Код модуля

Рядом со статьями живёт запускаемый код (Gradle-подпроект `:java-spring:06-reactive-webflux`,
WebFlux на Netty + реактивный H2 через R2DBC):

- [`src/main/java/...`](src/main/java/com/javaroadmap/spring/s06/) — операторы Reactor на потоке цен
  (`PriceOps`: скользящее среднее, фильтр выбросов, fallback, timeout, `onBackpressureLatest`),
  аннотационный контроллер и functional endpoints, `ReactiveCrudRepository`, `WebClient`.
- [`src/test/java/...`](src/test/java/com/javaroadmap/spring/s06/) — `StepVerifier` (включая
  `withVirtualTime` и `TestPublisher`), backpressure через `thenRequest`, `@WebFluxTest` +
  `WebTestClient`, `@DataR2dbcTest`, интеграционный тест `WebClient` на живом Netty.
- [`homework/`](homework/README.md) — домашка: 4 задания с «красными» тестами
  (`./gradlew :java-spring:06-reactive-webflux:homeworkTest`).

```bash
./gradlew :java-spring:06-reactive-webflux:test          # тесты примеров
./gradlew :java-spring:06-reactive-webflux:homeworkTest  # «красные» задания
```

---

[📚 К треку Java Spring](../README.md)
