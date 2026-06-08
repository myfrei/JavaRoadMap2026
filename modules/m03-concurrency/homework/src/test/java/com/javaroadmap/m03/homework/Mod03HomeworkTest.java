package com.javaroadmap.m03.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 03 — Конкурентность")
class Mod03HomeworkTest {

    @Test
    @DisplayName("Задание 1: sumConcurrently")
    void step1_sumConcurrently() {
        int[] xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertEquals(55L, Mod03Homework.sumConcurrently(xs, 4));
    }

    @Test
    @DisplayName("Задание 2: atomicCount (без гонки)")
    void step2_atomicCount() {
        assertEquals(8000, Mod03Homework.atomicCount(8, 1000));
    }

    @Test
    @DisplayName("Задание 3: parallelSquares (порядок сохранён)")
    void step3_parallelSquares() {
        assertEquals(List.of(1, 4, 9, 16), Mod03Homework.parallelSquares(List.of(1, 2, 3, 4)));
    }

    @Test
    @DisplayName("Задание 4: countPrimes")
    void step4_countPrimes() {
        assertEquals(5L, Mod03Homework.countPrimes(List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
    }

    @Test
    @DisplayName("Задание 5: produceConsumeSum")
    void step5_produceConsumeSum() {
        assertEquals(5050, Mod03Homework.produceConsumeSum(100));
    }
}
