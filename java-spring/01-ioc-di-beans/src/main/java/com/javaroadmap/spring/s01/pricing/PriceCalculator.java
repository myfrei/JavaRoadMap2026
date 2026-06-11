package com.javaroadmap.spring.s01.pricing;

/**
 * «Сторонний» класс: на нём нет Spring-аннотаций, и положить их некуда —
 * представь, что класс приехал из чужой библиотеки. В контейнер он попадает
 * через @Bean-метод в AppConfig (статья 03).
 */
public class PriceCalculator {

    private final double taxRate;

    public PriceCalculator(double taxRate) {
        this.taxRate = taxRate;
    }

    public long withTax(long amount) {
        return Math.round(amount * (1 + taxRate));
    }
}
