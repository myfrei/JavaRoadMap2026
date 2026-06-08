package com.javaroadmap.m02.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 02 — Глубокая Java")
class Mod02HomeworkTest {

    @Test
    @DisplayName("Задание 1: distinctSorted")
    void step1_distinctSorted() {
        assertEquals(List.of(1, 2, 3), Mod02Homework.distinctSorted(List.of(3, 1, 2, 3, 1)));
    }

    @Test
    @DisplayName("Задание 2: groupByFirstLetter")
    void step2_groupByFirstLetter() {
        Map<Character, List<String>> g =
                Mod02Homework.groupByFirstLetter(List.of("apple", "avocado", "banana"));
        assertEquals(List.of("apple", "avocado"), g.get('a'));
        assertEquals(List.of("banana"), g.get('b'));
    }

    @Test
    @DisplayName("Задание 3: sumEven")
    void step3_sumEven() {
        assertEquals(12, Mod02Homework.sumEven(List.of(1, 2, 3, 4, 5, 6)));
    }

    @Test
    @DisplayName("Задание 4: firstLongerThan")
    void step4_firstLongerThan() {
        assertEquals(Optional.of("bb"), Mod02Homework.firstLongerThan(List.of("a", "bb", "ccc"), 1));
        assertTrue(Mod02Homework.firstLongerThan(List.of("a"), 5).isEmpty());
    }

    @Test
    @DisplayName("Задание 5: joinUpper")
    void step5_joinUpper() {
        assertEquals("A,B,C", Mod02Homework.joinUpper(List.of("a", "b", "c")));
    }
}
