# Домашка модуля 9 — DevOps

> Теория и уроки: [`course/09-devops.md`](../../../course/09-devops.md)

Реализуй задания в [`Mod09Homework.java`](src/main/java/com/javaroadmap/m09/homework/Mod09Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod09Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod09HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m09-devops:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `parseSemver(String)` — разобрать версию x.y.z.
2. `compareSemver(String,String)` — сравнить версии.
3. `parseEnv(String)` — разобрать .env.
4. `dockerTag(String,String)` — тег образа из ветки и sha.
5. `isValidImageName(String)` — валидность имени образа.

## Готово, когда

- [ ] `./gradlew :modules:m09-devops:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/09-devops.md) · [← Домашка 8](../../m08-microservices/homework/README.md) · [Домашка 10 →](../../m10-architecture/homework/README.md)
