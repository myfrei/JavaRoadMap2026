package com.javaroadmap.spring.s01.homework;

/** Задание 1. Контракт форматирования цены — менять его не нужно. */
public interface PriceFormatter {

    /** 100 -> "100 ₽" */
    String format(long amount);
}
