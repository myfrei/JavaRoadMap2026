# Домашка модуля 19 — Парсинг и компиляторы

> Теория и уроки: [`course/19-parsing.md`](../../../course/19-parsing.md)

Реализуй задания в [`Mod19Homework.java`](src/main/java/com/javaroadmap/m19/homework/Mod19Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod19Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod19HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m19-parsing:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `tokenize(String)` — токенизировать выражение.
2. `matchParens(String)` — баланс скобок.
3. `toReversePolish(List)` — инфикс в ОПЗ (shunting-yard).
4. `evaluate(String)` — вычислить выражение с приоритетами.
5. `countTokens(String)` — число токенов.

## Готово, когда

- [ ] `./gradlew :modules:m19-parsing:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/19-parsing.md) · [← Домашка 18](../../m18-ai-data/homework/README.md)
