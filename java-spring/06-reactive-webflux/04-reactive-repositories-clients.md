# Лекция: Реактивные репозитории и клиенты

> Java Spring · Модуль 6 — Реактивный стек (WebFlux) · [⬅ К содержанию трека](../README.md)

## Введение

В прошлой статье мы построили реактивный контроллер и трижды повторили: блокирующему коду нет места в event loop. Но любой реальный сервис ходит в базу данных и дёргает другие сервисы по HTTP. Если эти обращения блокирующие — вся реактивность насмарку. Значит, нужны **реактивные** способы доступа к данным.

Представьте водопровод. Реактивная цепочка — это труба, по которой вода (данные) течёт без остановок. Стоит вставить в неё одну заглушку — и поток встал, как бы хороши ни были остальные участки. Классический JDBC — именно такая заглушка. Давайте разберём, чем её заменить: реактивными репозиториями на **R2DBC** и неблокирующим HTTP-клиентом **WebClient**.

## Почему JDBC блокирует и ломает реактивность

JDBC спроектирован десятилетия назад под синхронную модель: вызов `executeQuery()` **блокирует** поток, пока БД не вернёт результат. Архитектурно иначе он работать не может — это блокирующий API на уровне дизайна.

```text
Реактивная цепочка с блокирующим JDBC:

  WebClient (non-blocking) ──▶ flatMap ──▶ [ JDBC.executeQuery() ] ──▶ map ──▶ ...
        ✅ поток свободен                    ❌ поток ЗАМЕР здесь —
                                                event loop встал
```

⚠️ Достаточно **одного** блокирующего драйвера в цепочке, чтобы свести весь профит к нулю: поток event loop заблокируется, и сервер потеряет способность обслуживать другие соединения. Поэтому в WebFlux JPA/JDBC напрямую не используют — нужен неблокирующий драйвер.

## R2DBC и ReactiveCrudRepository

**R2DBC (Reactive Relational Database Connectivity)** — это спецификация и набор драйверов для реляционных БД с *неблокирующим* доступом. Есть драйверы для PostgreSQL, MySQL, MariaDB, H2 и других. Spring Data R2DBC даёт поверх них привычные репозитории — только методы возвращают `Mono`/`Flux`.

Зависимости (Gradle):

```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
runtimeOnly("org.postgresql:r2dbc-postgresql")   // неблокирующий драйвер PostgreSQL
```

Сущность и репозиторий выглядят почти как в обычном Spring Data, но с реактивными типами:

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")                                  // ← маппинг на таблицу (без JPA-аннотаций)
public record User(@Id Long id, String name, String email) {}
```

```java
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Flux<User> findByName(String name);          // ← много результатов → Flux

    Mono<User> findByEmail(String email);        // ← 0..1 результат → Mono

    @Query("SELECT * FROM users WHERE email LIKE :domain")
    Flux<User> findByEmailDomain(String domain); // свой SQL — тоже реактивный
}
```

- `ReactiveCrudRepository<User, Long>` — реактивный аналог `CrudRepository`: `save`, `findById`, `findAll`, `deleteById` — всё возвращает `Mono`/`Flux`.
- Spring сам генерирует реализацию запроса из имени метода — как в обычном Spring Data.

Сервис собирает из репозитория реактивную цепочку без единого `block()`:

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {   // конструкторная инъекция
        this.repository = repository;
    }

    public Mono<User> register(String name, String email) {
        return repository.findByEmail(email)                       // Mono<User>
            .flatMap(existing -> Mono.<User>error(                 // уже есть → ошибка
                new EmailTakenException(email)))
            .switchIfEmpty(repository.save(                        // пусто → сохраняем нового
                new User(null, name, email)));
    }
}
```

⚠️ Важное ограничение: R2DBC — это *не* JPA. Здесь нет ленивых связей, кэша первого уровня и автоматических `JOIN` по аннотациям. Связи между таблицами вы собираете руками (через `flatMap`/`zip` или явный SQL). Это плата за неблокирующую модель.

## WebClient: неблокирующая замена RestTemplate

Для обращения к другим HTTP-сервисам в реактивном мире используют **`WebClient`** — неблокирующий клиент (классический `RestTemplate` блокирует поток и в WebFlux неуместен; вдобавок он в режиме поддержки).

`WebClient` обычно создают как бин и инъецируют:

```java
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient ratesClient(WebClient.Builder builder) {
        return builder
            .baseUrl("https://api.rates.example.com")   // базовый адрес сервиса
            .build();
    }
}
```

Дальше — типичные вызовы. Метод `retrieve()` запускает запрос, а `bodyToMono`/`bodyToFlux` превращают тело ответа в реактивный тип:

```java
@Service
public class RateService {

    private final WebClient client;

    public RateService(WebClient ratesClient) {   // инъекция готового бина
        this.client = ratesClient;
    }

    // GET одного объекта → Mono
    public Mono<Rate> getRate(String symbol) {
        return client.get()
            .uri("/rates/{symbol}", symbol)
            .retrieve()                                // выполнить запрос
            .bodyToMono(Rate.class)                    // тело → Mono<Rate>
            .onErrorResume(ex -> Mono.empty());        // ошибка → пустой результат
    }

    // GET коллекции (стрим) → Flux
    public Flux<Rate> getAllRates() {
        return client.get()
            .uri("/rates")
            .retrieve()
            .bodyToFlux(Rate.class);                   // тело → Flux<Rate>
    }

    // POST с телом
    public Mono<Order> placeOrder(OrderRequest req) {
        return client.post()
            .uri("/orders")
            .bodyValue(req)                            // сериализуем тело запроса
            .retrieve()
            .bodyToMono(Order.class);
    }
}
```

✅ Поскольку `WebClient` неблокирующий, такой вызов прекрасно встраивается в цепочку контроллера: пока внешний сервис думает, поток event loop обслуживает других. Несколько независимых вызовов легко распараллелить через `Mono.zip` (см. статью про Reactor).

## Реактивные Redis и Mongo

Реляционные БД — не единственный источник данных. В реактивном стеке есть неблокирующие драйверы и для NoSQL:

- **Reactive MongoDB** — `spring-boot-starter-data-mongodb-reactive`, репозитории `ReactiveMongoRepository` (Mongo отлично ложится на реактивность за счёт асинхронного драйвера).
- **Reactive Redis** — `ReactiveRedisTemplate` для кэша, счётчиков, pub/sub.

Принцип везде один и тот же: методы возвращают `Mono`/`Flux`, драйвер не блокирует поток. ✅ Это позволяет держать **сквозную** реактивную цепочку: HTTP-запрос → `WebClient` → реактивная БД → ответ — без единой блокирующей точки.

## ⚠️ Один блокирующий драйвер убивает весь профит

Повторим главную мысль модуля, потому что на ней спотыкаются чаще всего. Реактивный стек хорош только целиком. Если в сквозную цепочку затесался хоть один блокирующий вызов — выигрыша от WebFlux не будет, а отладка усложнится.

| Блокирующее (❌ в event loop) | Реактивная замена (✅)                  |
|-------------------------------|----------------------------------------|
| JDBC / Spring Data JPA        | R2DBC / Spring Data R2DBC              |
| `RestTemplate`                | `WebClient`                            |
| Jedis (Redis, sync)           | `ReactiveRedisTemplate` (Lettuce)      |
| Mongo sync driver             | Reactive MongoDB driver                |
| блокирующее чтение файла      | неблокирующий I/O / вынос на пул       |

Если заменить драйвер невозможно (легаси), изолируйте блокирующий вызов на `Schedulers.boundedElastic()` (как показано в статье про WebFlux) — но это компромисс, а не полноценная реактивность. ✅ Честный вывод: либо стек реактивный *целиком*, либо проще и надёжнее остаться на привычном блокирующем Spring MVC.

## Заключение

**Что изучено:**
- JDBC блокирует поток by design и несовместим с реактивной цепочкой.
- R2DBC + `ReactiveCrudRepository` дают неблокирующий доступ к реляционным БД с методами, возвращающими `Mono`/`Flux`.
- `WebClient` — неблокирующая замена `RestTemplate`: `retrieve()` + `bodyToMono`/`bodyToFlux`.
- Есть реактивные драйверы и для Mongo/Redis — можно держать сквозную реактивную цепочку.
- Один блокирующий драйвер обнуляет весь выигрыш WebFlux.

**Как применять на практике:**
- Под WebFlux берите R2DBC и `WebClient`; забудьте про JPA/JDBC и `RestTemplate` в реактивных сервисах.
- Связи между таблицами в R2DBC собирайте руками (`flatMap`/`zip`/SQL) — ORM-магии JPA здесь нет.
- Перед выбором WebFlux проверьте, что для *всех* источников данных есть реактивные драйверы; иначе оставайтесь на MVC.

**Что дальше:** разберём механизм, который защищает реактивный конвейер от переполнения, — backpressure и управление темпом потока.

---

Полезные ссылки:
- [Spring Data R2DBC — Reference](https://docs.spring.io/spring-data/relational/reference/r2dbc.html)
- [Spring — WebClient (Reference)](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)
- [Baeldung — Spring Data R2DBC](https://www.baeldung.com/spring-data-r2dbc)

[⬅ Реактивный подход: Spring WebFlux](03-spring-webflux.md) · [📑 Оглавление модуля](README.md) · [Backpressure и управление потоком ➡](05-backpressure.md)
