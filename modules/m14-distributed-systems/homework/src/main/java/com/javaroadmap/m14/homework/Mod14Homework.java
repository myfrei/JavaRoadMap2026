package com.javaroadmap.m14.homework;

import java.util.Map;

/**
 * Домашка модуля 14 — «Распределённые системы» (векторные часы, кворум, шардирование).
 * Проверка: {@code ./gradlew :modules:m14-distributed-systems:homeworkTest}
 */
public final class Mod14Homework {

    private Mod14Homework() {
    }

    /** Задание 1: вернуть НОВЫЕ часы с инкрементом счётчика узла node на 1. */
    public static Map<String, Integer> incrementClock(Map<String, Integer> clock, String node) {
        throw new UnsupportedOperationException("TODO задание 1: incrementClock");
    }

    /** Задание 2: слить часы — максимум по каждому узлу. */
    public static Map<String, Integer> mergeClocks(Map<String, Integer> a, Map<String, Integer> b) {
        throw new UnsupportedOperationException("TODO задание 2: mergeClocks");
    }

    /** Задание 3: a произошло строго раньше b (по всем узлам a<=b и a != b). */
    public static boolean happensBefore(Map<String, Integer> a, Map<String, Integer> b) {
        throw new UnsupportedOperationException("TODO задание 3: happensBefore");
    }

    /** Задание 4: есть ли кворум (большинство): votes*2 > total. */
    public static boolean hasQuorum(int votes, int total) {
        throw new UnsupportedOperationException("TODO задание 4: hasQuorum");
    }

    /** Задание 5: выбрать шард по ключу: floorMod(key.hashCode(), shards). */
    public static int pickShard(String key, int shards) {
        throw new UnsupportedOperationException("TODO задание 5: pickShard");
    }
}
