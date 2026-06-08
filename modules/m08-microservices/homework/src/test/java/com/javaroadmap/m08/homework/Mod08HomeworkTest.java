package com.javaroadmap.m08.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 08 — Микросервисы")
class Mod08HomeworkTest {

    @Test
    @DisplayName("Задание 1: CircuitBreaker")
    void step1_circuitBreaker() {
        CircuitBreaker cb = new CircuitBreaker(2);
        assertEquals("CLOSED", cb.state());
        assertTrue(cb.allowRequest());
        cb.recordFailure();
        cb.recordFailure();
        assertEquals("OPEN", cb.state());
        assertFalse(cb.allowRequest());
        cb.recordSuccess();
        assertEquals("CLOSED", cb.state());
    }

    @Test
    @DisplayName("Задание 2: retryDelaysMs")
    void step2_retryDelaysMs() {
        assertEquals(List.of(100L, 200L, 400L), Mod08Homework.retryDelaysMs(3, 100));
    }

    @Test
    @DisplayName("Задание 3: IdempotencyStore")
    void step3_idempotency() {
        IdempotencyStore store = new IdempotencyStore();
        assertTrue(store.firstSeen("k"));
        assertFalse(store.firstSeen("k"));
        assertTrue(store.firstSeen("other"));
    }

    @Test
    @DisplayName("Задание 4: roundRobin")
    void step4_roundRobin() {
        List<String> nodes = List.of("a", "b", "c");
        assertEquals("a", Mod08Homework.roundRobin(nodes, 0));
        assertEquals("b", Mod08Homework.roundRobin(nodes, 4));
    }

    @Test
    @DisplayName("Задание 5: splitBatches")
    void step5_splitBatches() {
        assertEquals(
                List.of(List.of(1, 2), List.of(3, 4), List.of(5)),
                Mod08Homework.splitBatches(List.of(1, 2, 3, 4, 5), 2));
    }
}
