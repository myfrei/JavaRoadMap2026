package com.javaroadmap.spring.s05.consumer;

import com.javaroadmap.spring.s05.config.KafkaTopicsConfig;
import com.javaroadmap.spring.s05.event.OrderPlacedEvent;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Консьюмер (статья 05). Kafka гарантирует at-least-once: одно событие может
 * прийти дважды (ребаланс, ретрай продюсера) — поэтому обработка идемпотентна:
 * повторный orderId ничего не меняет.
 */
@Component
public class OrderEventListener {

    private final Map<String, OrderPlacedEvent> processed = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();

    @KafkaListener(topics = KafkaTopicsConfig.ORDERS_TOPIC, groupId = "s05-orders")
    public void onOrderPlaced(OrderPlacedEvent event) {
        attempts.computeIfAbsent(event.orderId(), id -> new AtomicInteger()).incrementAndGet();

        if (event.amount() <= 0) {
            // после ретраев DefaultErrorHandler отправит событие в orders.placed-dlt
            throw new IllegalArgumentException("Некорректная сумма заказа: " + event.amount());
        }

        processed.putIfAbsent(event.orderId(), event);
    }

    public Optional<OrderPlacedEvent> processed(String orderId) {
        return Optional.ofNullable(processed.get(orderId));
    }

    public int processedCount() {
        return processed.size();
    }

    /** Сколько раз событие реально доставлялось — видно ретраи. */
    public int attempts(String orderId) {
        return attempts.getOrDefault(orderId, new AtomicInteger()).get();
    }
}
