# Домашка модуля 5 — Базы данных

> Теория и уроки: [`course/05-databases.md`](../../../course/05-databases.md)

Реализуй задания в [`Mod05Homework.java`](src/main/java/com/javaroadmap/m05/homework/Mod05Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod05Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod05HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m05-databases:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `InMemoryRepository` — CRUD: save / findById / findAll / deleteById / count.
2. `pageOf(List,int,int)` — страница списка (page с нуля).
3. `buildSelect(String,List)` — SELECT-запрос.
4. `topN(List,int)` — n наибольших по убыванию.
5. `groupCount(List)` — GROUP BY COUNT.

## Готово, когда

- [ ] `./gradlew :modules:m05-databases:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/05-databases.md) · [← Домашка 4](../../m04-core-apis/homework/README.md) · [Домашка 6 →](../../m06-web-api/homework/README.md)
