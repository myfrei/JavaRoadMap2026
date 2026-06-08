package com.javaroadmap.m03.homework;

import java.util.List;

/**
 * Домашка модуля 03 — «Конкурентность». Все задания должны быть потокобезопасны.
 * Проверка: {@code ./gradlew :modules:m03-concurrency:homeworkTest}
 */
public final class Mod03Homework {

    private Mod03Homework() {
    }

    /** Задание 1: сумма массива, посчитанная параллельно несколькими потоками. */
    public static long sumConcurrently(int[] xs, int threads) {
        throw new UnsupportedOperationException("TODO задание 1: sumConcurrently");
    }

    /** Задание 2: threads потоков по incrementsPerThread инкрементов общего счётчика без потери обновлений. */
    public static int atomicCount(int threads, int incrementsPerThread) {
        throw new UnsupportedOperationException("TODO задание 2: atomicCount");
    }

    /** Задание 3: квадраты элементов, посчитанные асинхронно, в исходном порядке. */
    public static List<Integer> parallelSquares(List<Integer> xs) {
        throw new UnsupportedOperationException("TODO задание 3: parallelSquares");
    }

    /** Задание 4: число простых среди значений (используй параллельный стрим). */
    public static long countPrimes(List<Integer> xs) {
        throw new UnsupportedOperationException("TODO задание 4: countPrimes");
    }

    /** Задание 5: producer кладёт 1..n в очередь, consumer суммирует; вернуть сумму. */
    public static int produceConsumeSum(int n) {
        throw new UnsupportedOperationException("TODO задание 5: produceConsumeSum");
    }
}
