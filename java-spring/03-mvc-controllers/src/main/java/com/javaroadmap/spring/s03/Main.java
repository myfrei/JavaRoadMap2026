package com.javaroadmap.spring.s03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа модуля 3 — «MVC и контроллеры».
 *
 * <p>Запуск: {@code ./gradlew :java-spring:03-mvc-controllers:run}
 * — REST на /api/tasks, WebSocket-эхо на /ws/echo, gRPC — in-process (см. тесты).
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
