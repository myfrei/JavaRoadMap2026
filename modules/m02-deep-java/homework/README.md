# Домашка модуля 2 — Глубокая Java

> Теория и уроки: [`course/02-deep-java.md`](../../../course/02-deep-java.md)

Реализуй задания в [`Mod02Homework.java`](src/main/java/com/javaroadmap/m02/homework/Mod02Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod02Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod02HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m02-deep-java:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `distinctSorted(List)` — уникальные значения по возрастанию.
2. `groupByFirstLetter(List)` — сгруппировать слова по первой букве.
3. `sumEven(List)` — сумма чётных чисел.
4. `firstLongerThan(List,int)` — первое слово длиннее len (Optional).
5. `joinUpper(List)` — соединить в верхнем регистре через запятую.

## Готово, когда

- [ ] `./gradlew :modules:m02-deep-java:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/02-deep-java.md) · [← Домашка 1](../../m01-syntax/homework/README.md) · [Домашка 3 →](../../m03-concurrency/homework/README.md)
