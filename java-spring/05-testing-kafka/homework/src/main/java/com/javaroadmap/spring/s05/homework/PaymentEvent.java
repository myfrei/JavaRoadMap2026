package com.javaroadmap.spring.s05.homework;

/** Событие платежа — менять не нужно. status: "OK" или "FAILED". */
public record PaymentEvent(String paymentId, long amountCents, String status) {
}
