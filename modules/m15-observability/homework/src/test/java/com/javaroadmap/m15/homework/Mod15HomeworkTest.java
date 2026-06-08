package com.javaroadmap.m15.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 15 — Наблюдаемость")
class Mod15HomeworkTest {

    @Test
    @DisplayName("Задание 1: percentile (nearest-rank)")
    void step1_percentile() {
        List<Long> s = List.of(30L, 10L, 50L, 20L, 40L);
        assertEquals(10L, Mod15Homework.percentile(s, 20));
        assertEquals(30L, Mod15Homework.percentile(s, 50));
        assertEquals(50L, Mod15Homework.percentile(s, 100));
    }

    @Test
    @DisplayName("Задание 2: ratePerSecond")
    void step2_ratePerSecond() {
        assertEquals(50.0, Mod15Homework.ratePerSecond(100, 2000), 1e-9);
    }

    @Test
    @DisplayName("Задание 3: parseLogLevel")
    void step3_parseLogLevel() {
        assertEquals("ERROR", Mod15Homework.parseLogLevel("12:00 ERROR boom"));
        assertEquals("WARN", Mod15Homework.parseLogLevel("12:00 WARN slow"));
        assertEquals("UNKNOWN", Mod15Homework.parseLogLevel("hello world"));
    }

    @Test
    @DisplayName("Задание 4: SlidingWindowCounter")
    void step4_slidingWindow() {
        SlidingWindowCounter c = new SlidingWindowCounter();
        c.record(1000);
        c.record(1500);
        c.record(2000);
        assertEquals(3L, c.count(2000, 1000));
        assertEquals(2L, c.count(2000, 600));
    }

    @Test
    @DisplayName("Задание 5: formatLabels")
    void step5_formatLabels() {
        assertEquals(
                "{code=\"200\",method=\"GET\"}",
                Mod15Homework.formatLabels(Map.of("method", "GET", "code", "200")));
        assertEquals("{}", Mod15Homework.formatLabels(Map.of()));
    }
}
