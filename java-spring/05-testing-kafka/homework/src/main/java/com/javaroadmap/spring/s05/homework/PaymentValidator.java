package com.javaroadmap.spring.s05.homework;

/**
 * Задание 1. Чистая логика без Spring и Kafka — низ пирамиды тестов.
 * Событие валидно, когда: paymentId не null и не пустой (после trim),
 * amountCents > 0, status — "OK" или "FAILED".
 */
public class PaymentValidator {

    public boolean isValid(PaymentEvent event) {
        throw new UnsupportedOperationException("TODO: задание 1");
    }
}
