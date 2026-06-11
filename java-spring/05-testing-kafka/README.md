# Модуль 5. Тестирование и Kafka — оглавление

> Формат модуля — серия лонгридов (лекций). Читай по порядку: каждая статья опирается на предыдущую и связана с ней по смыслу. Это финальный модуль трека по тестам и Kafka — от пирамиды тестов в Spring Boot до интеграционных тестов реального брокера. Статьи 4 → 5 → 6 образуют сквозную цепочку: отправили сообщение → приняли → проверили это тестом.

## Содержание

1. [Тестирование в Spring Boot](01-testing-spring-boot.md)
2. [Очереди, стриминг и Event-Driven архитектура](02-event-driven-queues.md)
3. [Apache Kafka: основы для разработчика](03-kafka-basics.md)
4. [KafkaTemplate: отправка сообщений](04-kafka-template.md)
5. [@KafkaListener: приём сообщений](05-kafka-listener.md)
6. [Тестирование интеграций с Kafka](06-testing-kafka.md)

## 💻 Код модуля

Рядом со статьями живёт запускаемый код (Gradle-подпроект `:java-spring:05-testing-kafka`,
домен — события заказов; Docker не нужен — тесты поднимают EmbeddedKafka внутри JVM):

- [`src/main/java/...`](src/main/java/com/javaroadmap/spring/s05/) — продюсер на `KafkaTemplate`
  (JSON-серде, ключ = orderId), идемпотентный `@KafkaListener`, топики как код (`NewTopic`),
  ретраи + Dead Letter Topic (`DefaultErrorHandler` + `DeadLetterPublishingRecoverer`).
- [`src/test/java/...`](src/test/java/com/javaroadmap/spring/s05/) — пирамида: юнит-тест продюсера
  с мок-шаблоном и интеграционные тесты на EmbeddedKafka (доставка, идемпотентность,
  «ядовитое» событие → 3 попытки → DLT).
- [`homework/`](homework/README.md) — домашка: 4 задания с «красными» тестами
  (`./gradlew :java-spring:05-testing-kafka:homeworkTest`).

```bash
./gradlew :java-spring:05-testing-kafka:test          # тесты примеров
./gradlew :java-spring:05-testing-kafka:homeworkTest  # «красные» задания
```

---

[📚 К треку Java Spring](../README.md)
