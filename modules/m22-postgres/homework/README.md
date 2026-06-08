# Домашка модуля 22 — PostgreSQL

> Теория и проект: [`course/22-postgres.md`](../../../course/22-postgres.md)

Реализуй задания в [`Mod22Homework.java`](src/main/java/com/javaroadmap/m22/homework/Mod22Homework.java),
сделав «красные» тесты зелёными. Тема — **индексы и SQL-запросы, собираемые из Java-кода**.
**Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod22Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку, сверяясь с `Mod22HomeworkTest.java`.
3. Запускай проверку:
   ```bash
   ./gradlew :modules:m22-postgres:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным.

## Задания (по порядку)

1. `buildSelect(table, cols, whereCols)` — собрать `SELECT … FROM … WHERE c = ? AND …` из Java (плейсхолдеры `?`).
2. `keysetPage(table, key, limit)` — keyset-пагинация (`WHERE key > ? ORDER BY key LIMIT n`) — быстрее, чем `OFFSET`.
3. `createIndex(table, cols)` — DDL составного индекса `CREATE INDEX idx_…_… ON … (…)`.
4. `isSargable(predicate)` — может ли предикат использовать индекс (функция на колонке или `LIKE '%…'` — нет).
5. `indexColumnOrder(predicates)` — порядок колонок составного индекса: сначала равенства (`EQ`), потом диапазоны (`RANGE`).

> 💡 Это «правила», которые экономят запросам миллисекунды: подбор индекса, sargable-предикаты,
> порядок колонок и keyset-пагинация. В проекте модуля проверишь их на реальной БД через `EXPLAIN ANALYZE`.

## Готово, когда

- [ ] `./gradlew :modules:m22-postgres:homeworkTest` зелёный (все 5 заданий).
- [ ] Можешь объяснить, почему `lower(name) = ?` и `LIKE '%x'` не используют обычный B-tree индекс.

---

[📚 К модулю](../../../course/22-postgres.md) · [← Домашка 21](../../m21-kafka/homework/README.md)
