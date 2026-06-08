package com.javaroadmap.m01.homework;

import java.util.List;

/**
 * Домашка модуля 01 — «Синтаксис и ООП». Реализуй задания; см. {@code homework/README.md}.
 * Проверка: {@code ./gradlew :modules:m01-syntax:homeworkTest}
 */
public final class Mod01Homework {

    private Mod01Homework() {
    }

    /** Задание 1: FizzBuzz для 1..n. Кратные 3 -> "Fizz", 5 -> "Buzz", 15 -> "FizzBuzz", иначе само число. */
    public static List<String> fizzbuzz(int n) {
        throw new UnsupportedOperationException("TODO задание 1: fizzbuzz");
    }

    /** Задание 2: факториал; n < 0 -> IllegalArgumentException; 0! = 1. */
    public static long factorial(int n) {
        throw new UnsupportedOperationException("TODO задание 2: factorial");
    }

    /** Задание 3: буква оценки: >=90 'A', >=80 'B', >=70 'C', >=60 'D', иначе 'F'. */
    public static char gradeOf(int score) {
        throw new UnsupportedOperationException("TODO задание 3: gradeOf");
    }
}

/** Задание 4: банковский счёт. Отрицательная сумма или овердрафт -> IllegalArgumentException. */
class BankAccount {

    BankAccount(long initialBalance) {
        throw new UnsupportedOperationException("TODO задание 4: BankAccount(long)");
    }

    void deposit(long amount) {
        throw new UnsupportedOperationException("TODO задание 4: deposit");
    }

    void withdraw(long amount) {
        throw new UnsupportedOperationException("TODO задание 4: withdraw");
    }

    long balance() {
        throw new UnsupportedOperationException("TODO задание 4: balance");
    }
}

/** Задание 5: стороны света. turnRight() — поворот по часовой стрелке (N->E->S->W->N). */
enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    Direction turnRight() {
        throw new UnsupportedOperationException("TODO задание 5: turnRight");
    }
}
