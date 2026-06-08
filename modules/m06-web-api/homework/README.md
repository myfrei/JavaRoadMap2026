# Домашка модуля 6 — Web и API

> Теория и уроки: [`course/06-web-api.md`](../../../course/06-web-api.md)

Реализуй задания в [`Mod06Homework.java`](src/main/java/com/javaroadmap/m06/homework/Mod06Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod06Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod06HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m06-web-api:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `parseQuery(String)` — разобрать query-строку.
2. `matchRoute(String,String)` — матчинг /users/{id}.
3. `statusText(int)` — текст HTTP-статуса.
4. `validate(String,int)` — список ошибок валидации.
5. `buildUrl(String,Map)` — собрать URL с query.

## Готово, когда

- [ ] `./gradlew :modules:m06-web-api:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/06-web-api.md) · [← Домашка 5](../../m05-databases/homework/README.md) · [Домашка 7 →](../../m07-testing/homework/README.md)
