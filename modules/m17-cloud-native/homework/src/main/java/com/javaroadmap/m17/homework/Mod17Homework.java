package com.javaroadmap.m17.homework;

import java.util.List;

/**
 * Домашка модуля 17 — «Cloud Native» (backoff, health, ресурсы K8s).
 * Проверка: {@code ./gradlew :modules:m17-cloud-native:homeworkTest}
 */
public final class Mod17Homework {

    private Mod17Homework() {
    }

    /** Задание 1: экспоненциальный backoff: min(cap, base * 2^attempt), attempt с нуля. */
    public static long backoffMs(int attempt, long baseMs, long capMs) {
        throw new UnsupportedOperationException("TODO задание 1: backoffMs");
    }

    /** Задание 2: агрегировать здоровье: есть "DOWN" -> DOWN; есть "UNKNOWN" -> UNKNOWN; иначе UP. */
    public static String aggregateHealth(List<String> statuses) {
        throw new UnsupportedOperationException("TODO задание 2: aggregateHealth");
    }

    /** Задание 3: память K8s в байты: суффиксы Ki/Mi/Gi (1024^n); без суффикса — число как есть. */
    public static long parseMemory(String value) {
        throw new UnsupportedOperationException("TODO задание 3: parseMemory");
    }

    /** Задание 4: milli-CPU в ядра: 1000m -> 1.0. */
    public static double cpuMillisToCores(long millis) {
        throw new UnsupportedOperationException("TODO задание 4: cpuMillisToCores");
    }

    /** Задание 5: ограничить число реплик диапазоном [min, max] (как HPA). */
    public static int clampReplicas(int desired, int min, int max) {
        throw new UnsupportedOperationException("TODO задание 5: clampReplicas");
    }
}
