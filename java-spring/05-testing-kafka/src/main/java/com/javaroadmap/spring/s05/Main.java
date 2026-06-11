package com.javaroadmap.spring.s05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа модуля 5 — «Тестирование и Kafka».
 *
 * <p>Для запуска нужен брокер на localhost:9092 (например, из модуля m21-kafka
 * основного курса: docker compose up). Тесты брокер не требуют — они поднимают
 * EmbeddedKafka внутри JVM.
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
