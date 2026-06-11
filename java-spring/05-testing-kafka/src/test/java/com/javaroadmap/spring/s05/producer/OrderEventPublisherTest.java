package com.javaroadmap.spring.s05.producer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.javaroadmap.spring.s05.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Низ пирамиды тестов (статья 01): юнит-тест без Spring и без брокера.
 * Проверяем ровно НАШУ логику — топик и ключ; доставку сообщений
 * проверяет интеграционный слой (KafkaIntegrationTest).
 */
class OrderEventPublisherTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);

    private final OrderEventPublisher publisher = new OrderEventPublisher(kafka);

    @Test
    void publishesToOrdersTopicWithOrderIdAsKey() {
        var event = new OrderPlacedEvent("order-1", 500);

        publisher.publish(event);

        verify(kafka).send("orders.placed", "order-1", event);
    }
}
