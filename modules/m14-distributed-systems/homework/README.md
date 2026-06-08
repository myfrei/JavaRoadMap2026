# Домашка модуля 14 — Распределённые системы

> Теория и уроки: [`course/14-distributed-systems.md`](../../../course/14-distributed-systems.md)

Реализуй задания в [`Mod14Homework.java`](src/main/java/com/javaroadmap/m14/homework/Mod14Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod14Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod14HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m14-distributed-systems:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `incrementClock(Map,String)` — инкремент векторных часов.
2. `mergeClocks(Map,Map)` — слияние (max по узлам).
3. `happensBefore(Map,Map)` — строгое предшествование.
4. `hasQuorum(int,int)` — наличие кворума.
5. `pickShard(String,int)` — выбор шарда по ключу.

## Готово, когда

- [ ] `./gradlew :modules:m14-distributed-systems:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/14-distributed-systems.md) · [← Домашка 13](../../m13-jvm-internals/homework/README.md) · [Домашка 15 →](../../m15-observability/homework/README.md)
