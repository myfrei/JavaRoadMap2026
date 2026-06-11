package com.javaroadmap.spring.s01.homework;

/**
 * Задание 1 (часть 1). Сделай класс бином, который контейнер найдёт
 * component scan-ом, и реализуй format().
 */
public class RubPriceFormatter implements PriceFormatter {

    @Override
    public String format(long amount) {
        throw new UnsupportedOperationException("TODO: задание 1 — формат \"<сумма> ₽\"");
    }
}
