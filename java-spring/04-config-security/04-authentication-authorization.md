# Лекция: Аутентификация и авторизация

> Java Spring · Модуль 4 — Конфигурация и безопасность · [⬅ К содержанию трека](../README.md)

## Введение

Вернёмся к нашему аэропорту из [второй статьи](02-web-security-basics.md). Мы уже знаем: аутентификация —
это паспортный контроль («кто ты?»), а авторизация — билет в бизнес-класс («что тебе можно?»). В прошлой
статье мы разобрали архитектуру Spring Security и собрали каркас. Теперь пора заселить его людьми и
правилами.

Давайте разберём практику: какими способами пользователь может войти, как подтянуть пользователей из
настоящей базы данных, чем роли отличаются от прав и как ограничить доступ — отдельно к URL и отдельно к
методам сервисов. Это та часть, которую вы будете писать в каждом реальном проекте.

## Способы аутентификации

Spring Security поддерживает несколько механизмов входа. Выбор зависит от того, что за приложение.

- **Form login** — классическая HTML-форма с полями «логин/пароль». Подходит для веб-приложений со
  страницами. После входа сервер заводит сессию.
- **HTTP Basic** — логин и пароль передаются в заголовке `Authorization` при каждом запросе. Просто,
  годится для внутренних сервисов и быстрых проверок. ⚠️ Только по HTTPS — пароль едет в Base64, это не
  шифрование.
- **JWT / токены** — клиент получает подписанный токен и шлёт его в заголовке. Stateless, идеально для
  API, SPA и мобильных приложений. Подробно — в [статье 6](06-oauth2-jwt.md).

```java
http
    .formLogin(Customizer.withDefaults())    // ← HTML-форма входа (для страниц)
    .httpBasic(Customizer.withDefaults());   // ← плюс HTTP Basic (для API/curl)
```

Механизмы можно комбинировать, но обычно под тип клиента выбирают один. Для остального модуля сосредоточимся
на пользователях из БД — это основа.

## Кастомный UserDetailsService: пользователь из БД

По умолчанию Spring Security умеет держать пользователей в памяти — удобно для демо, бесполезно для прода.
В реальном приложении пользователи лежат в базе. Чтобы Security их видел, мы реализуем
`UserDetailsService` — он по логину возвращает `UserDetails` (логин, хеш пароля, права).

Сначала — сущность и репозиторий (из [модуля про Spring Data JPA](../02-spring-data-jpa/README.md)):

```java
@Entity
public class AppUser {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String passwordHash;     // ← в БД только хеш, не plaintext
    private String role;             // ← например, "ADMIN" или "USER"
    // геттеры опущены для краткости
}

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);   // ← Spring Data сгенерирует запрос
}
```

Теперь сам сервис — мост между нашей таблицей и Spring Security:

```java
@Service
public class DbUserDetailsService implements UserDetailsService {
    private final AppUserRepository repo;

    public DbUserDetailsService(AppUserRepository repo) {   // ← конструкторная инъекция
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));   // ← нет → 401

        return User.builder()                       // ← адаптируем нашу сущность к UserDetails
            .username(user.getUsername())
            .password(user.getPasswordHash())       // ← отдаём хеш, Security сверит сам
            .roles(user.getRole())                  // ← "ADMIN" → authority ROLE_ADMIN
            .build();
    }
}
```

Spring Security сам найдёт этот бин, вызовет `loadUserByUsername` при входе и сверит введённый пароль с
хешем через `PasswordEncoder`. Больше ничего подключать не нужно — связка заработает.

## Роли против authorities

Здесь часто возникает путаница, давайте расставим точки.

- **Authority (право)** — базовая единица доступа, просто строка: `READ_ORDERS`, `DELETE_USER`,
  `ROLE_ADMIN`. Security оперирует именно authorities.
- **Role (роль)** — это та же authority, но по соглашению с префиксом `ROLE_`. Роль `ADMIN` — это
  authority `ROLE_ADMIN`. Роль — грубая группировка («админ», «пользователь»), authority — мелкое
  конкретное право.

```
Роль ADMIN  ==  authority "ROLE_ADMIN"        ← роль = authority с префиксом ROLE_
                                              ← hasRole("ADMIN") ищет "ROLE_ADMIN"
                                              ← hasAuthority("ROLE_ADMIN") — то же, но префикс пишем сами
```

⚠️ Ключевое: `hasRole("ADMIN")` автоматически добавляет префикс и ищет `ROLE_ADMIN`. А
`hasAuthority("ADMIN")` ищет ровно `ADMIN`, без префикса. Смешивать их — частый источник багов «доступ не
работает, хотя роль есть».

| Понятие | Что это | Пример | Метод проверки |
|---|---|---|---|
| Authority | любое право-строка | `READ_ORDERS` | `hasAuthority("READ_ORDERS")` |
| Role | authority с префиксом `ROLE_` | `ROLE_ADMIN` | `hasRole("ADMIN")` |

## Авторизация на уровне URL

Самый частый способ — ограничить доступ к путям. Это делается в `SecurityFilterChain`, в блоке
`authorizeHttpRequests`. Каждый `requestMatchers` задаёт правило для группы URL.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/public/**").permitAll()          // ← открыто всем
            .requestMatchers(HttpMethod.GET, "/api/orders").hasAuthority("READ_ORDERS")  // ← по праву
            .requestMatchers("/admin/**").hasRole("ADMIN")           // ← только роль ADMIN
            .anyRequest().authenticated()                            // ← остальное — после входа
        )
        .formLogin(Customizer.withDefaults());
    return http.build();
}
```

Можно ограничивать не только путь, но и HTTP-метод: `GET /api/orders` — по одному правилу, `DELETE` — по
другому. ⚠️ Помните про порядок: правила читаются сверху вниз, частное — выше общего.

## Авторизация на уровне методов

Иногда URL-правил мало: одну и ту же бизнес-операцию вызывают из разных мест, и защищать хочется сам
метод, а не путь. Для этого есть **method security** — аннотации прямо на методах. Сначала их надо
включить:

```java
@Configuration
@EnableMethodSecurity   // ← включает @PreAuthorize / @PostAuthorize / @Secured
public class MethodSecurityConfig { }
```

Теперь можно навешивать проверки на методы сервисов:

```java
@Service
public class OrderService {

    @PreAuthorize("hasRole('ADMIN')")          // ← проверка ДО вызова метода
    public void deleteOrder(Long id) {
        // сюда попадём, только если у пользователя роль ADMIN
    }

    @PreAuthorize("hasAuthority('READ_ORDERS')")   // ← по конкретному праву
    public List<Order> listOrders() {
        return repo.findAll();
    }

    @Secured("ROLE_MANAGER")                   // ← более простой вариант: только роли, без SpEL
    public void approve(Long id) { }
}
```

- `@PreAuthorize` — самый гибкий: принимает SpEL-выражение, может ссылаться на аргументы метода и
  текущего пользователя. Например, `@PreAuthorize("#userId == authentication.name")` — «только свои
  данные».
- `@Secured` — проще: только список ролей-строк, без выражений.

## URL-security против method-security

Когда что выбирать? Они не конкурируют, а дополняют друг друга — обычно используют оба слоя.

| Критерий | URL-security | Method-security |
|---|---|---|
| Где задаётся | в `SecurityFilterChain` | аннотацией на методе |
| Гранулярность | по путям/HTTP-методам | по конкретному методу |
| Доступ к аргументам вызова | ❌ нет | ✅ да (`#id`, `#user`) |
| Логика «только свои данные» | ⚠️ сложно | ✅ через SpEL |
| Защита при вызове из разных мест | ⚠️ нужно покрыть все URL | ✅ метод защищён везде |
| Хорошо для | грубого деления зон (`/admin/**`) | точечной бизнес-логики |

✅ Типичный подход: URL-security рисует крупные границы («админка только для ADMIN»), а method-security
закрывает тонкие правила в сервисах («редактировать может только владелец»). Защита в два слоя надёжнее
одного.

## Заключение

**Что изучено:**

- Способы входа: form login (страницы), HTTP Basic (внутренние сервисы/API), JWT (stateless API).
- Кастомный `UserDetailsService` подтягивает пользователей из БД и отдаёт их Security как `UserDetails`.
- Роль — это authority с префиксом `ROLE_`; `hasRole("ADMIN")` ищет `ROLE_ADMIN`, `hasAuthority` — точную строку.
- URL-security (`requestMatchers(...).hasRole(...)`) делит крупные зоны; method-security
  (`@PreAuthorize`/`@Secured` при `@EnableMethodSecurity`) защищает конкретные методы.

**Как применять на практике:**

- Пользователей храните в БД и подключайте через свой `UserDetailsService`; пароль — только хешем.
- Не путайте `hasRole` и `hasAuthority` — помните про префикс `ROLE_`.
- Крупные границы закрывайте URL-правилами, тонкие («только свои данные») — `@PreAuthorize` с SpEL.
- Включайте `@EnableMethodSecurity` явно — без неё аннотации на методах не работают.

**Что дальше:** разберёмся с CORS — почему браузер блокирует запросы между доменами и как правильно их
разрешить, не открыв дыру.

## Ресурсы

- [Spring Security — Authorize HTTP Requests](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html) — авторизация на уровне URL.
- [Spring Security — Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) — `@PreAuthorize`, `@Secured`, `@EnableMethodSecurity`.
- [Baeldung — Spring Security: Roles and Privileges](https://www.baeldung.com/role-and-privilege-for-spring-security-registration) — роли против authorities на практике.

[⬅ Структура и настройка Spring Security](03-spring-security-architecture.md) · [📑 Оглавление модуля](README.md) · [CORS и Spring ➡](05-cors.md)
