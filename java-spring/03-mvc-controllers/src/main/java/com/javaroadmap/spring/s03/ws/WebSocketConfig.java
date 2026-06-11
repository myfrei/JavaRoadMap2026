package com.javaroadmap.spring.s03.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/** Маппинг «путь → хендлер» для WebSocket: аналог @RequestMapping в MVC. */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EchoWebSocketHandler echoHandler;

    public WebSocketConfig(EchoWebSocketHandler echoHandler) {
        this.echoHandler = echoHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoHandler, "/ws/echo");
    }
}
