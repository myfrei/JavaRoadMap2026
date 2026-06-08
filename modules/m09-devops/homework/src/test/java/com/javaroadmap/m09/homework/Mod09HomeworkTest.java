package com.javaroadmap.m09.homework;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 09 — DevOps")
class Mod09HomeworkTest {

    @Test
    @DisplayName("Задание 1: parseSemver")
    void step1_parseSemver() {
        assertArrayEquals(new int[] {1, 2, 3}, Mod09Homework.parseSemver("1.2.3"));
        assertThrows(IllegalArgumentException.class, () -> Mod09Homework.parseSemver("1.2"));
        assertThrows(IllegalArgumentException.class, () -> Mod09Homework.parseSemver("a.b.c"));
    }

    @Test
    @DisplayName("Задание 2: compareSemver")
    void step2_compareSemver() {
        assertTrue(Mod09Homework.compareSemver("1.2.3", "1.2.4") < 0);
        assertTrue(Mod09Homework.compareSemver("1.3.0", "1.2.9") > 0);
        assertEquals(0, Mod09Homework.compareSemver("2.0.0", "2.0.0"));
    }

    @Test
    @DisplayName("Задание 3: parseEnv")
    void step3_parseEnv() {
        Map<String, String> env = Mod09Homework.parseEnv("A=1\n# comment\n\nB=two\n");
        assertEquals(2, env.size());
        assertEquals("1", env.get("A"));
        assertEquals("two", env.get("B"));
    }

    @Test
    @DisplayName("Задание 4: dockerTag")
    void step4_dockerTag() {
        assertEquals("feature-login-abcdef1", Mod09Homework.dockerTag("feature/login", "abcdef1234567"));
    }

    @Test
    @DisplayName("Задание 5: isValidImageName")
    void step5_isValidImageName() {
        assertTrue(Mod09Homework.isValidImageName("myapp"));
        assertTrue(Mod09Homework.isValidImageName("org/myapp:1.0"));
        assertFalse(Mod09Homework.isValidImageName("My_App"));
        assertFalse(Mod09Homework.isValidImageName(""));
    }
}
