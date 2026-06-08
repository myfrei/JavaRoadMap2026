package com.javaroadmap.m19.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 19 — Парсинг и компиляторы")
class Mod19HomeworkTest {

    @Test
    @DisplayName("Задание 1: tokenize")
    void step1_tokenize() {
        assertEquals(
                List.of("12", "+", "3", "*", "(", "4", "-", "5", ")"),
                Mod19Homework.tokenize("12 + 3 * (4 - 5)"));
    }

    @Test
    @DisplayName("Задание 2: matchParens")
    void step2_matchParens() {
        assertTrue(Mod19Homework.matchParens("(a+(b))"));
        assertFalse(Mod19Homework.matchParens("(a"));
        assertFalse(Mod19Homework.matchParens(")("));
    }

    @Test
    @DisplayName("Задание 3: toReversePolish")
    void step3_toReversePolish() {
        assertEquals(
                List.of("2", "3", "4", "*", "+"),
                Mod19Homework.toReversePolish(List.of("2", "+", "3", "*", "4")));
    }

    @Test
    @DisplayName("Задание 4: evaluate")
    void step4_evaluate() {
        assertEquals(14.0, Mod19Homework.evaluate("2+3*4"), 1e-9);
        assertEquals(20.0, Mod19Homework.evaluate("(2+3)*4"), 1e-9);
        assertEquals(2.0, Mod19Homework.evaluate("8/(2+2)"), 1e-9);
    }

    @Test
    @DisplayName("Задание 5: countTokens")
    void step5_countTokens() {
        assertEquals(9, Mod19Homework.countTokens("12 + 3 * (4 - 5)"));
    }
}
