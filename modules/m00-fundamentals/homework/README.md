# Домашка модуля 0 — Фундамент

> Теория и уроки: [`course/00-fundamentals.md`](../../../course/00-fundamentals.md)

Реализуй задания в [`Mod00Homework.java`](src/main/java/com/javaroadmap/m00/homework/Mod00Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod00Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod00HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m00-fundamentals:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `reverse(String)` — перевернуть строку посимвольно.
2. `countNonEmptyLines(String)` — число непустых строк.
3. `wordCount(String)` — число слов (разделитель — пробелы).
4. `wordFrequencies(String)` — частоты слов без учёта регистра.
5. `parseArgs(String[])` — разобрать --key=value и --key value.
6. `isPalindrome(String)` — палиндром без учёта регистра и не-букв/цифр.

## Готово, когда

- [ ] `./gradlew :modules:m00-fundamentals:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/00-fundamentals.md) · [Домашка 1 →](../../m01-syntax/homework/README.md)
