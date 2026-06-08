package com.javaroadmap.m15.homework;

import java.util.List;
import java.util.Map;

/**
 * Домашка модуля 15 — «Наблюдаемость» (перцентили, rate, парсинг логов, метки).
 * Проверка: {@code ./gradlew :modules:m15-observability:homeworkTest}
 */
public final class Mod15Homework {

    private Mod15Homework() {
    }

    /** Задание 1: перцентиль методом nearest-rank (p в [0,100]); список можно не сортированный. */
    public static long percentile(List<Long> samples, double p) {
        throw new UnsupportedOperationException("TODO задание 1: percentile");
    }

    /** Задание 2: события в секунду: count / (millis/1000). */
    public static double ratePerSecond(long count, long millis) {
        throw new UnsupportedOperationException("TODO задание 2: ratePerSecond");
    }

    /** Задание 3: уровень лога из строки: INFO/WARN/ERROR/DEBUG (в верхнем регистре); иначе "UNKNOWN". */
    public static String parseLogLevel(String line) {
        throw new UnsupportedOperationException("TODO задание 3: parseLogLevel");
    }

    /** Задание 5: метки в стиле Prometheus: {k="v",...} с отсортированными ключами; пусто -> "{}". */
    public static String formatLabels(Map<String, String> labels) {
        throw new UnsupportedOperationException("TODO задание 5: formatLabels");
    }
}

/** Задание 4: счётчик в скользящем окне. count(now, window) — события в [now-window, now]. */
class SlidingWindowCounter {

    void record(long timestampMs) {
        throw new UnsupportedOperationException("TODO задание 4: record");
    }

    long count(long nowMs, long windowMs) {
        throw new UnsupportedOperationException("TODO задание 4: count");
    }
}
