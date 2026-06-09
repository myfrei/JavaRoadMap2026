# Лекция: Реактивный подход: Spring WebFlux

> Java Spring · Модуль 6 — Реактивный стек (WebFlux) · [⬅ К содержанию трека](../README.md)

## Введение

Мы научились собирать конвейеры из `Mono` и `Flux`. Теперь подключим их к вебу. В экосистеме Spring за реактивный стек отвечает **Spring WebFlux** — альтернатива привычному Spring MVC, построенная не на блокирующих сервлетах, а на неблокирующем event loop.

Представьте разницу между двумя кофейнями. В первой бариста принимает заказ, варит кофе до конца, отдаёт — и только потом зовёт следующего; чтобы обслуживать больше людей, нужно больше бариста. Во второй бариста принимает заказ, ставит его в работу и сразу зовёт следующего; когда напиток готов, он выкрикивает имя. Один человек крутит десятки заказов. Spring MVC — первая кофейня, WebFlux — вторая. Давайте разберём, чем именно они отличаются, как пишется реактивный контроллер и почему в нём нельзя «варить кофе до конца» (блокировать поток).

## WebFlux vs Spring MVC

Оба фреймворка живут в Spring и используют знакомые аннотации (`@RestController`, `@GetMapping`), но под капотом устроены принципиально по-разному.

| Характеристика        | Spring MVC                       | Spring WebFlux                       |
|-----------------------|----------------------------------|--------------------------------------|
| **Базовый API**       | Servlet API                      | Reactive Streams                     |
| **Сервер по умолчанию**| Tomcat (Servlet-контейнер)      | Netty (event loop)                   |
| **Модель I/O**        | блокирующая                      | неблокирующая                        |
| **Модель потоков**    | thread-per-request               | event loop, мало потоков             |
| **Тип результата**    | `User`, `List<User>`             | `Mono<User>`, `Flux<User>`           |
| **Доступ к данным**   | JDBC/JPA (блокирующий)           | R2DBC, реактивные Mongo/Redis        |
| **HTTP-клиент**       | `RestTemplate`                   | `WebClient`                          |
| **Backpressure**      | нет                              | есть                                 |

⚠️ Важно: WebFlux **не быстрее** MVC на одиночном запросе — латентность примерно та же. Его сила — в *масштабируемости под высокой конкурентностью*: он удерживает тысячи соединений горсткой потоков. На умеренной нагрузке MVC проще и ничем не хуже.

## Аннотационная модель контроллера

Самый простой путь в WebFlux — те же аннотации, что и в MVC, только методы возвращают `Mono`/`Flux`. Это знакомо и удобно для большинства задач.

```java
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {   // ← конструкторная инъекция
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return service.findById(id);               // вернули Mono — НЕ .block()!
    }                                              // подпишется сам WebFlux

    @GetMapping
    public Flux<User> getAll() {
        return service.findAll();                  // вернули Flux — поток пользователей
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> create(@RequestBody Mono<User> body) {
        return body.flatMap(service::save);        // тело запроса — тоже реактивное
    }
}
```

Ключевая мысль: **контроллер ничего не «вычисляет» сам** — он лишь возвращает рецепт (`Mono`/`Flux`). Фреймворк подпишется на него, дождётся данных неблокирующим способом и сериализует в JSON. Никаких `block()` и `subscribe()` в коде контроллера быть не должно.

## Функциональная модель: RouterFunction

Кроме аннотаций, WebFlux предлагает **функциональный** стиль: маршруты описываются кодом, а не аннотациями. Это даёт явный, тестируемый роутинг и нравится тем, кто любит «всё под контролем».

```java
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRoutes {

    @Bean
    public RouterFunction<ServerResponse> routes(UserHandler handler) {
        return route(GET("/api/users/{id}"), handler::getUser)   // ← маршрут → обработчик
            .andRoute(GET("/api/users"), handler::getAll)
            .andRoute(POST("/api/users"), handler::create);
    }
}

@Component
public class UserHandler {                         // HandlerFunction — обработчики запросов
    private final UserService service;

    public UserHandler(UserService service) { this.service = service; }

    public Mono<ServerResponse> getUser(ServerRequest req) {
        Long id = Long.valueOf(req.pathVariable("id"));
        return service.findById(id)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))   // нашли → 200 + тело
            .switchIfEmpty(ServerResponse.notFound().build());      // пусто → 404
    }
}
```

- `RouterFunction` — таблица маршрутов (предикат запроса → обработчик).
- `HandlerFunction` — функция `ServerRequest → Mono<ServerResponse>`.

✅ Аннотационная модель — выбор по умолчанию (привычно, меньше кода). Функциональную берут, когда нужен максимально явный роутинг или лёгкие микросервисы без «магии» аннотаций.

## Streaming-ответ (text/event-stream)

Где WebFlux раскрывается особенно ярко — это **стриминг**: отдавать клиенту элементы по мере их появления, не накапливая весь список в памяти. Самый частый формат — **Server-Sent Events (SSE)**.

```java
@GetMapping(value = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<PriceTick> streamPrices() {
    return Flux.interval(Duration.ofSeconds(1))    // тик каждую секунду, бесконечно
        .map(i -> priceService.currentPrice("BTC"));  // и сразу отдаём клиенту
}
```

Указав `produces = TEXT_EVENT_STREAM_VALUE`, мы говорим Spring: «не жди завершения `Flux`, шли каждый элемент отдельным событием». Браузер получит непрерывный поток котировок. ✅ Так делают живые курсы валют, ленты уведомлений, прогресс длинных операций — без поллинга и без WebSocket.

## ASCII-схема: event loop на Netty

Чтобы понять, почему горстка потоков справляется с тысячами соединений, посмотрим на event loop Netty.

```text
                    ┌──────────────────────────────────────────┐
   тысячи           │            EVENT LOOP (Netty)            │
   соединений       │   небольшой пул потоков (~ по числу ядер) │
   ───────────────▶ │                                          │
                    │  ┌────────────────────────────────────┐  │
   req #1 ─────────▶│  │  очередь готовых событий            │  │
   req #2 ─────────▶│  │  • req#1: данные из БД пришли  ──┐  │  │
   req #3 ─────────▶│  │  • req#5: тело запроса дочитано │  │  │
        ...         │  └─────────────────────────────────┼──┘  │
                    │   поток берёт готовое событие  ◀────┘     │
                    │   обрабатывает и СРАЗУ берёт следующее    │
                    └──────────────────┬───────────────────────┘
                                       │ ожидание I/O НЕ блокирует поток:
                                       ▼ запрос ушёл в сеть/БД, поток свободен
                          (когда ответ придёт — снова попадёт в очередь событий)
```

Поток никогда не «висит» в ожидании сети — он только обрабатывает уже *готовые* события и тут же берёт следующее. Пока ответ от БД в пути, поток занят другими запросами. Отсюда и экономия: 4–8 потоков вместо сотен.

## ⚠️ Нельзя смешивать блокирующий код в реактивной цепочке

Это самая опасная и частая ошибка в WebFlux. Если в обработчик, выполняемый на event loop, попадёт **блокирующий** вызов — он остановит поток, который должен был обслуживать тысячи других соединений. Один такой вызов способен «подвесить» весь сервер.

```java
@GetMapping("/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    User user = jdbcRepository.findById(id);   // ❌ блокирующий JDBC прямо в event loop —
                                                //    КАТАСТРОФА под нагрузкой
    return Mono.just(user);
}
```

Что считается блокирующим и недопустимым на event loop:
- ❌ классический **JDBC** / JPA (берите R2DBC — следующая статья);
- ❌ `RestTemplate`, `Thread.sleep`, блокирующее чтение файла;
- ❌ любой `.block()` на `Mono`/`Flux`.

✅ Если блокирующий вызов *неизбежен* (легаси-библиотека без реактивного аналога), его нужно изолировать на отдельный пул, чтобы не трогать event loop:

```java
return Mono.fromCallable(() -> legacyBlockingCall())   // оборачиваем блокирующий вызов
    .subscribeOn(Schedulers.boundedElastic());          // ← и уносим на спец-пул для блокировок
```

## Заключение

**Что изучено:**
- WebFlux — реактивная альтернатива MVC: Netty + event loop + неблокирующий I/O вместо Servlet/Tomcat.
- Контроллеры возвращают `Mono`/`Flux`; подписку делает фреймворк, `block()`/`subscribe()` в них не нужны.
- Две модели: аннотационная (как в MVC) и функциональная (`RouterFunction`/`HandlerFunction`).
- `text/event-stream` отдаёт данные потоком по мере готовности.
- Event loop удерживает тысячи соединений горсткой потоков — но только если в цепочке нет блокировок.

**Как применять на практике:**
- По умолчанию пишите аннотационные контроллеры с `Mono`/`Flux`; функциональный стиль — по вкусу/для микросервисов.
- Для живых лент и прогресса используйте SSE (`Flux` + `TEXT_EVENT_STREAM_VALUE`).
- Безжалостно гоните блокирующий код из event loop; неизбежные блокировки выносите на `boundedElastic()`.

**Что дальше:** разберёмся, откуда контроллеры берут данные неблокирующим способом — реактивные репозитории (R2DBC) и клиент `WebClient`.

---

Полезные ссылки:
- [Spring WebFlux — Reference](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Spring WebFlux — функциональные эндпоинты](https://docs.spring.io/spring-framework/reference/web/webflux-functional.html)
- [Baeldung — Spring WebFlux](https://www.baeldung.com/spring-webflux)

[⬅ Reactor: Mono, Flux и операторы](02-reactor-mono-flux.md) · [📑 Оглавление модуля](README.md) · [Реактивные репозитории и клиенты ➡](04-reactive-repositories-clients.md)
