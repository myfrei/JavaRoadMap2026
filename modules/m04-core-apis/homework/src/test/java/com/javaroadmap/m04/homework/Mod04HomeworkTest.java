package com.javaroadmap.m04.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 04 — Core APIs")
class Mod04HomeworkTest {

    @Test
    @DisplayName("Задание 1: daysBetween")
    void step1_daysBetween() {
        assertEquals(10L, Mod04Homework.daysBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 11)));
    }

    @Test
    @DisplayName("Задание 2: isWeekend")
    void step2_isWeekend() {
        // 2000-01-01 — суббота, 2000-01-03 — понедельник.
        assertTrue(Mod04Homework.isWeekend(LocalDate.of(2000, 1, 1)));
        assertFalse(Mod04Homework.isWeekend(LocalDate.of(2000, 1, 3)));
    }

    @Test
    @DisplayName("Задание 3: extractEmails")
    void step3_extractEmails() {
        assertEquals(
                List.of("a@b.com", "c.d@e.org"),
                Mod04Homework.extractEmails("write a@b.com and c.d@e.org!"));
    }

    @Test
    @DisplayName("Задание 4: slugify")
    void step4_slugify() {
        assertEquals("hello-world-2026", Mod04Homework.slugify("Hello, World! 2026"));
    }

    @Test
    @DisplayName("Задание 5: secondsToClock")
    void step5_secondsToClock() {
        assertEquals("1:01:01", Mod04Homework.secondsToClock(3661));
        assertEquals("0:00:59", Mod04Homework.secondsToClock(59));
    }
}
