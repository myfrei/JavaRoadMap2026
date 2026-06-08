# Домашка модуля 18 — AI и данные

> Теория и уроки: [`course/18-ai-data.md`](../../../course/18-ai-data.md)

Реализуй задания в [`Mod18Homework.java`](src/main/java/com/javaroadmap/m18/homework/Mod18Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod18Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod18HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m18-ai-data:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `dotProduct(double[],double[])` — скалярное произведение.
2. `cosineSimilarity(double[],double[])` — косинусная близость.
3. `termFrequencies(String)` — частоты термов.
4. `topKByScore(Map,int)` — топ-k по score.
5. `normalize(double[])` — L2-нормализация.

## Готово, когда

- [ ] `./gradlew :modules:m18-ai-data:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/18-ai-data.md) · [← Домашка 17](../../m17-cloud-native/homework/README.md) · [Домашка 19 →](../../m19-parsing/homework/README.md)
