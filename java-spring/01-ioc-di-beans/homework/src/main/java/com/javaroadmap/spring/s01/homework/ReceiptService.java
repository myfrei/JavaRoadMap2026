package com.javaroadmap.spring.s01.homework;

/**
 * Задание 1 (часть 2). Сделай класс сервисом-бином и внедри PriceFormatter
 * через конструктор (поле уже объявлено — добавь конструктор сам).
 */
public class ReceiptService {

    // TODO: задание 1 — внедри зависимость через конструктор
    private PriceFormatter priceFormatter;

    /** Чек вида: "Чек для alice: 100 ₽" (сумма — через PriceFormatter). */
    public String receipt(String customer, long amount) {
        throw new UnsupportedOperationException("TODO: задание 1");
    }
}
