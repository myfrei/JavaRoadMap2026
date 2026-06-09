# Лекция: KafkaTemplate — отправка сообщений

> Java Spring · Модуль 5 — Тестирование и Kafka · [⬅ К содержанию трека](../README.md)

## Введение

Помните, как в Spring отправляют HTTP-запросы через `RestTemplate`, а письма — через `JmsTemplate`? Spring любит паттерн «Template»: класс-помощник, который прячет всю низкоуровневую возню и даёт пару удобных методов. Для Kafka такой помощник — **`KafkaTemplate`**.

В прошлой статье мы разобрали, что такое topic, partition и ключ. Теперь применим это на практике: подключим `spring-kafka`, настроим producer и научимся отправлять доменные события. Давайте по порядку — от зависимости до боевой отправки с обработкой ошибок.

## Подключаем spring-kafka

Стартовый модуль `spring-kafka` подтягивает клиент Kafka и автоконфигурацию Spring Boot. В Gradle:

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.springframework.kafka:spring-kafka")   // ← версию даст Spring Boot BOM
}
```

Дальше — конфигурация producer'а в `application.yml`. Минимум, который нужен: адрес кластера и сериализаторы ключа и значения (Kafka хранит **байты**, поэтому объект надо во что-то превратить).

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092          # ← адрес(а) брокеров кластера
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer  # ← объект → JSON-байты
      acks: all                                # ← про это ниже
```

✅ `StringSerializer` для ключа (у нас ключ — строка-id) и `JsonSerializer` для значения (наше доменное событие сериализуется в JSON). На этом Spring Boot сам создаст бин `KafkaTemplate`, и его можно инжектить.

## Доменное событие и его отправка

Договоримся об объекте, который шлём. Пусть это событие «заказ оформлен» — обычный Java `record`:

```java
public record OrderPlacedEvent(
        String orderId,
        String item,
        int quantity) { }
```

Теперь сервис, который публикует это событие. Инжектим `KafkaTemplate` **через конструктор** (как мы условились во всём треке) и зовём `send`:

```java
@Service
public class OrderEventPublisher {

    private static final String TOPIC = "orders";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;          // ← конструкторная инъекция
    }

    public void publish(OrderPlacedEvent event) {
        // ключ = orderId → все события одного заказа в одну партицию (порядок!)
        kafkaTemplate.send(TOPIC, event.orderId(), event);   // ← topic, key, value
    }
}
```

Обратите внимание на `event.orderId()` вторым аргументом — это **ключ**. Из прошлой статьи помним: одинаковый ключ → одна партиция → сохранённый порядок событий заказа. Если бы порядок был не важен, можно было бы вызвать `send(TOPIC, event)` без ключа.

## send() возвращает future, а не void

Вот первое, что важно понять про `send`: он **не блокирующий** и возвращает не `void`, а `CompletableFuture` с результатом. Сообщение уходит в фоне, а вы получаете «обещание» результата.

```java
CompletableFuture<SendResult<String, OrderPlacedEvent>> future =
        kafkaTemplate.send(TOPIC, event.orderId(), event);   // ← вернулся СРАЗУ, до подтверждения брокером
```

Дальше у вас две стратегии — синхронная и асинхронная.

**Синхронно** — заблокироваться и дождаться результата через `.get()`:

```java
try {
    SendResult<String, OrderPlacedEvent> result =
            kafkaTemplate.send(TOPIC, event.orderId(), event).get();   // ← БЛОКИРУЕМСЯ до ответа брокера
    RecordMetadata meta = result.getRecordMetadata();
    log.info("Отправлено в partition={} offset={}", meta.partition(), meta.offset());
} catch (InterruptedException | ExecutionException e) {
    Thread.currentThread().interrupt();
    throw new EventPublishException("Не удалось отправить событие заказа " + event.orderId(), e);
}
```

`.get()` дожидается подтверждения и **бросит исключение**, если отправка провалилась. Просто, надёжно, но медленно — поток стоит. Берите синхронный вариант, когда без подтверждения нельзя идти дальше.

## Асинхронная отправка с callback

Чаще лучше **не блокировать поток**, а навесить callback на future — он отработает, когда придёт результат:

```java
public void publish(OrderPlacedEvent event) {
    kafkaTemplate.send(TOPIC, event.orderId(), event)
        .whenComplete((result, ex) -> {                        // ← вызовется по готовности, в фоне
            if (ex != null) {
                // ❌ отправка провалилась — НЕ молчим: лог + метрика + (опц.) ретрай/outbox
                log.error("Ошибка отправки события заказа {}", event.orderId(), ex);
            } else {
                RecordMetadata meta = result.getRecordMetadata();
                log.debug("Событие {} ушло в partition={} offset={}",
                          event.orderId(), meta.partition(), meta.offset());
            }
        });
}
```

| Подход | Когда брать | Цена |
|--------|-------------|------|
| `.get()` (синхронно) | без подтверждения нельзя продолжать | блокирует поток |
| `whenComplete` (callback) | важна пропускная способность | результат обрабатываете позже |
| Проигнорировать future | ❌ почти никогда | теряете ошибки молча |

## Никогда не игнорируйте future

⚠️ Самая частая ошибка новичка — написать `kafkaTemplate.send(...)` и **выбросить future**, не проверив результат:

```java
// ❌ ТАК НЕ НАДО: ошибку отправки вы просто не заметите
kafkaTemplate.send(TOPIC, event.orderId(), event);   // результат потерян
```

Почему это опасно: `send` не блокирующий, и если брокер недоступен, сериализация упала или партиция переполнена — сообщение **не уйдёт**, а вы об этом не узнаете. Для бизнес-событий «потеряли тихо» — худший исход. Поэтому всегда либо `.get()`, либо `whenComplete` с логом и метрикой ошибки. Если нужна гарантия «сообщение точно опубликовано», в проде применяют паттерн **transactional outbox** (пишем событие в ту же БД-транзакцию, отдельный процесс досылает в Kafka).

## acks и идемпотентность producer'а — кратко

Два параметра конфигурации, которые определяют надёжность отправки.

**`acks`** — сколько реплик должны подтвердить запись, прежде чем брокер ответит «ок»:

```yaml
spring:
  kafka:
    producer:
      acks: all   # ← ждать подтверждения от ВСЕХ синхронных реплик (самый надёжный)
```

- `acks=0` — не ждём подтверждения вообще (быстро, можно потерять сообщение);
- `acks=1` — ждём только leader-партицию (компромисс);
- `acks=all` — ждём все синхронные реплики (надёжно, чуть медленнее) — для важных событий берите его.

**Идемпотентность producer'а** защищает от **дублей при ретраях**: без неё повторная отправка после сетевого сбоя может записать сообщение дважды.

```yaml
spring:
  kafka:
    producer:
      acks: all
      properties:
        enable.idempotence: true   # ← producer не создаст дубль при внутреннем ретрае
```

✅ С `enable.idempotence=true` Kafka присваивает сообщениям номера и отсекает повторы на стороне брокера. Это про дубли **отправки**; про дубли **обработки** на стороне consumer'а (at-least-once) поговорим в следующей статье. Глубже тема разобрана в [материале по гарантиям доставки](../../modules/m21-kafka/theory/delivery-guarantees-and-idempotency.md).

## Заключение

**Что изучено:**
- `KafkaTemplate` — Spring-помощник для отправки; подключается через стартер `spring-kafka`.
- Producer настраивается в `application.yml`: `bootstrap-servers` + сериализаторы ключа/значения (`JsonSerializer` для объектов).
- Ключ в `send(topic, key, value)` управляет партицией и порядком (ключ = id сущности).
- `send` возвращает `CompletableFuture`: `.get()` — синхронно, `whenComplete` — асинхронно с callback.
- Игнорировать future нельзя — тихо потеряете ошибки отправки.
- `acks=all` и `enable.idempotence=true` дают надёжную отправку без дублей на ретраях.

**Как применять на практике:**
- Конструкторная инъекция `KafkaTemplate`, отправка через выделенный publisher-сервис.
- Нужен порядок — кладите id сущности ключом; не нужен — шлите без ключа ради равномерности.
- Всегда обрабатывайте результат `send` (лог + метрика на ошибке); для бизнес-событий — `acks=all`.

**Что дальше:** мы научились отправлять. Теперь — принимающая сторона: `@KafkaListener`, группы, коммит offset и идемпотентная обработка.

[⬅ Apache Kafka: основы для разработчика](03-kafka-basics.md) · [📑 Оглавление модуля](README.md) · [@KafkaListener: приём сообщений ➡](05-kafka-listener.md)
