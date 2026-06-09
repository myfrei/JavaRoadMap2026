# Лекция: Тестирование интеграций с Kafka

> Java Spring · Модуль 5 — Тестирование и Kafka · [⬅ К содержанию трека](../README.md)

## Введение

Мы прошли полный круг: в [статье 4](04-kafka-template.md) научились отправлять события через `KafkaTemplate`, в [статье 5](05-kafka-listener.md) — принимать их `@KafkaListener`'ом. Осталось главное — **доказать, что эта связка работает**. Иначе вы узнаете о проблеме не на тесте, а в проде, в три часа ночи.

Вернёмся к аналогии из первой статьи: проверять Kafka-интеграцию моками — это как проверять водопровод, не пуская воду. Сериализация, ключи, партиции, десериализация на той стороне — всё это живёт **между** producer и consumer, и мок их не проверит. Давайте разберём, как поднять настоящую Kafka в тесте и дождаться сообщения без флакающих `Thread.sleep`.

## Почему нельзя мокать всё

В unit-тесте мы спокойно подменяем `KafkaTemplate` моком и проверяем, что `publish()` его дёрнул. Это правильно — для бизнес-логики сервиса. Но такой тест **ничего не говорит** про саму интеграцию:

```java
// Unit-уровень: проверяем ТОЛЬКО что publisher вызвал send — и всё
verify(kafkaTemplate).send("orders", "order-1", event);   // ← но дошло ли? десериализовалось ли?
```

❌ Чего мок **не** проверит:
- сериализуется ли событие и десериализуется ли обратно в нужный тип;
- правильно ли настроены сериализаторы и `trusted.packages`;
- сматчился ли topic, сработал ли `@KafkaListener`, выбралась ли партиция по ключу.

Всё это — поведение **реального** брокера. Поэтому связку «отправили → приняли» проверяют **интеграционным** тестом с настоящей (или почти настоящей) Kafka. Вопрос только — какой Kafka.

## Два способа поднять Kafka в тесте

Есть два рабочих варианта, и выбор между ними — это классический компромисс «быстро» против «как в проде».

**1. `@EmbeddedKafka`** (из `spring-kafka-test`) — поднимает Kafka-брокер **в той же JVM**, что и тест. Никакого Docker, стартует быстро.

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("org.springframework.kafka:spring-kafka-test")  // ← @EmbeddedKafka
}
```

**2. Testcontainers Kafka** — поднимает **реальный** Kafka в Docker-контейнере (тот же подход, что для PostgreSQL в [статье 1](01-testing-spring-boot.md)).

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:junit-jupiter")
}
```

| Критерий | `@EmbeddedKafka` | Testcontainers Kafka |
|----------|------------------|----------------------|
| **Скорость** | быстрее (в той же JVM, без Docker) | медленнее (старт контейнера) |
| **Реалистичность** | embedded-брокер, мелкие отличия от прода | настоящий Kafka — как в проде |
| **Требует Docker** | ❌ нет | ✅ да |
| **Изоляция** | в рамках JVM теста | полная (отдельный контейнер) |
| **Когда брать** | быстрые интеграционные тесты, CI без Docker | максимальная достоверность, сложные сценарии |

Практический выбор: для большинства тестов хватает `@EmbeddedKafka` (быстро и без Docker). Если важна стопроцентная достоверность или вы уже используете Testcontainers для БД — берите Testcontainers Kafka ради единообразия.

## Тест связки producer → listener через Awaitility

Соберём главный тест модуля: отправляем событие через `KafkaTemplate` и **дожидаемся**, что наш `@KafkaListener` его обработал. Поскольку обработка асинхронная, нельзя просто проверить результат сразу после `send` — сообщение ещё в пути.

Здесь нужен инструмент ожидания. ❌ **Не** `Thread.sleep(2000)` — это и медленно (всегда ждёт максимум), и флаки (иногда не хватит). ✅ Правильно — [Awaitility](https://github.com/awaitility/awaitility): он **опрашивает условие** и проходит ровно тогда, когда оно выполнилось.

```kotlin
// build.gradle.kts
testImplementation("org.awaitility:awaitility")
```

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "orders")    // ← поднимаем embedded-брокер с топиком
class OrderEventFlowIT {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    private OrderProcessedStore store;     // ← куда листенер складывает обработанные id (тестовый бин)

    @Test
    void publishedEventIsConsumedAndProcessed() {
        OrderPlacedEvent event = new OrderPlacedEvent("order-1", "BOOK-1", 2);

        kafkaTemplate.send("orders", event.orderId(), event);   // 1) отправили

        await()                                                  // 2) ждём, пока листенер обработает
            .atMost(Duration.ofSeconds(10))                      // ← верхняя граница ожидания
            .pollInterval(Duration.ofMillis(200))                // ← как часто проверять условие
            .untilAsserted(() ->                                 // ← повторяет проверку, пока не пройдёт
                assertThat(store.contains("order-1")).isTrue()   // ← листенер реально отработал
            );
    }
}
```

✅ Тест проходит весь путь: сериализация → запись в топик → вычитка → десериализация → `@KafkaListener` → бизнес-логика. Это и есть проверка интеграции целиком, а не одного `verify`.

## Тестовый consumer: проверяем само сообщение

Иногда нужно проверить не побочный эффект листенера, а **что именно** легло в топик (правильный ключ, правильный JSON). Тогда в тесте поднимают **свой consumer** и читают сообщение напрямую. `spring-kafka-test` даёт для этого утилиты `KafkaTestUtils`:

```java
@Test
void messageHasCorrectKeyAndPayload() {
    // тестовый consumer на свежей группе, читаем topic "orders"
    Map<String, Object> props = KafkaTestUtils.consumerProps(
            "test-group", "true", embeddedKafka);          // ← конфиг тест-консьюмера
    var cf = new DefaultKafkaConsumerFactory<String, OrderPlacedEvent>(
            props, new StringDeserializer(), new JsonDeserializer<>(OrderPlacedEvent.class, false));

    try (Consumer<String, OrderPlacedEvent> consumer = cf.createConsumer()) {
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "orders");

        kafkaTemplate.send("orders", "order-1", new OrderPlacedEvent("order-1", "BOOK-1", 2));

        // ждём ровно одну запись (с таймаутом — не зависаем навсегда):
        ConsumerRecord<String, OrderPlacedEvent> record =
                KafkaTestUtils.getSingleRecord(consumer, "orders", Duration.ofSeconds(5));

        assertThat(record.key()).isEqualTo("order-1");        // ← ключ = id заказа (партиционирование)
        assertThat(record.value().item()).isEqualTo("BOOK-1"); // ← payload сериализовался верно
    }
}
```

Такой тест проверяет «контракт» сообщения — он полезен, когда ваш топик читают **другие** команды и важно зафиксировать формат и ключ.

## Флаки-тесты: главная боль и как её снять

Тесты с Kafka печально известны нестабильностью («сегодня зелёный, завтра красный»). Почти все причины — из-за асинхронности и общего состояния. Разберём, как их убрать.

⚠️ **Источник 1 — `Thread.sleep` вместо ожидания условия.** Фиксированная пауза либо тратит время впустую, либо изредка не дожидается. Лечение — Awaitility с `atMost` + `untilAsserted` (как выше).

⚠️ **Источник 2 — слишком жёсткие таймауты.** На медленном CI старт брокера и rebalancing идут дольше, чем на вашей машине. Давайте запас: `atMost(10s)`, а не `2s`. Любое чтение в тесте — только **с таймаутом** (`getSingleRecord(..., Duration)`), чтобы упавший тест падал быстро, а не висел вечно.

⚠️ **Источник 3 — «грязные» топики между тестами.** Сообщения от предыдущего теста долетают до следующего, и проверки ломаются. Способы изоляции:

```java
@Test
void usesUniqueGroupPerTest() {
    // у каждого теста — своя consumer group → чужие offset'ы не мешают
    Map<String, Object> props = KafkaTestUtils.consumerProps(
            "group-" + UUID.randomUUID(), "true", embeddedKafka);  // ← уникальная группа
    // ...
}
```

- **Уникальная `group.id` на тест** (как выше) — новый consumer не видит чужой прогресс по offset.
- **Свой topic на тест** — `@EmbeddedKafka(topics = "orders-" + ...)` или генерация имени, чтобы тесты не делили один лог.
- **`@DirtiesContext`** — пересоздать контекст (и embedded-брокер) между тестовыми классами, если состояние всё равно протекает (дорого — применять точечно).

✅ Связка «Awaitility + щедрые таймауты + изоляция топиков/групп» убирает 95% флаки. Если тест всё равно мигает — почти всегда вы где-то ждёте фиксированной паузой или делите состояние между тестами.

## Заключение

**Что изучено:**
- Мок `KafkaTemplate` проверяет логику сервиса, но не интеграцию — сериализацию, ключи, десериализацию проверяет только реальный брокер.
- `@EmbeddedKafka` — быстро и без Docker; Testcontainers Kafka — медленнее, зато «как в проде» (см. таблицу-сравнение).
- Связку producer → listener тестируют, отправляя через `KafkaTemplate` и дожидаясь эффекта через Awaitility (`await().untilAsserted`), а не `Thread.sleep`.
- Тестовый consumer (`KafkaTestUtils`) проверяет сам контракт сообщения — ключ и payload.
- Флаки-тесты лечатся ожиданием условия, щедрыми таймаутами и изоляцией топиков/групп между тестами.

**Как применять на практике:**
- Бизнес-логику покрывайте unit-тестами с моками, а саму Kafka-связку — интеграционным тестом с `@EmbeddedKafka`.
- Никогда не ждите сообщение через `sleep` — только Awaitility с верхней границей и опросом.
- Изолируйте тесты: уникальная `group.id` (или topic) на тест; любое чтение — с таймаутом.

**Что дальше:** это финальная статья модуля. Вы прошли путь от пирамиды тестов до проверенной end-to-end интеграции с Kafka — отправка, приём и тест, который это гарантирует. Возвращайтесь к [оглавлению модуля](README.md), чтобы повторить связку 4 → 5 → 6, и дальше — к [реактивному стеку](../06-reactive-webflux/README.md).

[⬅ @KafkaListener: приём сообщений](05-kafka-listener.md) · [📑 Оглавление модуля](README.md)
