package com.javaroadmap.spring.s05.homework;

import org.springframework.kafka.core.KafkaTemplate;

/**
 * Задание 2. Сделай класс бином и реализуй публикацию события
 * в топик "payments" с ключом paymentId (образец — OrderEventPublisher
 * из примеров модуля).
 */
public class PaymentPublisher {

    public static final String PAYMENTS_TOPIC = "payments";

    private final KafkaTemplate<String, Object> kafka;

    public PaymentPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void publish(PaymentEvent event) {
        throw new UnsupportedOperationException("TODO: задание 2");
    }
}
