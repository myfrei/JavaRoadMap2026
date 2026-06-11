package com.javaroadmap.spring.s03.homework;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/** Готовая регистрация WebSocket-хендлера домашки — менять не нужно. */
@Configuration
@EnableWebSocket
public class HomeworkWsConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new UppercaseWebSocketHandler(), "/ws/upper");
    }
}
