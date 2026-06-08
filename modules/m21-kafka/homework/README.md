# Домашка модуля 21 — Kafka

> Теория и проект: [`course/21-kafka.md`](../../../course/21-kafka.md)

Реализуй задания в [`Mod21Homework.java`](src/main/java/com/javaroadmap/m21/homework/Mod21Homework.java),
сделав «красные» тесты зелёными. Тема — **идемпотентность и надёжность**. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod21Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку, сверяясь с `Mod21HomeworkTest.java`.
3. Запускай проверку:
   ```bash
   ./gradlew :modules:m21-kafka:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным.

## Задания (по порядку)

1. `partitionFor(key, partitions)` — партиция по ключу: один ключ → одна партиция → сохранение **порядка**.
2. `IdempotentProcessor.process(id)` — **идемпотентная** обработка: дубликаты пропускаются (exactly-once эффект).
3. `nextCommitOffset(processed)` — безопасный оффсет коммита при at-least-once: первый «разрыв» в обработанных.
4. `retryRoute(attempt, maxRetries)` — `RETRY`, пока есть попытки, иначе `DLQ` (надёжность, «ядовитые» сообщения).
5. `latestByKey(records)` — компакция лога: последнее значение по каждому ключу (по наибольшему offset).

## Готово, когда

- [ ] `./gradlew :modules:m21-kafka:homeworkTest` зелёный (все 5 заданий).
- [ ] Можешь объяснить, как идемпотентность + ручной коммит оффсетов дают надёжную доставку.

---

[📚 К модулю](../../../course/21-kafka.md) · [← Домашка 20](../../m20-docker/homework/README.md) · [Домашка 22 →](../../m22-postgres/homework/README.md)
