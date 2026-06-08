package com.javaroadmap.m01.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 01 — Синтаксис и ООП")
class Mod01HomeworkTest {

    @Test
    @DisplayName("Задание 1: fizzbuzz")
    void step1_fizzbuzz() {
        List<String> r = Mod01Homework.fizzbuzz(15);
        assertEquals(15, r.size());
        assertEquals("1", r.get(0));
        assertEquals("Fizz", r.get(2));
        assertEquals("Buzz", r.get(4));
        assertEquals("FizzBuzz", r.get(14));
    }

    @Test
    @DisplayName("Задание 2: factorial")
    void step2_factorial() {
        assertEquals(1L, Mod01Homework.factorial(0));
        assertEquals(120L, Mod01Homework.factorial(5));
        assertThrows(IllegalArgumentException.class, () -> Mod01Homework.factorial(-1));
    }

    @Test
    @DisplayName("Задание 3: gradeOf")
    void step3_gradeOf() {
        assertEquals('A', Mod01Homework.gradeOf(95));
        assertEquals('C', Mod01Homework.gradeOf(72));
        assertEquals('F', Mod01Homework.gradeOf(40));
    }

    @Test
    @DisplayName("Задание 4: BankAccount")
    void step4_bankAccount() {
        BankAccount acc = new BankAccount(100);
        acc.deposit(50);
        assertEquals(150L, acc.balance());
        acc.withdraw(30);
        assertEquals(120L, acc.balance());
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(1000));
    }

    @Test
    @DisplayName("Задание 5: Direction.turnRight")
    void step5_turnRight() {
        assertEquals(Direction.EAST, Direction.NORTH.turnRight());
        assertEquals(Direction.NORTH, Direction.WEST.turnRight());
    }
}
