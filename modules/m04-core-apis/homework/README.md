# Домашка модуля 4 — Core APIs

> Теория и уроки: [`course/04-core-apis.md`](../../../course/04-core-apis.md)

Реализуй задания в [`Mod04Homework.java`](src/main/java/com/javaroadmap/m04/homework/Mod04Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod04Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod04HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m04-core-apis:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `daysBetween(LocalDate,LocalDate)` — число дней между датами.
2. `isWeekend(LocalDate)` — выходной ли день.
3. `extractEmails(String)` — извлечь e-mail регуляркой.
4. `slugify(String)` — slug из строки.
5. `secondsToClock(long)` — секунды в H:MM:SS.

## Готово, когда

- [ ] `./gradlew :modules:m04-core-apis:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/04-core-apis.md) · [← Домашка 3](../../m03-concurrency/homework/README.md) · [Домашка 5 →](../../m05-databases/homework/README.md)
