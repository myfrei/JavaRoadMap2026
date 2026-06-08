# Домашка модуля 13 — Внутренности JVM

> Теория и уроки: [`course/13-jvm-internals.md`](../../../course/13-jvm-internals.md)

Реализуй задания в [`Mod13Homework.java`](src/main/java/com/javaroadmap/m13/homework/Mod13Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod13Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod13HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m13-jvm-internals:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `accessModifiers(int)` — декодировать access-flags.
2. `align8(int)` — выравнивание вверх до 8.
3. `popcount(long)` — число установленных битов.
4. `isPowerOfTwo(long)` — степень ли двойки.
5. `utf8Length(int)` — длина UTF-8 по ведущему байту.

## Готово, когда

- [ ] `./gradlew :modules:m13-jvm-internals:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/13-jvm-internals.md) · [← Домашка 12](../../m12-interview/homework/README.md) · [Домашка 14 →](../../m14-distributed-systems/homework/README.md)
