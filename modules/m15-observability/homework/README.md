# Домашка модуля 15 — Наблюдаемость

> Теория и уроки: [`course/15-observability.md`](../../../course/15-observability.md)

Реализуй задания в [`Mod15Homework.java`](src/main/java/com/javaroadmap/m15/homework/Mod15Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod15Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod15HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m15-observability:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `percentile(List,double)` — перцентиль nearest-rank.
2. `ratePerSecond(long,long)` — события в секунду.
3. `parseLogLevel(String)` — уровень лога из строки.
4. `SlidingWindowCounter` — счётчик в скользящем окне.
5. `formatLabels(Map)` — метки в стиле Prometheus.

## Готово, когда

- [ ] `./gradlew :modules:m15-observability:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/15-observability.md) · [← Домашка 14](../../m14-distributed-systems/homework/README.md) · [Домашка 16 →](../../m16-security/homework/README.md)
