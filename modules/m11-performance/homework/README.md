# Домашка модуля 11 — Производительность

> Теория и уроки: [`course/11-performance.md`](../../../course/11-performance.md)

Реализуй задания в [`Mod11Homework.java`](src/main/java/com/javaroadmap/m11/homework/Mod11Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod11Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod11HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m11-performance:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `sieve(int)` — решето Эратосфена.
2. `LruCache` — LRU-кэш с вытеснением.
3. `twoSumIndices(int[],int)` — two-sum за O(n).
4. `fibFast(int)` — Фибоначчи итеративно.
5. `dedupPreserveOrder(List)` — дедуп с сохранением порядка.

## Готово, когда

- [ ] `./gradlew :modules:m11-performance:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/11-performance.md) · [← Домашка 10](../../m10-architecture/homework/README.md) · [Домашка 12 →](../../m12-interview/homework/README.md)
