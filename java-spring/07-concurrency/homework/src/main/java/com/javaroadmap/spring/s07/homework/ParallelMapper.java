package com.javaroadmap.spring.s07.homework;

import java.util.List;
import java.util.function.Function;

/**
 * Задание 2. Применить функцию к каждому элементу ПАРАЛЛЕЛЬНО
 * (ExecutorService с parallelism потоками) и вернуть результаты
 * в исходном порядке. Пул обязан закрыться при любом исходе.
 */
public class ParallelMapper {

    public <T, R> List<R> map(List<T> items, Function<T, R> mapper, int parallelism) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: задание 2");
    }
}
