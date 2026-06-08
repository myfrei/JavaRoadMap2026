package com.javaroadmap.m11.homework;

import java.util.List;

/**
 * Домашка модуля 11 — «Производительность» (эффективные алгоритмы и структуры).
 * Проверка: {@code ./gradlew :modules:m11-performance:homeworkTest}
 */
public final class Mod11Homework {

    private Mod11Homework() {
    }

    /** Задание 1: решето Эратосфена — простые числа <= n. */
    public static List<Integer> sieve(int n) {
        throw new UnsupportedOperationException("TODO задание 1: sieve");
    }

    /** Задание 3: индексы двух чисел с суммой target за O(n). */
    public static int[] twoSumIndices(int[] xs, int target) {
        throw new UnsupportedOperationException("TODO задание 3: twoSumIndices");
    }

    /** Задание 4: число Фибоначчи итеративно за O(n). */
    public static long fibFast(int n) {
        throw new UnsupportedOperationException("TODO задание 4: fibFast");
    }

    /** Задание 5: убрать дубликаты, сохранив порядок первого появления. */
    public static <T> List<T> dedupPreserveOrder(List<T> items) {
        throw new UnsupportedOperationException("TODO задание 5: dedupPreserveOrder");
    }
}

/** Задание 2: LRU-кэш фиксированной ёмкости. При переполнении вытесняется давно не используемый. */
class LruCache<K, V> {

    LruCache(int capacity) {
        throw new UnsupportedOperationException("TODO задание 2: LruCache(int)");
    }

    V get(K key) {
        throw new UnsupportedOperationException("TODO задание 2: get");
    }

    void put(K key, V value) {
        throw new UnsupportedOperationException("TODO задание 2: put");
    }

    int size() {
        throw new UnsupportedOperationException("TODO задание 2: size");
    }
}
