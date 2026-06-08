# Домашка модуля 16 — Безопасность

> Теория и уроки: [`course/16-security.md`](../../../course/16-security.md)

Реализуй задания в [`Mod16Homework.java`](src/main/java/com/javaroadmap/m16/homework/Mod16Homework.java),
сделав «красные» тесты зелёными. **Файл с тестами трогать не нужно.**

## Как работать

1. Открой `Mod16Homework.java` — методы кидают `UnsupportedOperationException`.
2. Реализуй их по порядку (задания идут по плану модуля), сверяясь с `Mod16HomeworkTest.java`.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :modules:m16-security:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

1. `constantTimeEquals(byte[],byte[])` — сравнение за постоянное время.
2. `hmacSha256Hex(String,String)` — HMAC-SHA256 в hex.
3. `isStrongPassword(String)` — надёжность пароля.
4. `maskEmail(String)` — маска e-mail.
5. `sanitizeFilename(String)` — безопасное имя файла.

## Готово, когда

- [ ] `./gradlew :modules:m16-security:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../../../course/16-security.md) · [← Домашка 15](../../m15-observability/homework/README.md) · [Домашка 17 →](../../m17-cloud-native/homework/README.md)
