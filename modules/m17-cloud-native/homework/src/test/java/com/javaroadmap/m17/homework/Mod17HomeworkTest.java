package com.javaroadmap.m17.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 17 — Cloud Native")
class Mod17HomeworkTest {

    @Test
    @DisplayName("Задание 1: backoffMs (с потолком)")
    void step1_backoffMs() {
        assertEquals(100L, Mod17Homework.backoffMs(0, 100, 10000));
        assertEquals(200L, Mod17Homework.backoffMs(1, 100, 10000));
        assertEquals(400L, Mod17Homework.backoffMs(2, 100, 10000));
        assertEquals(1000L, Mod17Homework.backoffMs(10, 100, 1000));
    }

    @Test
    @DisplayName("Задание 2: aggregateHealth")
    void step2_aggregateHealth() {
        assertEquals("UP", Mod17Homework.aggregateHealth(List.of("UP", "UP")));
        assertEquals("DOWN", Mod17Homework.aggregateHealth(List.of("UP", "DOWN", "UNKNOWN")));
        assertEquals("UNKNOWN", Mod17Homework.aggregateHealth(List.of("UP", "UNKNOWN")));
    }

    @Test
    @DisplayName("Задание 3: parseMemory")
    void step3_parseMemory() {
        assertEquals(1024L, Mod17Homework.parseMemory("1Ki"));
        assertEquals(256L * 1024 * 1024, Mod17Homework.parseMemory("256Mi"));
        assertEquals(1024L * 1024 * 1024, Mod17Homework.parseMemory("1Gi"));
        assertEquals(1000L, Mod17Homework.parseMemory("1000"));
    }

    @Test
    @DisplayName("Задание 4: cpuMillisToCores")
    void step4_cpuMillisToCores() {
        assertEquals(1.0, Mod17Homework.cpuMillisToCores(1000), 1e-9);
        assertEquals(0.5, Mod17Homework.cpuMillisToCores(500), 1e-9);
    }

    @Test
    @DisplayName("Задание 5: clampReplicas")
    void step5_clampReplicas() {
        assertEquals(3, Mod17Homework.clampReplicas(3, 1, 5));
        assertEquals(1, Mod17Homework.clampReplicas(0, 1, 5));
        assertEquals(5, Mod17Homework.clampReplicas(9, 1, 5));
    }
}
