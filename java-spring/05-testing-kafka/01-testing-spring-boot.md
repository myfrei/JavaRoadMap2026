# Лекция: Тестирование в Spring Boot

> Java Spring · Модуль 5 — Тестирование и Kafka · [⬅ К содержанию трека](../README.md)

## Введение

Представьте бригаду, которая строит дом. Каждый кирпич проверяют отдельно — не трескается ли (это **unit-тест**). Потом смотрят, держит ли стена, сложенная из кирпичей (это **integration-тест**). И только в конце проверяют, что в готовом доме открываются двери, течёт вода и горит свет (это **end-to-end**). Никто не разбирает весь дом по кирпичику каждый раз, когда нужно проверить одну розетку — это слишком долго и дорого.

В Spring Boot ровно та же логика. Можно поднять весь контекст приложения для каждого теста — но тогда тесты будут ползти как улитка. А можно поднимать ровно тот «слой», который вы проверяете. Давайте разберём, как выбирать правильный инструмент под задачу.

## Пирамида тестов

Классическая метафора — **пирамида тестов**. Внизу много быстрых дешёвых тестов, наверху мало медленных и дорогих.

```
          ╱╲
         ╱e2e╲          ← мало: весь стек, реальная БД/брокер, медленно
        ╱──────╲
       ╱integr.  ╲      ← средне: связка компонентов, слайсы, Testcontainers
      ╱────────────╲
     ╱    unit       ╲  ← много: один класс, моки, миллисекунды
    ╱──────────────────╲
```

| Уровень | Что проверяет | Скорость | Spring-контекст |
|---------|---------------|----------|-----------------|
| **unit** | один класс/метод в изоляции | мс | не нужен (чистый JUnit + Mockito) |
| **integration** | связку слоёв (контроллер↔сервис↔БД) | сотни мс | слайс или часть контекста |
| **e2e** | приложение целиком как чёрный ящик | секунды | полный контекст + реальные зависимости |

Правило: **80% тестов — unit, остальное — выше**. Чем выше по пирамиде, тем дороже поддержка и тем медленнее обратная связь.

## Unit-тест: чистый JUnit и Mockito

Unit-тест не знает про Spring вообще. Вы создаёте объект руками, а его зависимости подменяете моками. Возьмём типичный сервис заказов:

```java
class OrderServiceTest {

    private final OrderRepository repo = mock(OrderRepository.class);   // ← мок зависимости
    private final OrderService service = new OrderService(repo);        // ← конструкторная инъекция вручную

    @Test
    void cancelsExistingOrder() {
        Order order = new Order(1L, OrderStatus.NEW);
        when(repo.findById(1L)).thenReturn(Optional.of(order));         // ← задаём поведение мока

        service.cancel(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(repo).save(order);                                       // ← проверяем взаимодействие
    }

    @Test
    void failsWhenOrderNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(99L))                   // ← проверяем исключение
            .isInstanceOf(OrderNotFoundException.class);
    }
}
```

✅ Такой тест запускается за миллисекунды: ни базы, ни сети, ни контекста. Именно конструкторная инъекция (`new OrderService(repo)`) делает класс легко тестируемым — зависимости видны и подменяемы.

## @SpringBootTest: полный контекст

Когда нужно проверить связку компонентов «как в бою», поднимают весь контекст аннотацией `@SpringBootTest`. Она загружает все бины приложения.

```java
@SpringBootTest                                  // ← поднимает ВЕСЬ контекст приложения
class OrderFlowIntegrationTest {

    @Autowired
    private OrderService orderService;            // ← настоящий бин из контекста

    @Test
    void createsAndReadsOrder() {
        Long id = orderService.create(new OrderRequest("BOOK-1", 2)).getId();

        Order found = orderService.findById(id);
        assertThat(found.getItem()).isEqualTo("BOOK-1");
    }
}
```

⚠️ `@SpringBootTest` — самый тяжёлый инструмент. Контекст со всеми бинами, авто-конфигурациями и подключениями поднимается **секунды**. Если так писать каждый тест, прогон на сотню тестов растянется на минуты. Полный контекст оправдан для немногих сквозных сценариев — не для проверки одного контроллера.

## Слайсы: поднимаем только нужный слой

Чтобы не платить за весь контекст, Spring Boot даёт **слайс-аннотации** (test slices). Каждая поднимает ровно один слой и его инфраструктуру.

| Аннотация | Что поднимает | Для чего |
|-----------|---------------|----------|
| `@WebMvcTest` | MVC-слой: контроллеры, фильтры, `MockMvc` | тесты REST-контроллеров |
| `@DataJpaTest` | JPA-слой: репозитории + embedded-БД, транзакция с откатом | тесты репозиториев и запросов |
| `@JsonTest` | Jackson/Gson-сериализация | проверка JSON-маппинга DTO |
| `@WebFluxTest` | реактивный веб-слой + `WebTestClient` | тесты WebFlux-контроллеров |
| `@RestClientTest` | `RestTemplate`/`RestClient` + мок-сервер | тесты исходящих HTTP-вызовов |

Слайс не грузит сервисы и репозитории, которых нет в его слое — поэтому стартует в разы быстрее `@SpringBootTest`.

## MockMvc: тест контроллера без сервера

`@WebMvcTest` поднимает только веб-слой и даёт `MockMvc` — способ дёргать контроллеры **без реального HTTP-сервера** (запросы идут через диспетчер напрямую). Сервисы при этом подменяются моками:

```java
@WebMvcTest(OrderController.class)               // ← поднимаем ТОЛЬКО этот контроллер
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;                      // ← клиент для вызова контроллера

    @MockitoBean                                  // ← Spring Boot 3.4+: мок-бин в контексте
    private OrderService orderService;            // (раньше — @MockBean, теперь deprecated)

    @Test
    void returnsOrderAsJson() throws Exception {
        when(orderService.findById(1L))
            .thenReturn(new Order(1L, "BOOK-1", OrderStatus.NEW));

        mockMvc.perform(get("/api/orders/1"))                       // ← имитируем GET-запрос
            .andExpect(status().isOk())                             // ← проверяем HTTP 200
            .andExpect(jsonPath("$.item").value("BOOK-1"))         // ← проверяем поле JSON
            .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void returns404WhenMissing() throws Exception {
        when(orderService.findById(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(get("/api/orders/99"))
            .andExpect(status().isNotFound());                      // ← проверяем обработку ошибки
    }
}
```

Два слова про `@MockBean` и `@MockitoBean`. И то, и другое кладёт **мок в контекст Spring** (в отличие от обычного `mock()`, который живёт только в тесте). Начиная со Spring Boot 3.4 классический `@MockBean` объявлен deprecated, а на замену пришёл `@MockitoBean` из ядра Spring — в новом коде используйте именно его.

## Testcontainers: реальная зависимость в Docker

Embedded-база (H2) удобна, но ведёт себя не как настоящий PostgreSQL: другой SQL-диалект, другие типы, другие баги. Для честных интеграционных тестов поднимают **реальную** зависимость в Docker через [Testcontainers](https://testcontainers.com/) — библиотека стартует контейнер на время теста и гасит его после.

```java
@SpringBootTest
@Testcontainers
class OrderRepositoryIT {

    @Container                                                      // ← управляемый контейнер
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource                                         // ← пробрасываем URL контейнера в Spring
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired OrderRepository repository;

    @Test
    void persistsAgainstRealPostgres() {
        repository.save(new Order("BOOK-1", OrderStatus.NEW));
        assertThat(repository.findByStatus(OrderStatus.NEW)).hasSize(1);
    }
}
```

✅ Тест бьёт по настоящему PostgreSQL — те же типы и диалект, что в проде. Цена — нужен Docker и старт контейнера занимает время, поэтому Testcontainers держат для немногих интеграционных тестов, а не для каждого. Этот же подход мы применим к Kafka в [статье 6](06-testing-kafka.md).

## Заключение

**Что изучено:**
- Пирамида тестов: много быстрых unit внизу, мало медленных e2e наверху.
- Unit-тест не знает про Spring — это чистый JUnit + Mockito и конструкторная инъекция.
- `@SpringBootTest` поднимает весь контекст и потому медленный — для немногих сквозных сценариев.
- Слайсы (`@WebMvcTest`, `@DataJpaTest`, `@JsonTest`...) грузят один слой и работают быстрее.
- `MockMvc` дёргает контроллеры без сервера; `@MockitoBean` (вместо deprecated `@MockBean`) кладёт мок в контекст.
- Testcontainers поднимает реальную зависимость в Docker для честных интеграционных тестов.

**Как применять на практике:**
- Начинайте с unit-тестов — они дают самую быструю обратную связь.
- Для контроллеров берите `@WebMvcTest` + `MockMvc`, для репозиториев — `@DataJpaTest`.
- `@SpringBootTest` оставляйте только для проверки сквозных сценариев.
- Где H2 врёт про прод — переходите на Testcontainers с реальной БД.

**Что дальше:** прежде чем тестировать Kafka, разберёмся, зачем вообще нужны очереди и стриминг и чем асинхронные сообщения лучше синхронного REST.

[⬅ К треку](../README.md) · [📑 Оглавление модуля](README.md) · [Очереди, стриминг и Event-Driven архитектура ➡](02-event-driven-queues.md)
