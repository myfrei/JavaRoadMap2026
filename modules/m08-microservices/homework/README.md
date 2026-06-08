# Домашка модуля 8 — Микросервисы

> Теория и уроки: [`course/08-microservices.md`](../../../course/08-microservices.md)

Реализуй задания в [`Mod08Homework.java`](src/main/java/com/javaroadmap/m08/homework/Mod08Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod08Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod08HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m08-microservices:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `CircuitBreaker` — автомат CLOSED/OPEN.
2. `retryDelaysMs(int,long)` — экспоненциальные задержки ретраев.
3. `IdempotencyStore` — firstSeen(key): true один раз.
4. `roundRobin(List,int)` — выбор узла по индексу.
5. `splitBatches(List,int)` — разбить список на батчи.

## Готово, когда

- [ ] `./gradlew :modules:m08-microservices:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/08-microservices.md) · [← Домашка 7](../../m07-testing/homework/README.md) · [Домашка 9 →](../../m09-devops/homework/README.md)
