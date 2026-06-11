# Модуль 4. Конфигурация и безопасность — оглавление

> Формат модуля — серия лонгридов (лекций). Читай по порядку: каждая статья опирается на предыдущую и связана с ней по смыслу. Это финальный модуль трека по безопасности — от внешней конфигурации Spring Boot до OAuth2 и JWT.

## Содержание

1. [Конфигурирование Spring Boot приложения](01-spring-boot-configuration.md)
2. [Основы безопасности веб-приложений](02-web-security-basics.md)
3. [Структура и настройка Spring Security](03-spring-security-architecture.md)
4. [Аутентификация и авторизация](04-authentication-authorization.md)
5. [CORS и Spring](05-cors.md)
6. [OAuth2 и JWT в Spring Security](06-oauth2-jwt.md)

## 💻 Код модуля

Рядом со статьями живёт запускаемый код (Gradle-подпроект `:java-spring:04-config-security`):

- [`src/main/java/...`](src/main/java/com/javaroadmap/spring/s04/) — `@ConfigurationProperties` +
  профиль dev, ДВЕ security-цепочки (Basic для `/api/**`, JWT resource server для `/api/jwt/**`),
  URL-авторизация по ролям, `@PreAuthorize`, CORS, выпуск JWT (HS256) через `JwtEncoder`.
- [`src/test/java/...`](src/test/java/com/javaroadmap/spring/s04/) — биндинг конфигурации и профилей,
  401 vs 403 c `httpBasic(...)`, method security c `@WithMockUser`, preflight-тесты CORS,
  полный JWT-флоу: Basic-логин → токен → Bearer-доступ.
- [`homework/`](homework/README.md) — домашка: 5 заданий с «красными» тестами
  (`./gradlew :java-spring:04-config-security:homeworkTest`).

```bash
./gradlew :java-spring:04-config-security:test          # тесты примеров
./gradlew :java-spring:04-config-security:homeworkTest  # «красные» задания
```

---

[📚 К треку Java Spring](../README.md)
