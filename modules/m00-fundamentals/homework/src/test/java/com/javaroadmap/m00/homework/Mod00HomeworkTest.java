package com.javaroadmap.m00.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 00 — Фундамент")
class Mod00HomeworkTest {

    @Test
    @DisplayName("Задание 1: reverse")
    void step1_reverse() {
        assertEquals("cba", Mod00Homework.reverse("abc"));
        assertEquals("", Mod00Homework.reverse(""));
        assertEquals("a", Mod00Homework.reverse("a"));
    }

    @Test
    @DisplayName("Задание 2: countNonEmptyLines")
    void step2_countNonEmptyLines() {
        assertEquals(0, Mod00Homework.countNonEmptyLines(""));
        assertEquals(2, Mod00Homework.countNonEmptyLines("a\n\nb\n   \n"));
        assertEquals(3, Mod00Homework.countNonEmptyLines("x\ny\nz"));
    }

    @Test
    @DisplayName("Задание 3: wordCount")
    void step3_wordCount() {
        assertEquals(0, Mod00Homework.wordCount("   "));
        assertEquals(1, Mod00Homework.wordCount("hello"));
        assertEquals(3, Mod00Homework.wordCount("  a  b   c "));
    }

    @Test
    @DisplayName("Задание 4: wordFrequencies")
    void step4_wordFrequencies() {
        Map<String, Integer> f = Mod00Homework.wordFrequencies("Go go, JAVA java java!");
        assertEquals(2, f.get("go"));
        assertEquals(3, f.get("java"));
    }

    @Test
    @DisplayName("Задание 5: parseArgs")
    void step5_parseArgs() {
        Map<String, String> a =
                Mod00Homework.parseArgs(new String[] {"--name=Ann", "--age", "30", "--verbose"});
        assertEquals("Ann", a.get("name"));
        assertEquals("30", a.get("age"));
        assertEquals("true", a.get("verbose"));
    }

    @Test
    @DisplayName("Задание 6: isPalindrome")
    void step6_isPalindrome() {
        assertTrue(Mod00Homework.isPalindrome("A man, a plan, a canal: Panama"));
        assertFalse(Mod00Homework.isPalindrome("java"));
    }
}
