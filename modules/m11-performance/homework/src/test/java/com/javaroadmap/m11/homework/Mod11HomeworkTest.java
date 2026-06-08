package com.javaroadmap.m11.homework;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 11 — Производительность")
class Mod11HomeworkTest {

    @Test
    @DisplayName("Задание 1: sieve")
    void step1_sieve() {
        assertEquals(List.of(2, 3, 5, 7), Mod11Homework.sieve(10));
        assertEquals(List.of(), Mod11Homework.sieve(1));
    }

    @Test
    @DisplayName("Задание 2: LruCache вытесняет LRU")
    void step2_lruCache() {
        LruCache<Integer, String> c = new LruCache<>(2);
        c.put(1, "a");
        c.put(2, "b");
        c.get(1); // 1 становится недавно использованным
        c.put(3, "c"); // вытесняет 2
        assertEquals("a", c.get(1));
        assertNull(c.get(2));
        assertEquals("c", c.get(3));
        assertEquals(2, c.size());
    }

    @Test
    @DisplayName("Задание 3: twoSumIndices")
    void step3_twoSumIndices() {
        assertArrayEquals(new int[] {0, 1}, Mod11Homework.twoSumIndices(new int[] {2, 7, 11, 15}, 9));
    }

    @Test
    @DisplayName("Задание 4: fibFast")
    void step4_fibFast() {
        assertEquals(0L, Mod11Homework.fibFast(0));
        assertEquals(55L, Mod11Homework.fibFast(10));
    }

    @Test
    @DisplayName("Задание 5: dedupPreserveOrder")
    void step5_dedup() {
        assertEquals(List.of(1, 2, 3), Mod11Homework.dedupPreserveOrder(List.of(1, 2, 1, 3, 2)));
    }
}
