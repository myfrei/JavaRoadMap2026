package com.javaroadmap.spring.s05.producer;

import com.javaroadmap.spring.s05.config.KafkaTopicsConfig;
import com.javaroadmap.spring.s05.event.OrderPlacedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Продюсер (статья 04): KafkaTemplate — это «JdbcTemplate для Kafka».
 * Ключ = orderId: события одного заказа попадают в одну партицию,
 * а значит обрабатываются по порядку.
 */
@Service
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void publish(OrderPlacedEvent event) {
        kafka.send(KafkaTopicsConfig.ORDERS_TOPIC, event.orderId(), event);
    }
}
