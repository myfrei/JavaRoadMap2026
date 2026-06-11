package com.javaroadmap.spring.s05.homework;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Готовое хранилище обработанных платежей — менять не нужно.
 * accept() идемпотентен: повторный paymentId не перезаписывает запись.
 */
@Component
public class PaymentStore {

    private final Map<String, PaymentEvent> payments = new ConcurrentHashMap<>();

    public void accept(PaymentEvent event) {
        payments.putIfAbsent(event.paymentId(), event);
    }

    public Optional<PaymentEvent> find(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public int count() {
        return payments.size();
    }

    public void clear() {
        payments.clear();
    }
}
