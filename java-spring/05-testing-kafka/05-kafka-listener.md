# Лекция: @KafkaListener — приём сообщений

> Java Spring · Модуль 5 — Тестирование и Kafka · [⬅ К содержанию трека](../README.md)

## Введение

В прошлой статье мы отправили событие `OrderPlacedEvent` в topic `orders`. Но отправить — половина дела: сообщение лежит в логе и ждёт, пока кто-то его прочитает и обработает. Представьте почтовый ящик: письмо опустили, но пока вы не подойдёте и не достанете его, ничего не произойдёт.

В Spring «подходит к ящику» аннотация **`@KafkaListener`**: вы помечаете ею метод, и Spring сам подписывается на topic, вычитывает сообщения и вызывает ваш метод на каждое. Давайте разберём, как это включить, как масштабировать приём и, главное, как не обработать одно сообщение дважды.

## Включаем приём: @EnableKafka и @KafkaListener

Сначала включаем инфраструктуру слушателей аннотацией `@EnableKafka` на конфигурации (в Spring Boot со стартером `spring-kafka` она часто включается автоконфигурацией, но указать явно — надёжнее):

```java
@Configuration
@EnableKafka                       // ← включает обработку @KafkaListener
public class KafkaConsumerConfig {
}
```

Дальше — сам слушатель. Помечаем метод `@KafkaListener`, указываем topic и группу. Spring подхватит сообщения и **десериализует** их в наш тип:

```java
@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @KafkaListener(
            topics = "orders",                 // ← какой topic слушаем
            groupId = "order-processor")       // ← consumer group (см. статью 3)
    public void onOrderPlaced(OrderPlacedEvent event) {   // ← Spring сам сделает JSON → объект
        log.info("Получено событие заказа {}: {} x{}",
                 event.orderId(), event.item(), event.quantity());
        // ... бизнес-логика: зарезервировать товар, начислить бонусы и т.п. ...
    }
}
```

✅ Метод вызывается на **каждое** сообщение из топика. Никаких циклов опроса и ручного `poll()` — Spring крутит цикл вычитки за вас, а вы пишете только обработку.

## Конфигурация consumer'а в application.yml

Слушателю нужны те же базовые настройки, что и producer'у, плюс десериализаторы — зеркало того, чем сериализовали:

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-processor
      auto-offset-reset: earliest          # ← новый consumer читает топик с начала
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
    properties:
      spring.json.trusted.packages: "com.javaroadmap.*"   # ← каким пакетам доверять при десериализации
```

⚠️ Про `spring.json.trusted.packages` забывают и ловят ошибку десериализации. Из соображений безопасности `JsonDeserializer` по умолчанию **не доверяет** произвольным классам в заголовке сообщения. Перечислите свои пакеты явно (или `"*"` только для учебных проектов).

## Безопасная десериализация: ErrorHandlingDeserializer

Что будет, если в топик прилетит «битое» сообщение — не тот JSON, чужой формат? Голый `JsonDeserializer` бросит исключение **до** вашего метода, прямо в цикле вычитки. Контейнер попробует прочитать это сообщение снова, снова упадёт — и встанет на нём навсегда. Это называется **poison pill** («ядовитая таблетка»), и она блокирует партицию.

Лечится оборачиванием в `ErrorHandlingDeserializer`: он ловит ошибку десериализации и **не роняет** цикл, а помечает запись как сбойную, чтобы её обработал error handler (например, отправил в DLT — об этом ниже).

```yaml
spring:
  kafka:
    consumer:
      key-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
    properties:
      # настоящие десериализаторы, которые оборачивает ErrorHandlingDeserializer:
      spring.deserializer.key.delegate.class: org.apache.kafka.common.serialization.StringDeserializer
      spring.deserializer.value.delegate.class: org.springframework.kafka.support.serializer.JsonDeserializer
      spring.json.trusted.packages: "com.javaroadmap.*"
```

✅ Теперь «ядовитое» сообщение не парализует партицию — его аккуратно отведут в сторону, а нормальные сообщения продолжат обрабатываться.

## Масштабирование: concurrency и партиции

Один слушатель — один поток. Чтобы обрабатывать партиции **параллельно**, настраивают `ConcurrentKafkaListenerContainerFactory` с параметром `concurrency` — это число потоков-консьюмеров внутри приложения.

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> kafkaListenerContainerFactory(
        ConsumerFactory<String, OrderPlacedEvent> consumerFactory) {

    var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3);    // ← 3 потока-консьюмера: до 3 партиций обрабатываются параллельно
    return factory;
}
```

Здесь вступает в силу правило из [статьи 3](03-kafka-basics.md): **одна партиция → один consumer в группе**. Значит, реальный параллелизм = `min(concurrency, число партиций)`.

```
topic "orders", 3 партиции, concurrency=3:
  partition 0 ──▶ поток-1
  partition 1 ──▶ поток-2     ← 3 потока разобрали 3 партиции, полный параллелизм
  partition 2 ──▶ поток-3

concurrency=5, но партиций всего 3:
  partition 0,1,2 ──▶ поток-1,2,3
  поток-4, поток-5 ──▶ ❌ простаивают (партиций не хватило)
```

⚠️ Задавать `concurrency` больше числа партиций бессмысленно — лишние потоки будут простаивать. Хотите больше параллелизма — увеличивайте число партиций топика.

## Коммит offset: AckMode и ручной Acknowledgment

Прочитать сообщение мало — нужно **закоммитить offset**, чтобы после рестарта не читать его заново. Когда именно коммитить — задаёт `AckMode`.

| AckMode | Когда коммитится offset |
|---------|--------------------------|
| `BATCH` (по умолчанию) | после обработки всей пачки из `poll()` |
| `RECORD` | после каждого сообщения |
| `MANUAL` / `MANUAL_IMMEDIATE` | вы коммитите сами через `Acknowledgment` |

Главное правило (мы выводили его в [материале по гарантиям доставки](../../modules/m21-kafka/theory/delivery-guarantees-and-idempotency.md)): **коммитить offset нужно ПОСЛЕ успешной обработки.** Иначе при падении между «прочитали» и «обработали» сообщение потеряется.

При ручном режиме (`AckMode.MANUAL`) вы получаете объект `Acknowledgment` и сами решаете момент:

```java
@KafkaListener(topics = "orders", groupId = "order-processor")
public void onOrderPlaced(OrderPlacedEvent event, Acknowledgment ack) {  // ← Spring внедрит Acknowledgment
    try {
        process(event);     // 1) сначала ОБРАБОТАЛИ
        ack.acknowledge();  // 2) и только теперь коммитим offset ← порядок важен!
    } catch (Exception e) {
        log.error("Не удалось обработать {} — offset не коммитим, будет повтор", event.orderId(), e);
        // не вызываем ack → сообщение придёт снова (at-least-once)
    }
}
```

Авто-коммит проще, ручной — даёт точный контроль над «точкой невозврата». Для критичных обработчиков берут ручной.

## Идемпотентность: at-least-once требует защиты от дублей

Вот ключевой вывод всей связки producer→listener. Поскольку offset коммитится **после** обработки, при сбое между обработкой и коммитом сообщение придёт **повторно**. Это и есть **at-least-once**: «хотя бы один раз, возможны дубли».

⚠️ Значит, обработчик **обязан быть идемпотентным** — повторная обработка того же события не должна менять результат. Простейший приём — дедупликация по уникальному id:

```java
@KafkaListener(topics = "orders", groupId = "order-processor")
public void onOrderPlaced(OrderPlacedEvent event) {
    if (processedRepository.existsByOrderId(event.orderId())) {   // ← уже обрабатывали?
        log.debug("Дубликат заказа {} — пропускаем", event.orderId());
        return;                                                   // ← повтор не даёт эффекта
    }
    reserveStock(event);
    processedRepository.save(new Processed(event.orderId()));     // ← фиксируем факт обработки (UNIQUE)
}
```

✅ В проде вместо `existsBy...` берут таблицу с UNIQUE-ограничением или Redis с TTL. Принцип один: **дубликат не повторяет эффект**.

## Повторы и Dead Letter Topic — кратко

Что если сообщение раз за разом не обрабатывается (битые данные, баг)? Бесконечно ретраить — значит навсегда застрять на партиции. Цивилизованный выход — несколько попыток, а потом отправить сообщение в **Dead Letter Topic (DLT)** — отдельный топик «карантина», куда складывают то, что обработать не удалось.

В spring-kafka это делает `DeadLetterPublishingRecoverer` вместе с `DefaultErrorHandler`:

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
    var recoverer = new DeadLetterPublishingRecoverer(template);   // ← публикует в topic "orders.DLT"
    // 3 попытки с паузой 1с, потом сообщение уходит в DLT:
    return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
}
```

✅ После 3 неудач сообщение уезжает в `orders.DLT`, партиция освобождается, а вы спокойно разбираете «карантин» отдельно. Глубже про retry и DLQ — в [материале по надёжности Kafka](../../modules/m21-kafka/theory/reliability-retry-dlq.md).

## Заключение

**Что изучено:**
- `@EnableKafka` + `@KafkaListener` подписывают метод на topic; Spring сам вычитывает и десериализует сообщения.
- Consumer настраивается в `application.yml`; не забудьте `spring.json.trusted.packages`.
- `ErrorHandlingDeserializer` спасает от poison pill — битое сообщение не парализует партицию.
- `ConcurrentKafkaListenerContainerFactory.concurrency` даёт параллелизм, ограниченный числом партиций.
- Offset коммитится после обработки; `AckMode.MANUAL` + `Acknowledgment` — для точного контроля.
- At-least-once → обработчик обязан быть идемпотентным (дедуп по id).
- Повторы и `DeadLetterPublishingRecoverer` уводят неисправимые сообщения в DLT.

**Как применять на практике:**
- Зеркальте сериализацию: чем отправили (`JsonSerializer`), тем и читайте (`JsonDeserializer`), и оберните в `ErrorHandlingDeserializer`.
- `concurrency` ≤ числа партиций; нужно больше — добавляйте партиции.
- Делайте каждый листенер идемпотентным (UNIQUE-ключ/Redis) и коммитьте offset только после успеха.
- Настройте retry + DLT, чтобы один битый месседж не вешал обработку.

**Что дальше:** мы умеем отправлять и принимать — осталось доказать, что эта связка работает. Переходим к тестированию интеграций с Kafka.

[⬅ KafkaTemplate: отправка сообщений](04-kafka-template.md) · [📑 Оглавление модуля](README.md) · [Тестирование интеграций с Kafka ➡](06-testing-kafka.md)
