package com.javaroadmap.m12.homework;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 12 — Собеседование")
class Mod12HomeworkTest {

    @Test
    @DisplayName("Задание 1: twoSum")
    void step1_twoSum() {
        assertArrayEquals(new int[] {0, 1}, Mod12Homework.twoSum(new int[] {2, 7, 11, 15}, 9));
    }

    @Test
    @DisplayName("Задание 2: isAnagram")
    void step2_isAnagram() {
        assertTrue(Mod12Homework.isAnagram("listen", "silent"));
        assertFalse(Mod12Homework.isAnagram("a", "ab"));
    }

    @Test
    @DisplayName("Задание 3: reverse")
    void step3_reverse() {
        assertEquals(List.of(3, 2, 1), Mod12Homework.reverse(List.of(1, 2, 3)));
    }

    @Test
    @DisplayName("Задание 4: maxSubArray (Kadane)")
    void step4_maxSubArray() {
        assertEquals(6, Mod12Homework.maxSubArray(new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4}));
    }

    @Test
    @DisplayName("Задание 5: firstUniqueChar")
    void step5_firstUniqueChar() {
        assertEquals(4, Mod12Homework.firstUniqueChar("aabbcdd"));
        assertEquals(-1, Mod12Homework.firstUniqueChar("aabb"));
    }
}
