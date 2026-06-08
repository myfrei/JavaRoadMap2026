package com.javaroadmap.m06.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 06 — Web и API")
class Mod06HomeworkTest {

    @Test
    @DisplayName("Задание 1: parseQuery")
    void step1_parseQuery() {
        assertEquals(Map.of("a", "1", "b", "2"), Mod06Homework.parseQuery("a=1&b=2"));
        assertTrue(Mod06Homework.parseQuery("").isEmpty());
    }

    @Test
    @DisplayName("Задание 2: matchRoute")
    void step2_matchRoute() {
        assertEquals(
                Map.of("id", "42"),
                Mod06Homework.matchRoute("/users/{id}", "/users/42").orElseThrow());
        assertTrue(Mod06Homework.matchRoute("/users/{id}", "/posts/1").isEmpty());
    }

    @Test
    @DisplayName("Задание 3: statusText")
    void step3_statusText() {
        assertEquals("OK", Mod06Homework.statusText(200));
        assertEquals("Not Found", Mod06Homework.statusText(404));
        assertEquals("Internal Server Error", Mod06Homework.statusText(500));
        assertEquals("Unknown", Mod06Homework.statusText(999));
    }

    @Test
    @DisplayName("Задание 4: validate")
    void step4_validate() {
        assertTrue(Mod06Homework.validate("", 30).contains("name is required"));
        assertTrue(Mod06Homework.validate("Ann", -1).contains("age must be >= 0"));
        assertTrue(Mod06Homework.validate("Ann", 30).isEmpty());
    }

    @Test
    @DisplayName("Задание 5: buildUrl")
    void step5_buildUrl() {
        assertEquals("/api?a=1&b=2", Mod06Homework.buildUrl("/api", Map.of("b", "2", "a", "1")));
        assertEquals("/api", Mod06Homework.buildUrl("/api", Map.of()));
    }
}
