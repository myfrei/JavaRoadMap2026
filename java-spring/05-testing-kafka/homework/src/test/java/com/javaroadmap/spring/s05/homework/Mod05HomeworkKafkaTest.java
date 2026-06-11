package com.javaroadmap.spring.s05.homework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

/**
 * «Красные» интеграционные тесты домашки модуля 5 — на встроенном брокере.
 * Менять их не нужно.
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1)
class Mod05HomeworkKafkaTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private KafkaTemplate<String, Object> kafka;

    @Autowired
    private PaymentStore store;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @BeforeEach
    void resetStore() {
        store.clear();
    }

    @Test
    @DisplayName("Задание 2: publish отправляет событие в topic payments с ключом paymentId")
    void task2_publisherSendsKeyedRecord() {
        PaymentPublisher publisher = context.getBean(PaymentPublisher.class);

        publisher.publish(new PaymentEvent("pay-42", 4200, "OK"));

        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, "hw-publisher-check",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        try (var consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(List.of(PaymentPublisher.PAYMENTS_TOPIC));
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(
                    consumer, PaymentPublisher.PAYMENTS_TOPIC, Duration.ofSeconds(15));

            assertThat(record.key()).isEqualTo("pay-42");
            assertThat(record.value()).contains("pay-42").contains("4200");
        }
    }

    @Test
    @DisplayName("Задание 3: листенер сохраняет OK-платежи и игнорирует FAILED")
    void task3_listenerStoresOnlyOk() {
        kafka.send(PaymentPublisher.PAYMENTS_TOPIC, "hw-fail", new PaymentEvent("hw-fail", 100, "FAILED"));
        kafka.send(PaymentPublisher.PAYMENTS_TOPIC, "hw-ok", new PaymentEvent("hw-ok", 500, "OK"));

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(store.find("hw-ok")).isPresent());

        // одна партиция: FAILED обработан раньше OK — и его в хранилище нет
        assertThat(store.find("hw-fail")).isEmpty();
    }

    @Test
    @DisplayName("Задание 3: повторная доставка не дублирует платёж")
    void task3_duplicateDeliveryIsIdempotent() {
        var event = new PaymentEvent("hw-dup", 300, "OK");
        kafka.send(PaymentPublisher.PAYMENTS_TOPIC, event.paymentId(), event);
        kafka.send(PaymentPublisher.PAYMENTS_TOPIC, event.paymentId(), event);
        kafka.send(PaymentPublisher.PAYMENTS_TOPIC, "hw-last", new PaymentEvent("hw-last", 1, "OK"));

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(store.find("hw-last")).isPresent());

        assertThat(store.find("hw-dup")).isPresent();
        assertThat(store.count()).as("дубликат не создал второй записи").isEqualTo(2);
    }
}
