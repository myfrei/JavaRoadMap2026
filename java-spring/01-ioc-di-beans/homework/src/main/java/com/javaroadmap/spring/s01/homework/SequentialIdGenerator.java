package com.javaroadmap.spring.s01.homework;

/**
 * Задание 5 (часть 1). Генератор по умолчанию: 1, 2, 3, …
 * Сделай его бином, который выигрывает при внедрении по типу IdGenerator,
 * хотя реализаций две.
 */
public class SequentialIdGenerator implements IdGenerator {

    @Override
    public long nextId() {
        throw new UnsupportedOperationException("TODO: задание 5 — последовательные id, начиная с 1");
    }
}
