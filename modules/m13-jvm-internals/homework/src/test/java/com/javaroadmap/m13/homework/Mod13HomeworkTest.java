package com.javaroadmap.m13.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 13 — Внутренности JVM")
class Mod13HomeworkTest {

    @Test
    @DisplayName("Задание 1: accessModifiers")
    void step1_accessModifiers() {
        assertEquals(List.of("PUBLIC", "STATIC"), Mod13Homework.accessModifiers(0x09));
        assertEquals(List.of(), Mod13Homework.accessModifiers(0));
    }

    @Test
    @DisplayName("Задание 2: align8")
    void step2_align8() {
        assertEquals(0, Mod13Homework.align8(0));
        assertEquals(8, Mod13Homework.align8(1));
        assertEquals(8, Mod13Homework.align8(8));
        assertEquals(16, Mod13Homework.align8(9));
    }

    @Test
    @DisplayName("Задание 3: popcount")
    void step3_popcount() {
        assertEquals(0, Mod13Homework.popcount(0));
        assertEquals(3, Mod13Homework.popcount(7));
        assertEquals(1, Mod13Homework.popcount(8));
    }

    @Test
    @DisplayName("Задание 4: isPowerOfTwo")
    void step4_isPowerOfTwo() {
        assertTrue(Mod13Homework.isPowerOfTwo(1));
        assertTrue(Mod13Homework.isPowerOfTwo(1024));
        assertFalse(Mod13Homework.isPowerOfTwo(0));
        assertFalse(Mod13Homework.isPowerOfTwo(6));
    }

    @Test
    @DisplayName("Задание 5: utf8Length")
    void step5_utf8Length() {
        assertEquals(1, Mod13Homework.utf8Length(0x41));
        assertEquals(2, Mod13Homework.utf8Length(0xC3));
        assertEquals(3, Mod13Homework.utf8Length(0xE2));
        assertEquals(4, Mod13Homework.utf8Length(0xF0));
        assertThrows(IllegalArgumentException.class, () -> Mod13Homework.utf8Length(0x80));
    }
}
