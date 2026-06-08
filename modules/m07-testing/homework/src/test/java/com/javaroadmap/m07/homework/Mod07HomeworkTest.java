package com.javaroadmap.m07.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 07 — Тестирование")
class Mod07HomeworkTest {

    @Test
    @DisplayName("Задание 1: String Calculator add")
    void step1_add() {
        assertEquals(0, Mod07Homework.add(""));
        assertEquals(1, Mod07Homework.add("1"));
        assertEquals(3, Mod07Homework.add("1,2"));
        assertEquals(6, Mod07Homework.add("1\n2,3"));
    }

    @Test
    @DisplayName("Задание 2: Stack")
    void step2_stack() {
        Stack<Integer> s = new Stack<>();
        assertTrue(s.isEmpty());
        s.push(1);
        s.push(2);
        assertEquals(2, s.size());
        assertEquals(2, s.peek());
        assertEquals(2, s.pop());
        assertEquals(1, s.pop());
        assertThrows(NoSuchElementException.class, s::pop);
    }

    @Test
    @DisplayName("Задание 3: isBalanced")
    void step3_isBalanced() {
        assertTrue(Mod07Homework.isBalanced("()[]{}"));
        assertTrue(Mod07Homework.isBalanced("([{}])"));
        assertFalse(Mod07Homework.isBalanced("(]"));
        assertFalse(Mod07Homework.isBalanced("(()"));
    }

    @Test
    @DisplayName("Задание 4: romanToInt")
    void step4_romanToInt() {
        assertEquals(3, Mod07Homework.romanToInt("III"));
        assertEquals(4, Mod07Homework.romanToInt("IV"));
        assertEquals(1994, Mod07Homework.romanToInt("MCMXCIV"));
    }

    @Test
    @DisplayName("Задание 5: fib")
    void step5_fib() {
        assertEquals(0L, Mod07Homework.fib(0));
        assertEquals(1L, Mod07Homework.fib(1));
        assertEquals(55L, Mod07Homework.fib(10));
        assertThrows(IllegalArgumentException.class, () -> Mod07Homework.fib(-1));
    }
}
