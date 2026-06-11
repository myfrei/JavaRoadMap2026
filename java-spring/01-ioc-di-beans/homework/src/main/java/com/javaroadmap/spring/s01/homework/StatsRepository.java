package com.javaroadmap.spring.s01.homework;

import java.util.List;

/** Задание 4. Порт хранилища сумм заказов — менять его не нужно. */
public interface StatsRepository {

    List<Long> amounts();
}
