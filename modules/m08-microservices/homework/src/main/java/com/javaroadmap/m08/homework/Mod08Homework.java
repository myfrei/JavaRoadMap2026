package com.javaroadmap.m08.homework;

import java.util.List;

/**
 * Домашка модуля 08 — «Микросервисы» (логика отказоустойчивости: без сети).
 * Проверка: {@code ./gradlew :modules:m08-microservices:homeworkTest}
 */
public final class Mod08Homework {

    private Mod08Homework() {
    }

    /** Задание 2: задержки ретраев (экспонента): base, base*2, base*4, ... всего attempts штук. */
    public static List<Long> retryDelaysMs(int attempts, long baseMs) {
        throw new UnsupportedOperationException("TODO задание 2: retryDelaysMs");
    }

    /** Задание 4: round-robin — выбрать узел по индексу запроса. */
    public static String roundRobin(List<String> nodes, int requestIndex) {
        throw new UnsupportedOperationException("TODO задание 4: roundRobin");
    }

    /** Задание 5: разбить список на батчи по size. [1..5],2 -> [[1,2],[3,4],[5]]. */
    public static <T> List<List<T>> splitBatches(List<T> items, int size) {
        throw new UnsupportedOperationException("TODO задание 5: splitBatches");
    }
}

/** Задание 1: circuit breaker. После failureThreshold подряд ошибок -> OPEN; recordSuccess -> CLOSED. */
class CircuitBreaker {

    CircuitBreaker(int failureThreshold) {
        throw new UnsupportedOperationException("TODO задание 1: CircuitBreaker(int)");
    }

    String state() {
        throw new UnsupportedOperationException("TODO задание 1: state");
    }

    boolean allowRequest() {
        throw new UnsupportedOperationException("TODO задание 1: allowRequest");
    }

    void recordFailure() {
        throw new UnsupportedOperationException("TODO задание 1: recordFailure");
    }

    void recordSuccess() {
        throw new UnsupportedOperationException("TODO задание 1: recordSuccess");
    }
}

/** Задание 3: хранилище идемпотентности. firstSeen(key): true в первый раз, далее false. */
class IdempotencyStore {

    boolean firstSeen(String key) {
        throw new UnsupportedOperationException("TODO задание 3: firstSeen");
    }
}
