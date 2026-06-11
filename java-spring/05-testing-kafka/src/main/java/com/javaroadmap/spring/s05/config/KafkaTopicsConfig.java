package com.javaroadmap.spring.s05.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Топики как код (статья 03): KafkaAdmin создаст их при старте, если их нет. */
@Configuration
public class KafkaTopicsConfig {

    public static final String ORDERS_TOPIC = "orders.placed";

    /** Суффикс "-dlt" — дефолт DeadLetterPublishingRecoverer. */
    public static final String ORDERS_DLT = "orders.placed-dlt";

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name(ORDERS_TOPIC).partitions(1).replicas(1).build();
    }

    /** Dead Letter Topic: сюда уезжают события, не пережившие ретраи (статья 05). */
    @Bean
    public NewTopic ordersDlt() {
        return TopicBuilder.name(ORDERS_DLT).partitions(1).replicas(1).build();
    }
}
