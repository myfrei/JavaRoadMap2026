# Домашка модуля 20 — Docker

> Теория и проект: [`course/20-docker.md`](../../../course/20-docker.md) ·
> примеры Dockerfile / compose — в [`../docker/`](../docker/)

Реализуй задания в [`Mod20Homework.java`](src/main/java/com/javaroadmap/m20/homework/Mod20Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod20Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку, сверяясь с `Mod20HomeworkTest.java`.
3. Запускай проверку:
   ```bash
   ./gradlew :modules:m20-docker:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным.

## Задания (по порядку)

1. `parseImageRef(String)` — разобрать ссылку на образ `[registry/]repo[:tag]` (tag по умолчанию `latest`).
2. `dockerRunCommand(image, name, host, container)` — собрать команду `docker run -d --name … -p …:… …`.
3. `instanceNames(base, count)` — имена N инстансов: `app-1 … app-N` (несколько копий приложения).
4. `portRange(basePort, count)` — порты N инстансов: `basePort, basePort+1, …`.
5. `lintDockerfile(lines)` — предупреждения: незакреплённый базовый образ, отсутствие `USER`, отсутствие `HEALTHCHECK`.
6. `mergeComposeEnv(base, override)` — слить env для compose (значения `override` перекрывают `base`).

> 💡 Связка с практикой: задания 1–6 — это «кирпичики» того, что ты делаешь руками в
> [`../docker/`](../docker/) (Dockerfile, compose, балансировка нескольких инстансов).

## Готово, когда

- [ ] `./gradlew :modules:m20-docker:homeworkTest` зелёный (все 6 заданий).
- [ ] Собрал multi-stage образ и поднял несколько инстансов за балансировщиком (см. `../docker/README.md`).

---

[📚 К модулю](../../../course/20-docker.md) · [← Домашка 19](../../m19-parsing/homework/README.md) · [Домашка 21 →](../../m21-kafka/homework/README.md)
