package com.javaroadmap.spring.s01.model;

/** Заказ: сумма — в рублях без копеек, для простоты примеров. */
public record Order(long id, String customer, long amount) {
}
