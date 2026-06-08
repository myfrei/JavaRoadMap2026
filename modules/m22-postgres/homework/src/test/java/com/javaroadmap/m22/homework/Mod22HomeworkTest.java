package com.javaroadmap.m22.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 22 — PostgreSQL")
class Mod22HomeworkTest {

    @Test
    @DisplayName("Задание 1: buildSelect")
    void step1_buildSelect() {
        assertEquals(
                "SELECT id, name FROM users WHERE age = ? AND city = ?",
                Mod22Homework.buildSelect("users", List.of("id", "name"), List.of("age", "city")));
        assertEquals("SELECT * FROM t", Mod22Homework.buildSelect("t", List.of(), List.of()));
    }

    @Test
    @DisplayName("Задание 2: keysetPage")
    void step2_keysetPage() {
        assertEquals(
                "SELECT * FROM events WHERE id > ? ORDER BY id ASC LIMIT 20",
                Mod22Homework.keysetPage("events", "id", 20));
    }

    @Test
    @DisplayName("Задание 3: createIndex")
    void step3_createIndex() {
        assertEquals(
                "CREATE INDEX idx_users_city_age ON users (city, age)",
                Mod22Homework.createIndex("users", List.of("city", "age")));
    }

    @Test
    @DisplayName("Задание 4: isSargable")
    void step4_isSargable() {
        assertTrue(Mod22Homework.isSargable("age > 18"));
        assertTrue(Mod22Homework.isSargable("name = 'x'"));
        assertTrue(Mod22Homework.isSargable("name LIKE 'a%'"));
        assertFalse(Mod22Homework.isSargable("lower(name) = 'x'"));
        assertFalse(Mod22Homework.isSargable("name LIKE '%a'"));
    }

    @Test
    @DisplayName("Задание 5: indexColumnOrder (EQ перед RANGE)")
    void step5_indexColumnOrder() {
        assertEquals(
                List.of("city", "status", "age"),
                Mod22Homework.indexColumnOrder(
                        List.of(
                                new Predicate("age", "RANGE"),
                                new Predicate("city", "EQ"),
                                new Predicate("status", "EQ"))));
    }
}
