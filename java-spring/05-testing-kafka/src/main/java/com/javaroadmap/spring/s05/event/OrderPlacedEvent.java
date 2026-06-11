package com.javaroadmap.spring.s05.event;

/**
 * Событие «заказ оформлен» (статья 02): факт, который уже случился.
 * Продюсер не знает и не должен знать, кто и как его обработает.
 */
public record OrderPlacedEvent(String orderId, long amount) {
}
