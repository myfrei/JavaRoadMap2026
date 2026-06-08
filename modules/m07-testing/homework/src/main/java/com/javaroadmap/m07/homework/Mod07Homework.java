package com.javaroadmap.m07.homework;

/**
 * Домашка модуля 07 — «Тестирование» (классические TDD-каты).
 * Проверка: {@code ./gradlew :modules:m07-testing:homeworkTest}
 */
public final class Mod07Homework {

    private Mod07Homework() {
    }

    /** Задание 1: String Calculator. "" -> 0, "1" -> 1, "1,2" -> 3, поддержка '\n' как разделителя. */
    public static int add(String numbers) {
        throw new UnsupportedOperationException("TODO задание 1: add");
    }

    /** Задание 3: сбалансированы ли скобки (), [], {}. */
    public static boolean isBalanced(String s) {
        throw new UnsupportedOperationException("TODO задание 3: isBalanced");
    }

    /** Задание 4: римское число в int. "IV" -> 4, "MCMXCIV" -> 1994. */
    public static int romanToInt(String roman) {
        throw new UnsupportedOperationException("TODO задание 4: romanToInt");
    }

    /** Задание 5: число Фибоначчи. fib(0)=0, fib(1)=1; n<0 -> IllegalArgumentException. */
    public static long fib(int n) {
        throw new UnsupportedOperationException("TODO задание 5: fib");
    }
}

/** Задание 2: обобщённый стек. pop()/peek() на пустом -> java.util.NoSuchElementException. */
class Stack<T> {

    void push(T item) {
        throw new UnsupportedOperationException("TODO задание 2: push");
    }

    T pop() {
        throw new UnsupportedOperationException("TODO задание 2: pop");
    }

    T peek() {
        throw new UnsupportedOperationException("TODO задание 2: peek");
    }

    boolean isEmpty() {
        throw new UnsupportedOperationException("TODO задание 2: isEmpty");
    }

    int size() {
        throw new UnsupportedOperationException("TODO задание 2: size");
    }
}
