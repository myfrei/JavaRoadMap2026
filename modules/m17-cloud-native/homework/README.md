# Домашка модуля 17 — Cloud Native

> Теория и уроки: [`course/17-cloud-native.md`](../../../course/17-cloud-native.md)

Реализуй задания в [`Mod17Homework.java`](src/main/java/com/javaroadmap/m17/homework/Mod17Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod17Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod17HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m17-cloud-native:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `backoffMs(int,long,long)` — экспоненциальный backoff с потолком.
2. `aggregateHealth(List)` — агрегировать здоровье.
3. `parseMemory(String)` — память K8s (Ki/Mi/Gi) в байты.
4. `cpuMillisToCores(long)` — milli-CPU в ядра.
5. `clampReplicas(int,int,int)` — ограничить число реплик.

## Готово, когда

- [ ] `./gradlew :modules:m17-cloud-native:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/17-cloud-native.md) · [← Домашка 16](../../m16-security/homework/README.md) · [Домашка 18 →](../../m18-ai-data/homework/README.md)
