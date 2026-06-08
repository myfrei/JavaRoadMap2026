# Домашка модуля 12 — Собеседование

> Теория и уроки: [`course/12-interview.md`](../../../course/12-interview.md)

Реализуй задания в [`Mod12Homework.java`](src/main/java/com/javaroadmap/m12/homework/Mod12Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod12Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod12HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m12-interview:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `twoSum(int[],int)` — индексы двух чисел.
2. `isAnagram(String,String)` — являются ли анаграммами.
3. `reverse(List)` — развернуть список.
4. `maxSubArray(int[])` — максимальная сумма подмассива (Кадане).
5. `firstUniqueChar(String)` — первый неповторяющийся символ.

## Готово, когда

- [ ] `./gradlew :modules:m12-interview:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/12-interview.md) · [← Домашка 11](../../m11-performance/homework/README.md) · [Домашка 13 →](../../m13-jvm-internals/homework/README.md)
