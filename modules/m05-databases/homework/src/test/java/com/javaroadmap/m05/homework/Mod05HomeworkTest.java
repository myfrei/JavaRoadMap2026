package com.javaroadmap.m05.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 05 — Базы данных")
class Mod05HomeworkTest {

    @Test
    @DisplayName("Задание 1: InMemoryRepository")
    void step1_repository() {
        InMemoryRepository<Integer, String> repo = new InMemoryRepository<>();
        repo.save(1, "a");
        repo.save(2, "b");
        assertEquals(2, repo.count());
        assertEquals(Optional.of("a"), repo.findById(1));
        repo.deleteById(1);
        assertEquals(1, repo.count());
        assertTrue(repo.findById(1).isEmpty());
    }

    @Test
    @DisplayName("Задание 2: pageOf")
    void step2_pageOf() {
        List<Integer> all = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertEquals(List.of(4, 5, 6), Mod05Homework.pageOf(all, 1, 3));
        assertEquals(List.of(10), Mod05Homework.pageOf(all, 3, 3));
    }

    @Test
    @DisplayName("Задание 3: buildSelect")
    void step3_buildSelect() {
        assertEquals(
                "SELECT id, name FROM users",
                Mod05Homework.buildSelect("users", List.of("id", "name")));
        assertEquals("SELECT * FROM t", Mod05Homework.buildSelect("t", List.of()));
    }

    @Test
    @DisplayName("Задание 4: topN")
    void step4_topN() {
        assertEquals(List.of(9, 6, 5), Mod05Homework.topN(List.of(3, 1, 4, 1, 5, 9, 2, 6), 3));
    }

    @Test
    @DisplayName("Задание 5: groupCount")
    void step5_groupCount() {
        Map<String, Long> c = Mod05Homework.groupCount(List.of("a", "b", "a", "c", "a", "b"));
        assertEquals(3L, c.get("a"));
        assertEquals(2L, c.get("b"));
        assertEquals(1L, c.get("c"));
    }
}
