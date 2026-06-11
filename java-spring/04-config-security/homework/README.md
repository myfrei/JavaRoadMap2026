# Домашка модуля 4 — Конфигурация и безопасность

> Статьи модуля: [оглавление](../README.md) · Примеры кода: [`src/main/java/...`](../src/main/java/com/javaroadmap/spring/s04/)

Заготовки лежат в [`homework/src/main/java/.../homework/`](src/main/java/com/javaroadmap/spring/s04/homework/).
Сделай «красные» тесты [`Mod04HomeworkTest.java`](src/test/java/com/javaroadmap/spring/s04/homework/Mod04HomeworkTest.java)
зелёными. **Файл с тестами трогать не нужно.** Примеры модуля — твой основной справочник:
почти каждое задание имеет образец в `src/main`.

## Как работать

1. Открой заготовку задания, прочитай TODO и формулировку ниже.
2. Реализуй задания по порядку.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :java-spring:04-config-security:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

### 1. Типобезопасная конфигурация (статья 01)

`HwProperties`

**Задача.** Сделай record конфигурацией с префиксом `hw`. Значения (`hw.motd`, `hw.max-attempts`)
уже в `application.properties`; `@ConfigurationPropertiesScan` в Main найдёт класс сам.

**Ожидаемый результат.** Бин `HwProperties` доступен из контекста, поля заполнены значениями
из properties (включая релакс-биндинг `max-attempts` → `maxAttempts`).

### 2. Своя security-цепочка (статьи 02–04)

`HwController` (2а), `HwSecurityConfig` (2б)

**Задача.** Преврати `HwController` в REST-контроллер (`/hw/public/ping`, `/hw/secret`,
`/hw/admin/panel`) и напиши для путей `/hw/**` собственную `SecurityFilterChain`
с HTTP Basic: public — без аутентификации, admin — только роль ADMIN, остальное —
любой аутентифицированный. Пользователи общие с примерами: `user/user123`, `admin/admin123`.

**Ожидаемый результат.** Аноним: ping → 200, secret → **401**. `user`: secret → 200,
panel → **403**. `admin`: panel → 200. Почувствуй разницу 401/403 руками.

### 3. Method security (статья 04)

`HwReportService`

**Задача.** Сделай класс бином и закрой `exportAll()` так, чтобы вызвать его могла только
роль ADMIN — на уровне **метода**, не URL (`@EnableMethodSecurity` уже включён в примерах).

**Ожидаемый результат.** Вызов от USER — `AccessDeniedException`, от ADMIN — результат.
HTTP в этом задании вообще не участвует.

### 4. CORS (статья 05)

`HwCorsConfig`

**Задача.** Через `WebMvcConfigurer#addCorsMappings` разреши для `/hw/public/**`
origin `https://hw.example.com` и методы GET, POST.

**Ожидаемый результат.** Preflight `OPTIONS /hw/public/ping` с этим origin получает
заголовок `Access-Control-Allow-Origin`.

### 5. Выпуск JWT (статья 06)

`HwTokenService`

**Задача.** Сделай класс бином и реализуй `issueToken`: подпиши токен уже настроенным
`JwtEncoder`-ом (HS256), `subject` = имя, claim `roles` = список ролей, срок жизни —
ровно 15 минут. Образец — `TokenController` из примеров.

**Ожидаемый результат.** `JwtDecoder` (тот же секрет) успешно проверяет токен;
в нём правильные subject, роли и `expiresAt - issuedAt == 15 минут`.

## 🎓 Навыки после модуля

- Выносишь настройки в **@ConfigurationProperties** с профилями вместо хардкода и `@Value`-россыпи.
- Пишешь **SecurityFilterChain** под конкретные пути и понимаешь, что цепочек может быть несколько.
- Различаешь **401 и 403** и настраиваешь авторизацию на двух уровнях: URL и метод (`@PreAuthorize`).
- Понимаешь **CORS**: что такое preflight и почему «ошибка CORS» — это решение браузера, а не сервера.
- Работаешь с **JWT**: выпуск, подпись, проверка, claims и срок жизни токена.

## Готово, когда

- [ ] `./gradlew :java-spring:04-config-security:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../README.md) · [← Домашка 3](../../03-mvc-controllers/homework/README.md) · [К треку Java Spring](../../README.md)
