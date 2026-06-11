package com.javaroadmap.spring.s03.ws;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Интеграционный тест WebSocket (статья 06): настоящий Tomcat на случайном
 * порту, настоящий ws://-клиент, настоящее соединение.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EchoWebSocketTest {

    @LocalServerPort
    private int port;

    @Test
    void serverEchoesClientMessage() throws Exception {
        BlockingQueue<String> received = new ArrayBlockingQueue<>(1);
        var client = new StandardWebSocketClient();

        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage message) {
                received.add(message.getPayload());
            }
        }, "ws://localhost:%d/ws/echo".formatted(port)).get(5, TimeUnit.SECONDS);

        try (session) {
            session.sendMessage(new TextMessage("ping"));

            assertThat(received.poll(5, TimeUnit.SECONDS)).isEqualTo("echo: ping");
        }
    }
}
