# Домашка модуля 3 — Конкурентность

> Теория и уроки: [`course/03-concurrency.md`](../../../course/03-concurrency.md)

Реализуй задания в [`Mod03Homework.java`](src/main/java/com/javaroadmap/m03/homework/Mod03Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod03Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod03HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m03-concurrency:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `sumConcurrently(int[],int)` — параллельная сумма массива.
2. `atomicCount(int,int)` — счётчик без потери обновлений.
3. `parallelSquares(List)` — квадраты асинхронно, порядок сохранён.
4. `countPrimes(List)` — число простых (параллельный стрим).
5. `produceConsumeSum(int)` — producer/consumer через очередь.

## Готово, когда

- [ ] `./gradlew :modules:m03-concurrency:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/03-concurrency.md) · [← Домашка 2](../../m02-deep-java/homework/README.md) · [Домашка 4 →](../../m04-core-apis/homework/README.md)
