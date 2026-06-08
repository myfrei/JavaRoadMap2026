# Домашка модуля 7 — Тестирование

> Теория и уроки: [`course/07-testing.md`](../../../course/07-testing.md)

Реализуй задания в [`Mod07Homework.java`](src/main/java/com/javaroadmap/m07/homework/Mod07Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod07Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod07HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m07-testing:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `add(String)` — String Calculator.
2. `Stack` — стек: push / pop / peek / isEmpty / size.
3. `isBalanced(String)` — сбалансированы ли скобки.
4. `romanToInt(String)` — римское число в int.
5. `fib(int)` — число Фибоначчи.

## Готово, когда

- [ ] `./gradlew :modules:m07-testing:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/07-testing.md) · [← Домашка 6](../../m06-web-api/homework/README.md) · [Домашка 8 →](../../m08-microservices/homework/README.md)
