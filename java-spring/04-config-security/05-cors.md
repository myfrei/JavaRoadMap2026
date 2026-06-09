# Лекция: CORS и Spring

> Java Spring · Модуль 4 — Конфигурация и безопасность · [⬅ К содержанию трека](../README.md)

## Введение

Представьте, что вы сидите в кафе и просите официанта принести что-нибудь из ресторана через дорогу. Он
вежливо откажет: «Я обслуживаю только наше заведение». Логично — иначе любой мог бы гонять персонал по
всему городу. Но если у двух заведений договор, официант сначала сходит и уточнит: «А вам можно сюда
заказывать?» — и только потом примет заказ.

Браузер ведёт себя точно так же. По умолчанию страница с одного домена не может дёргать API на другом
домене — это защита под названием **Same-Origin Policy**. А **CORS** — это механизм договорённости,
который позволяет серверу явно сказать: «вот этим чужим сайтам ко мне можно». Давайте разберём, как это
работает и как настроить в Spring, не наделав типичных ошибок.

## Same-Origin Policy: почему браузер блокирует

**Origin (источник)** — это тройка: схема + домен + порт. Например, `https://app.example.com:443`. Два URL
имеют один origin, только если совпадают все три части.

**Same-Origin Policy (SOP)** — правило браузера: скрипт со страницы может свободно обращаться только к
своему origin. Запрос на чужой origin браузер по умолчанию блокирует. Это защищает вас: вредоносный сайт
не сможет из вашего браузера тихо дёргать API вашего банка.

```
Страница открыта на  https://app.example.com
─────────────────────────────────────────────
fetch("https://app.example.com/data")   ← ✅ тот же origin — разрешено
fetch("https://api.example.com/data")   ← ❌ другой домен — заблокировано (если нет CORS)
fetch("http://app.example.com/data")    ← ❌ другая схема (http≠https) — заблокировано
fetch("https://app.example.com:8443/x") ← ❌ другой порт — заблокировано
```

⚠️ Важно: блокирует именно **браузер**, и блокирует он *чтение ответа* фронтендом. Сервер запрос может и
получить. Поэтому CORS — это про фронтенд в браузере; `curl` или другой бэкенд никаких CORS-ограничений не
видят.

## Как работает preflight

Для «простых» запросов (обычный `GET`, простой `POST`) браузер шлёт запрос сразу и потом смотрит на
заголовки ответа. Но если запрос «непростой» — например, `DELETE`, или с заголовком `Authorization`, или с
`Content-Type: application/json` — браузер сначала отправляет **preflight**: предварительный запрос
методом `OPTIONS`, чтобы спросить разрешение.

```
Браузер (app.example.com)                          Сервер (api.example.com)
        │                                                   │
        │   1. PREFLIGHT — «можно?»                         │
        │   OPTIONS /orders/5                               │
        │   Origin: https://app.example.com                │
        │   Access-Control-Request-Method: DELETE          │
        │ ─────────────────────────────────────────────▶   │
        │                                                   │
        │   2. Ответ — «можно, вот условия»                 │
        │   Access-Control-Allow-Origin: https://app...    │
        │   Access-Control-Allow-Methods: GET,POST,DELETE  │
        │ ◀─────────────────────────────────────────────   │
        │                                                   │
        │   3. Браузер видит разрешение → шлёт реальный     │
        │   DELETE /orders/5                                │
        │ ─────────────────────────────────────────────▶   │
        │                                                   │
        │   4. Реальный ответ (тоже с CORS-заголовками)     │
        │ ◀─────────────────────────────────────────────   │
```

Если на шаге 2 сервер не вернул нужные `Access-Control-*` заголовки — браузер прервёт всё и не отправит
реальный запрос. Именно поэтому в консоли вы видите загадочную CORS-ошибку, хотя сервер «вроде работает».

## Заголовки CORS

Договорённость выражается набором HTTP-заголовков ответа. Вот ключевые.

| Заголовок | Что говорит браузеру |
|---|---|
| `Access-Control-Allow-Origin` | каким origin можно (`https://app.example.com` или `*`) |
| `Access-Control-Allow-Methods` | какие HTTP-методы разрешены (`GET, POST, DELETE`) |
| `Access-Control-Allow-Headers` | какие заголовки можно слать (`Authorization, Content-Type`) |
| `Access-Control-Allow-Credentials` | можно ли слать куки/credentials (`true`) |
| `Access-Control-Max-Age` | сколько секунд кэшировать preflight (чтобы не спрашивать каждый раз) |

## Настройка в Spring: три уровня

Spring даёт несколько способов разрешить CORS. Идём от точечного к глобальному.

**1. Точечно на контроллере — `@CrossOrigin`:**

```java
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "https://app.example.com")   // ← разрешаем этому origin для всего контроллера
public class OrderController {
    @GetMapping
    public List<Order> list() { return service.findAll(); }
}
```

**2. Глобально — бин `CorsConfigurationSource`:** одна настройка на всё приложение.

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://app.example.com"));   // ← конкретный origin, не "*"
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);                               // ← разрешаем куки/токены

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);   // ← применяем ко всем путям
        return source;
    }
}
```

**3. Связка со Spring Security.** ⚠️ Это критично: если в проекте есть Spring Security, он стоит *раньше* в
цепочке фильтров и сам должен пропустить preflight `OPTIONS`. Поэтому CORS нужно явно включить в
`SecurityFilterChain` — иначе глобальный бин просто не сработает.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())     // ← подхватывает бин CorsConfigurationSource
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

`http.cors(...)` говорит Security: примени CORS-настройки до проверки авторизации и пропусти preflight.
Без этой строки браузерные запросы с другого домена будут падать, даже если бин CORS объявлен.

## Частые ошибки

CORS — благодатная почва для граблей. Запомните три главных.

- ⚠️ **CORS — это не защита.** Он защищает *пользователя в браузере*, а не ваш сервер. Любой может
  обратиться к API напрямую (`curl`, Postman) — CORS-заголовки на это не влияют. Серверная защита — это
  аутентификация и авторизация, а не CORS.
- ⚠️ **`allowCredentials(true)` несовместим с `allowedOrigins("*")`.** Если разрешаете куки/токены,
  обязаны указать **конкретные** origin — браузер запрещает связку «звёздочка + credentials». Используйте
  список доменов, а не `*`.
- ⚠️ **Не открывайте `*` бездумно.** `Access-Control-Allow-Origin: *` означает «любой сайт может звать мой
  API из браузера». Для публичного открытого API это нормально; для приватного — указывайте точный список
  доверенных origin.

```java
// ❌ так браузер отвергнет: credentials + "*" запрещено стандартом
config.setAllowedOrigins(List.of("*"));
config.setAllowCredentials(true);

// ✅ с credentials — только конкретные origin
config.setAllowedOrigins(List.of("https://app.example.com"));
config.setAllowCredentials(true);
```

## Заключение

**Что изучено:**

- Same-Origin Policy блокирует браузерные запросы между разными origin (схема + домен + порт).
- CORS — механизм, которым сервер разрешает конкретным чужим origin; «непростые» запросы предваряет
  preflight (`OPTIONS` + заголовки `Access-Control-*`).
- В Spring CORS настраивается точечно (`@CrossOrigin`), глобально (`CorsConfigurationSource`) и
  обязательно связывается со Spring Security через `http.cors(...)`.
- Главные грабли: CORS — не защита; `allowCredentials(true)` несовместим с `*`.

**Как применять на практике:**

- Для SPA + отдельного API заводите глобальный `CorsConfigurationSource` и подключайте его в `SecurityFilterChain`.
- Перечисляйте конкретные доверенные origin; `*` — только для публичного API без credentials.
- Если запрос «непростой» (JSON, `Authorization`, `DELETE`) — закладывайтесь на preflight `OPTIONS`.
- Не путайте CORS с безопасностью сервера: защищают аутентификация и авторизация, а не CORS-заголовки.

**Что дальше:** перейдём к делегированному доступу — разберём OAuth2 и JWT, и как Spring Security работает
в роли resource server.

## Ресурсы

- [Spring Security — CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html) — связка CORS со Spring Security.
- [MDN — CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) — как работают preflight и заголовки.
- [Baeldung — CORS with Spring](https://www.baeldung.com/spring-cors) — `@CrossOrigin` и глобальная настройка.

[⬅ Аутентификация и авторизация](04-authentication-authorization.md) · [📑 Оглавление модуля](README.md) · [OAuth2 и JWT в Spring Security ➡](06-oauth2-jwt.md)
