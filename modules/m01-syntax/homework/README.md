# Домашка модуля 1 — Синтаксис и ООП

> Теория и уроки: [`course/01-syntax.md`](../../../course/01-syntax.md)

Реализуй задания в [`Mod01Homework.java`](src/main/java/com/javaroadmap/m01/homework/Mod01Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod01Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod01HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m01-syntax:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `fizzbuzz(int)` — FizzBuzz для 1..n.
2. `factorial(int)` — факториал.
3. ` n<0 -> IllegalArgumentException` —  n<0 -> IllegalArgumentException.
4. `gradeOf(int)` — буква оценки по баллу.
5. `BankAccount` — счёт: deposit / withdraw (овердрафт -> исключение) / balance.
6. `Direction.turnRight()` — поворот по часовой стрелке.

## Готово, когда

- [ ] `./gradlew :modules:m01-syntax:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/01-syntax.md) · [← Домашка 0](../../m00-fundamentals/homework/README.md) · [Домашка 2 →](../../m02-deep-java/homework/README.md)
