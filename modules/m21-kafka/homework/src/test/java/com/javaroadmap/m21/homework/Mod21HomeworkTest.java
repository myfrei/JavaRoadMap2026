package com.javaroadmap.m21.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 21 — Kafka")
class Mod21HomeworkTest {

    @Test
    @DisplayName("Задание 1: partitionFor (детерминированный, в диапазоне)")
    void step1_partitionFor() {
        assertEquals(0, Mod21Homework.partitionFor("anything", 1));
        int p = Mod21Homework.partitionFor("order-42", 6);
        assertTrue(p >= 0 && p < 6);
        assertEquals(p, Mod21Homework.partitionFor("order-42", 6));
    }

    @Test
    @DisplayName("Задание 2: IdempotentProcessor (без дублей)")
    void step2_idempotent() {
        IdempotentProcessor proc = new IdempotentProcessor();
        assertTrue(proc.process("m1"));
        assertFalse(proc.process("m1"));
        assertTrue(proc.process("m2"));
        assertEquals(2, proc.processedCount());
    }

    @Test
    @DisplayName("Задание 3: nextCommitOffset (первый разрыв)")
    void step3_nextCommitOffset() {
        assertEquals(0L, Mod21Homework.nextCommitOffset(List.of()));
        assertEquals(3L, Mod21Homework.nextCommitOffset(List.of(0L, 1L, 2L)));
        assertEquals(3L, Mod21Homework.nextCommitOffset(List.of(0L, 1L, 2L, 4L)));
        assertEquals(0L, Mod21Homework.nextCommitOffset(List.of(1L, 2L)));
    }

    @Test
    @DisplayName("Задание 4: retryRoute (RETRY/DLQ)")
    void step4_retryRoute() {
        assertEquals("RETRY", Mod21Homework.retryRoute(0, 3));
        assertEquals("RETRY", Mod21Homework.retryRoute(2, 3));
        assertEquals("DLQ", Mod21Homework.retryRoute(3, 3));
        assertEquals("DLQ", Mod21Homework.retryRoute(5, 3));
    }

    @Test
    @DisplayName("Задание 5: latestByKey (компакция)")
    void step5_latestByKey() {
        Map<String, String> m =
                Mod21Homework.latestByKey(
                        List.of(
                                new KafkaRecord("k1", "a", 0),
                                new KafkaRecord("k2", "b", 1),
                                new KafkaRecord("k1", "c", 2)));
        assertEquals("c", m.get("k1"));
        assertEquals("b", m.get("k2"));
        assertEquals(2, m.size());
    }
}
