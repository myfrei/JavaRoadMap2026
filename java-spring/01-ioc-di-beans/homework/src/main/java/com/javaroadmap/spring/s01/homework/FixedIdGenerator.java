package com.javaroadmap.spring.s01.homework;

/**
 * Задание 5 (часть 2). Тестовый генератор: всегда возвращает 42.
 * Сделай его бином с именем "fixed" — его достают по квалификатору.
 */
public class FixedIdGenerator implements IdGenerator {

    @Override
    public long nextId() {
        throw new UnsupportedOperationException("TODO: задание 5 — всегда 42");
    }
}
