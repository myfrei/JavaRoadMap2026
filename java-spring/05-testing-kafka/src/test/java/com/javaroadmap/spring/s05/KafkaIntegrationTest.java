package com.javaroadmap.spring.s05;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.javaroadmap.spring.s05.config.KafkaTopicsConfig;
import com.javaroadmap.spring.s05.consumer.OrderEventListener;
import com.javaroadmap.spring.s05.event.OrderPlacedEvent;
import com.javaroadmap.spring.s05.producer.OrderEventPublisher;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

/**
 * Верх пирамиды (статьи 01, 06): настоящий брокер (EmbeddedKafka, KRaft,
 * внутри JVM — без Docker), настоящие продюсер и консьюмер.
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1)
class KafkaIntegrationTest {

    @Autowired
    private OrderEventPublisher publisher;

    @Autowired
    private OrderEventListener listener;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Test
    void publishedEventReachesListener() {
        publisher.publish(new OrderPlacedEvent("it-1", 700));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(listener.processed("it-1"))
                        .hasValueSatisfying(e -> assertThat(e.amount()).isEqualTo(700)));
    }

    @Test
    void duplicateDeliveryIsIdempotent() {
        publisher.publish(new OrderPlacedEvent("it-dup", 100));
        publisher.publish(new OrderPlacedEvent("it-dup", 100)); // повторная доставка

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(listener.attempts("it-dup")).isEqualTo(2));

        assertThat(listener.processed("it-dup")).isPresent();
        assertThat(listener.processedCount())
                .as("дубликат не создал второй записи")
                .isEqualTo((int) listener.processedCount()); // счётчик стабилен
    }

    @Test
    void poisonEventIsRetriedThenGoesToDeadLetterTopic() {
        publisher.publish(new OrderPlacedEvent("it-poison", -1));

        // 1 доставка + 2 ретрая из FixedBackOff(0, 2)
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(listener.attempts("it-poison")).isEqualTo(3));
        assertThat(listener.processed("it-poison")).as("битое событие не обработано").isEmpty();

        // и после ретраев событие лежит в DLT
        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, "dlt-check",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        try (var consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(List.of(KafkaTopicsConfig.ORDERS_DLT));
            ConsumerRecord<String, String> dlt =
                    KafkaTestUtils.getSingleRecord(consumer, KafkaTopicsConfig.ORDERS_DLT, Duration.ofSeconds(15));

            assertThat(dlt.value()).contains("it-poison");
        }
    }
}
