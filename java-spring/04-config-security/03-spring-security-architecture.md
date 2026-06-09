# Лекция: Структура и настройка Spring Security

> Java Spring · Модуль 4 — Конфигурация и безопасность · [⬅ К содержанию трека](../README.md)

## Введение

Представьте вход в закрытый бизнес-центр. Вы не попадаете внутрь одним шагом — вы проходите цепочку постов:
охранник на входе, турникет по пропуску, лифт с ключ-картой, ресепшен на этаже. Каждый пост проверяет
что-то своё и либо пропускает дальше, либо разворачивает. Если все посты пройдены — вы в кабинете.

Spring Security устроен ровно так же. Каждый HTTP-запрос, прежде чем дойти до вашего контроллера, проходит
**цепочку фильтров (filter chain)** — последовательность проверок: «есть ли логин?», «валиден ли токен?»,
«есть ли права?». Давайте разберём эту архитектуру изнутри: из чего состоит цепочка, какие абстракции
двигают аутентификацию и как собрать рабочую конфигурацию на Spring Security 6.

## Цепочка фильтров: общая картина

В основе Spring Security лежат **сервлетные фильтры (servlet filters)** — перехватчики, которые видят
запрос до контроллера. Security вставляет в стандартную цепочку сервлет-контейнера свой набор фильтров,
каждый из которых отвечает за один аспект безопасности.

```
HTTP-запрос
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│              Цепочка фильтров Spring Security             │
│                                                          │
│  CsrfFilter ──▶ ... ──▶ UsernamePasswordAuthFilter       │
│       │                          │                       │
│       ▼                          ▼                       │
│  AuthorizationFilter ──▶ ExceptionTranslationFilter      │
└───────────────────────────┬──────────────────────────────┘
                            │ все проверки пройдены
                            ▼
                   ┌─────────────────┐
                   │  Ваш контроллер │   ← сюда запрос дойдёт, только пройдя цепочку
                   └─────────────────┘
```

Если какой-то фильтр решит, что запрос не проходит (нет аутентификации, нет прав) — он прервёт цепочку и
вернёт `401` или `403`, не пустив запрос к контроллеру.

## Как Security встраивается: DelegatingFilterProxy и FilterChainProxy

Здесь возникает стык двух миров. Сервлет-контейнер (Tomcat) ничего не знает о бинах Spring, а фильтры
Security — это бины. Как их подружить? Через два моста.

- **`DelegatingFilterProxy`** — единственный фильтр, который регистрируется в сервлет-контейнере. Сам он
  ничего не делает, а **делегирует** работу бину из Spring-контекста. Это переходник «мир Tomcat → мир
  Spring».
- **`FilterChainProxy`** — тот самый бин, которому делегирует прокси. Внутри него лежат все
  `SecurityFilterChain` и их фильтры. Он смотрит на запрос и выбирает, какую цепочку применить.

```
Сервлет-контейнер (Tomcat)            Spring-контекст
─────────────────────────            ───────────────
DelegatingFilterProxy   ── делегирует ──▶  FilterChainProxy
(знает контейнер)                          └─ SecurityFilterChain
                                              ├─ CsrfFilter
                                              ├─ UsernamePasswordAuthFilter
                                              └─ AuthorizationFilter
```

На практике вы это вручную не настраиваете — Spring Boot регистрирует `DelegatingFilterProxy`
автоматически. Но знать про мост важно: когда что-то идёт не так, понимаешь, где искать.

## Ключевые абстракции аутентификации

Внутри фильтров аутентификацию двигают несколько интерфейсов. Разберём, кто за что отвечает — это сердце
Spring Security.

- **`Authentication`** — объект, представляющий попытку или результат входа. До проверки в нём логин и
  пароль; после успеха — кто пользователь и его права (authorities).
- **`AuthenticationManager`** — главный «менеджер входа». Принимает `Authentication` и решает: пускать или
  нет. Сам не проверяет, а делегирует провайдерам.
- **`AuthenticationProvider`** — конкретный способ проверки (по паролю из БД, по LDAP, по токену).
  Менеджер перебирает провайдеров, пока один не справится.
- **`UserDetailsService`** — поставляет данные пользователя по логину: метод `loadUserByUsername`
  возвращает `UserDetails` (логин, хеш пароля, роли). Откуда брать — из БД, из памяти — решаете вы.
- **`SecurityContextHolder`** — хранилище текущего аутентифицированного пользователя. После успешного
  входа сюда кладётся `Authentication`, и из любого места кода можно спросить «кто сейчас залогинен».

```
Authentication (логин+пароль)
        │
        ▼
AuthenticationManager  ──▶  AuthenticationProvider  ──▶  UserDetailsService
                                       │                  (грузит UserDetails из БД)
                                       │ успех
                                       ▼
                            SecurityContextHolder        ← здесь лежит «кто залогинен»
                            (хранит Authentication)
```

```java
// достать текущего пользователя из любого места кода
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();   // ← логин залогиненного пользователя
```

## Конфигурация: SecurityFilterChain как бин

В Spring Security 6 (Spring Boot 3.x) конфигурация — это **бин `SecurityFilterChain`**. ⚠️ Раньше для
этого наследовали `WebSecurityConfigurerAdapter` — он **удалён** и больше не используется. Современный
стиль — объявить бин и описать правила через lambda-DSL.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth                 // ← правила доступа к URL
                .requestMatchers("/public/**").permitAll()      // ← открыто всем
                .requestMatchers("/admin/**").hasRole("ADMIN")   // ← только роль ADMIN
                .anyRequest().authenticated()                    // ← всё остальное — после входа
            )
            .httpBasic(Customizer.withDefaults());               // ← способ аутентификации: HTTP Basic

        return http.build();   // ← собираем цепочку фильтров
    }
}
```

Разберём по строкам:

- `@EnableWebSecurity` — включает поддержку веб-безопасности (в Spring Boot часто уже включено
  автоконфигурацией, но указать явно — норма).
- `authorizeHttpRequests` — блок правил авторизации; порядок важен, правила проверяются **сверху вниз**.
- `permitAll()` / `hasRole(...)` / `authenticated()` — что требуется для доступа к матчингу URL.
- `httpBasic(...)` — выбираем механизм аутентификации (про варианты — в [статье 4](04-authentication-authorization.md)).
- `http.build()` — превращает настройки в готовый бин цепочки.

⚠️ **Правило порядка:** более конкретные матчеры (`/admin/**`) пишите *выше* общих (`anyRequest()`).
Spring берёт первое совпавшее правило — если `anyRequest()` окажется первым, он перехватит всё.

## PasswordEncoder: бин для паролей

Из [предыдущей статьи](02-web-security-basics.md) мы знаем: пароли хранят только хешем. В Spring это
интерфейс `PasswordEncoder`, который тоже объявляется бином — Security будет использовать его и при входе,
и при регистрации.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();   // ← медленный хеш + соль внутри
}
```

Как им пользоваться:

```java
// при регистрации — кодируем перед сохранением
String hash = passwordEncoder.encode(rawPassword);   // ← в БД кладём hash, не rawPassword

// при входе Security сам сравнивает введённый пароль с хешем
boolean ok = passwordEncoder.matches(rawPassword, storedHash);   // ← true/false
```

✅ `BCryptPasswordEncoder` — разумный дефолт. Если нужна гибкость (поддержка нескольких алгоритмов сразу),
есть `DelegatingPasswordEncoder` — он хранит в хеше префикс алгоритма (`{bcrypt}...`) и умеет плавно
мигрировать на новые схемы.

## Что в итоге происходит с запросом

Соберём всё вместе на примере запроса к защищённому `/admin/users` под HTTP Basic.

1. Запрос входит в `DelegatingFilterProxy` → делегирует `FilterChainProxy`.
2. `FilterChainProxy` выбирает наш `SecurityFilterChain` и гонит запрос по фильтрам.
3. Фильтр аутентификации читает заголовок `Authorization`, строит `Authentication` и отдаёт
   `AuthenticationManager`.
4. Менеджер через `AuthenticationProvider` зовёт `UserDetailsService`, грузит пользователя, сверяет пароль
   через `PasswordEncoder`.
5. Успех → `Authentication` кладётся в `SecurityContextHolder`.
6. Фильтр авторизации проверяет правило `/admin/** → hasRole("ADMIN")`. Роль есть → запрос идёт к
   контроллеру; нет → `403`.

```http
GET /admin/users HTTP/1.1
Authorization: Basic YWRtaW46cGFzcw==      ← логин:пароль в Base64 (поэтому только по HTTPS!)
```

## Заключение

**Что изучено:**

- Каждый запрос проходит цепочку фильтров (filter chain), где каждый фильтр отвечает за свой аспект безопасности.
- `DelegatingFilterProxy` соединяет сервлет-контейнер со Spring, делегируя работу `FilterChainProxy`.
- Аутентификацию двигают `Authentication`, `AuthenticationManager`, `AuthenticationProvider`,
  `UserDetailsService`; текущий пользователь живёт в `SecurityContextHolder`.
- Конфигурация в Spring Security 6 — это бин `SecurityFilterChain` с lambda-DSL; `WebSecurityConfigurerAdapter` удалён.
- Пароли обслуживает бин `PasswordEncoder` (`BCryptPasswordEncoder`).

**Как применять на практике:**

- Объявляйте `SecurityFilterChain` бином и настраивайте через lambda-DSL — без устаревшего адаптера.
- Правила в `authorizeHttpRequests` пишите от частного к общему; `anyRequest()` — всегда последним.
- Заводите `PasswordEncoder` бином и используйте `encode`/`matches`, а не самописные хеши.
- `SecurityContextHolder.getContext().getAuthentication()` — способ узнать текущего пользователя в коде.

**Что дальше:** каркас собран — настроим конкретные способы входа (form login, HTTP Basic), подключим
пользователей из БД и разрулим права на уровне URL и методов.

## Ресурсы

- [Spring Security — Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html) — фильтры, `DelegatingFilterProxy`, `FilterChainProxy`.
- [Spring Security — Authentication Architecture](https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html) — `Authentication`, `AuthenticationManager`, `SecurityContextHolder`.
- [Baeldung — Spring Security 6 SecurityFilterChain](https://www.baeldung.com/spring-deprecated-websecurityconfigureradapter) — переход с адаптера на бин-конфигурацию.

[⬅ Основы безопасности веб-приложений](02-web-security-basics.md) · [📑 Оглавление модуля](README.md) · [Аутентификация и авторизация ➡](04-authentication-authorization.md)
