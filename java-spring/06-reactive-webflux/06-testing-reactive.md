# Лекция: Тестируем реактивные компоненты

> Java Spring · Модуль 6 — Реактивный стек (WebFlux) · [⬅ К содержанию трека](../README.md)

## Введение

Мы прошли весь реактивный стек: Reactor, WebFlux, R2DBC, backpressure. Осталось последнее, без чего код нельзя выпускать в прод, — **тесты**. И тут новичка ждёт сюрприз: привычный `assertEquals` на реактивных типах не работает так, как кажется.

Представьте, что вы проверяете не готовое блюдо, а *рецепт*: написать «рецепт правильный» нельзя — нужно встать у плиты, включить конвейер и убедиться, что на выходе появляется именно то, что обещано, в правильном порядке и без ошибок. `Mono` и `Flux` — это рецепты (помните: без подписки ничего не происходит). Поэтому для них есть специальные инструменты: `StepVerifier` для самих потоков и `WebTestClient` для WebFlux-эндпоинтов. Давайте разберёмся, как ими пользоваться, и почему забытый `.verify()` — это тест, который ничего не проверяет.

## Почему нельзя просто assertEquals на Mono/Flux

Возникает соблазн написать так:

```java
Mono<String> result = service.greet("Мир");
assertEquals("Привет, Мир", result);   // ❌ сравниваем Mono со строкой — всегда мимо
```

❌ Это бессмысленно: слева `Mono<String>` (рецепт), справа `String` (готовое значение) — они никогда не равны. Хуже того — `Mono` ещё и **ленивый**: пока на него не подписались, код внутри даже не выполнялся. Чтобы проверить *содержимое* потока, его нужно запустить (подписаться) и убедиться, какие сигналы он испустит: `onNext` (значения), `onComplete` (успешное завершение) или `onError` (ошибка). Ровно для этого и создан `StepVerifier`.

## StepVerifier: проверяем сигналы потока

`StepVerifier` (из `reactor-test`) описывает *ожидаемый сценарий* сигналов и сам подписывается на поток. Читается как «ожидаю такой-то элемент, потом такой-то, потом завершение».

Зависимость (Gradle, обычно уже есть в `spring-boot-starter-webflux`):

```kotlin
testImplementation("io.projectreactor:reactor-test")
```

Тест для `Flux`:

```java
import reactor.test.StepVerifier;

@Test
void fluxEmitsThreeNumbers() {
    Flux<Integer> flux = Flux.just(1, 2, 3).map(n -> n * 10);

    StepVerifier.create(flux)        // ← подписаться на поток и начать проверку
        .expectNext(10)              // ждём первый onNext = 10
        .expectNext(20, 30)          // затем 20 и 30 (можно перечислять)
        .expectComplete()            // ждём сигнал onComplete
        .verify();                   // ⚠️ ЗАПУСТИТЬ проверку — без этого тест пустой!
}
```

Проверка ошибочного сценария — отдельный важный кейс:

```java
@Test
void monoFailsWhenUserMissing() {
    Mono<User> mono = service.findById(404L);   // пользователя нет

    StepVerifier.create(mono)
        .expectError(UserNotFoundException.class)   // ждём именно эту ошибку
        .verify();
}
```

Полезные шаги `StepVerifier`:
- `expectNext(...)` — следующий элемент(ы);
- `expectComplete()` — успешное завершение;
- `expectError(Class)` — завершение ошибкой нужного типа;
- `expectNextCount(n)` — ровно `n` элементов (когда значения не важны);
- `assertNext(consumer)` — проверить элемент произвольными ассертами.

## ⚠️ Не забывайте verify()

Это **главная ловушка** тестирования Reactor. `StepVerifier.create(...).expectNext(...)` лишь *описывает* сценарий, но **не запускает** его. Без финального `.verify()` подписки не произойдёт, поток не выполнится, и тест пройдёт зелёным — *ничего не проверив*.

```java
StepVerifier.create(flux)
    .expectNext(10, 20, 30)
    .expectComplete();          // ❌ забыли .verify() — тест ЗЕЛЁНЫЙ, но фиктивный!
```

✅ Всегда завершайте цепочку терминальным шагом: `.verify()`, либо его варианты `.verifyComplete()` / `.verifyError(...)` (они объединяют `expect*` и `verify` в один вызов):

```java
StepVerifier.create(flux)
    .expectNext(10, 20, 30)
    .verifyComplete();          // ✅ = expectComplete() + verify()
```

Запомните намертво: **нет `verify()` (или `verify*`) — нет теста.**

## Тестируем время: withVirtualTime

Как проверить поток с задержкой, не заставляя тест реально ждать? Например, `Flux.interval(Duration.ofHours(1))` — не ждать же час. Reactor умеет **виртуализировать время**: `StepVerifier.withVirtualTime` подменяет планировщик и «перематывает» время мгновенно.

```java
@Test
void emitsAfterOneHourVirtually() {
    StepVerifier.withVirtualTime(() ->                 // ← фабрика потока (важно: лямбда!)
            Flux.interval(Duration.ofHours(1)).take(2))
        .expectSubscription()
        .expectNoEvent(Duration.ofHours(1))            // за первый час — ничего
        .expectNext(0L)                                // через час — первый элемент
        .thenAwait(Duration.ofHours(1))                // «перемотать» ещё час
        .expectNext(1L)                                // второй элемент
        .verifyComplete();                             // тест отработает за миллисекунды
}
```

⚠️ Поток обязательно создаётся **внутри лямбды** в `withVirtualTime(() -> ...)` — иначе он успеет подписаться на реальный планировщик до подмены, и виртуальное время не сработает. `thenAwait` перематывает время вперёд, `expectNoEvent` проверяет, что в заданный интервал ничего не пришло.

## WebTestClient: тестируем WebFlux-эндпоинты

Для проверки реактивных контроллеров целиком есть **`WebTestClient`** — реактивный аналог `MockMvc`. Он шлёт запросы к приложению (без реального сетевого порта) и удобно проверяет статус и тело ответа.

```java
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(UserController.class)               // ← поднять только web-слой контроллера
class UserControllerTest {

    @Autowired
    WebTestClient client;                        // готовый клиент к контроллеру

    @MockitoBean
    UserService service;                         // зависимость — мок

    @Test
    void returnsUserById() {
        when(service.findById(eq(1L)))
            .thenReturn(Mono.just(new User(1L, "Анна", "anna@example.com")));

        client.get().uri("/api/users/1")
            .exchange()                          // выполнить запрос
            .expectStatus().isOk()               // проверить HTTP 200
            .expectBody()
            .jsonPath("$.name").isEqualTo("Анна")        // проверить поля JSON
            .jsonPath("$.email").isEqualTo("anna@example.com");
    }

    @Test
    void returnsListOfUsers() {
        when(service.findAll()).thenReturn(
            Flux.just(new User(1L, "Анна", "a@e.com"),
                      new User(2L, "Иван", "i@e.com")));

        client.get().uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)          // тело как список
            .hasSize(2);                         // ровно два пользователя
    }
}
```

- `@WebFluxTest(...)` поднимает только указанный контроллер и web-инфраструктуру (быстро, без полного контекста).
- `@MockitoBean` подменяет зависимости моками (в свежих Spring Boot 3.x вместо устаревшего `@MockBean`).
- `.exchange()` выполняет запрос, дальше — fluent-проверки статуса и тела.

✅ Для интеграционного теста всего приложения берут `@SpringBootTest(webEnvironment = RANDOM_PORT)` и инъецируют тот же `WebTestClient` — проверки выглядят идентично.

## Заключение

**Что изучено:**
- `Mono`/`Flux` — ленивые рецепты; `assertEquals` на них бессмыслен, нужно проверять *сигналы* (`onNext`/`onComplete`/`onError`).
- `StepVerifier` описывает ожидаемый сценарий и подписывается на поток; ключевые шаги — `expectNext`/`expectComplete`/`expectError`.
- `StepVerifier.withVirtualTime` тестирует задержки мгновенно, без реального ожидания.
- `WebTestClient` (+ `@WebFluxTest`, `@MockitoBean`) проверяет реактивные эндпоинты: статус и тело.

**Как применять на практике:**
- Каждую цепочку `StepVerifier` завершайте `verify()` / `verifyComplete()` / `verifyError()` — иначе тест фиктивный.
- Потоки с `delay`/`interval` гоняйте через `withVirtualTime`, создавая поток внутри лямбды.
- Контроллеры тестируйте `@WebFluxTest` + `WebTestClient` с моками сервисов; полный стек — через `@SpringBootTest` с `RANDOM_PORT`.

**Что дальше:** это финальный модуль реактивного трека. Возвращайтесь к [оглавлению модуля](README.md), чтобы пройтись по чек-листам «как применять на практике» в каждой статье, и дальше — к [треку Java Spring](../README.md).

---

Полезные ссылки:
- [Project Reactor — Testing (`StepVerifier`)](https://projectreactor.io/docs/core/release/reference/#testing)
- [Spring — `WebTestClient` (Reference)](https://docs.spring.io/spring-framework/reference/testing/webtestclient.html)
- [Baeldung — Testing Reactive Streams With StepVerifier](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher)

[⬅ Backpressure и управление потоком](05-backpressure.md) · [📑 Оглавление модуля](README.md)
