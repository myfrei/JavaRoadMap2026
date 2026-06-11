package com.javaroadmap.spring.s01.homework;

/**
 * Задание 4. Сервисный слой: сделай класс бином, внедри StatsRepository
 * через конструктор и реализуй расчёты по данным репозитория.
 */
public class OrderStatsService {

    // TODO: задание 4 — внедри зависимость через конструктор
    private StatsRepository repository;

    /** Сумма всех заказов. */
    public long totalRevenue() {
        throw new UnsupportedOperationException("TODO: задание 4");
    }

    /** Средний чек (целочисленно). Пустое хранилище -> 0. */
    public long averageCheck() {
        throw new UnsupportedOperationException("TODO: задание 4");
    }

    /** Максимальная сумма заказа. Пустое хранилище -> 0. */
    public long maxAmount() {
        throw new UnsupportedOperationException("TODO: задание 4");
    }
}
