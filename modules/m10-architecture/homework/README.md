# Домашка модуля 10 — Архитектура

> Теория и уроки: [`course/10-architecture.md`](../../../course/10-architecture.md)

Реализуй задания в [`Mod10Homework.java`](src/main/java/com/javaroadmap/m10/homework/Mod10Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod10Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod10HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m10-architecture:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `EventBus` — шина событий (observer).
2. `priceAfter(double,DiscountType)` — стратегия скидки.
3. `pipeline(int,List)` — конвейер стадий.
4. `firstMatch(List,Predicate)` — первый по предикату.
5. `countBy(List,Function)` — группировка с подсчётом.

## Готово, когда

- [ ] `./gradlew :modules:m10-architecture:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/10-architecture.md) · [← Домашка 9](../../m09-devops/homework/README.md) · [Домашка 11 →](../../m11-performance/homework/README.md)
