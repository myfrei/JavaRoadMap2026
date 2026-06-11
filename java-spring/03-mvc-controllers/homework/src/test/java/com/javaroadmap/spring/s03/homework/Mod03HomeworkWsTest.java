package com.javaroadmap.spring.s03.homework;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/** «Красный» тест WebSocket-части домашки модуля 3. Менять его не нужно. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Mod03HomeworkWsTest {

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Задание 5: /ws/upper отвечает сообщением в верхнем регистре")
    void task5_uppercaseEcho() throws Exception {
        BlockingQueue<String> received = new ArrayBlockingQueue<>(1);
        var client = new StandardWebSocketClient();

        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage message) {
                received.add(message.getPayload());
            }
        }, "ws://localhost:%d/ws/upper".formatted(port)).get(5, TimeUnit.SECONDS);

        try (session) {
            session.sendMessage(new TextMessage("hello spring"));

            assertThat(received.poll(5, TimeUnit.SECONDS)).isEqualTo("HELLO SPRING");
        }
    }
}
