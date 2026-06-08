package com.javaroadmap.m14.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 14 — Распределённые системы")
class Mod14HomeworkTest {

    @Test
    @DisplayName("Задание 1: incrementClock")
    void step1_incrementClock() {
        assertEquals(2, Mod14Homework.incrementClock(Map.of("a", 1), "a").get("a"));
        Map<String, Integer> c = Mod14Homework.incrementClock(Map.of("a", 1), "b");
        assertEquals(1, c.get("a"));
        assertEquals(1, c.get("b"));
    }

    @Test
    @DisplayName("Задание 2: mergeClocks (max по узлам)")
    void step2_mergeClocks() {
        Map<String, Integer> m =
                Mod14Homework.mergeClocks(Map.of("a", 2, "b", 1), Map.of("a", 1, "b", 3, "c", 1));
        assertEquals(2, m.get("a"));
        assertEquals(3, m.get("b"));
        assertEquals(1, m.get("c"));
    }

    @Test
    @DisplayName("Задание 3: happensBefore")
    void step3_happensBefore() {
        assertTrue(Mod14Homework.happensBefore(Map.of("a", 1), Map.of("a", 2)));
        assertFalse(Mod14Homework.happensBefore(Map.of("a", 2), Map.of("a", 1)));
        assertFalse(Mod14Homework.happensBefore(Map.of("a", 1), Map.of("a", 1)));
    }

    @Test
    @DisplayName("Задание 4: hasQuorum")
    void step4_hasQuorum() {
        assertTrue(Mod14Homework.hasQuorum(2, 3));
        assertFalse(Mod14Homework.hasQuorum(1, 3));
        assertFalse(Mod14Homework.hasQuorum(2, 4));
        assertTrue(Mod14Homework.hasQuorum(3, 4));
    }

    @Test
    @DisplayName("Задание 5: pickShard (детерминированный, в диапазоне)")
    void step5_pickShard() {
        assertEquals(0, Mod14Homework.pickShard("anything", 1));
        int s = Mod14Homework.pickShard("user-42", 8);
        assertTrue(s >= 0 && s < 8);
        assertEquals(s, Mod14Homework.pickShard("user-42", 8));
    }
}
