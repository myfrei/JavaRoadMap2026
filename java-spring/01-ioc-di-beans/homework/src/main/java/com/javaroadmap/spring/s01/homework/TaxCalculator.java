package com.javaroadmap.spring.s01.homework;

/**
 * Задание 3. «Сторонний» класс: аннотации на него повесить нельзя
 * (представь, что он из чужой библиотеки). Менять его не нужно —
 * зарегистрируй его бином в HomeworkConfig.
 */
public final class TaxCalculator {

    private final double rate;

    public TaxCalculator(double rate) {
        this.rate = rate;
    }

    public long withTax(long amount) {
        return Math.round(amount * (1 + rate));
    }
}
