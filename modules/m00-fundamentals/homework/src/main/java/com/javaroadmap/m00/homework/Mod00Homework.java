package com.javaroadmap.m00.homework;

import java.util.Map;

/**
 * Домашка модуля 00 — «Фундамент».
 *
 * <p>Реализуй методы (убери {@code UnsupportedOperationException}), сделав тесты зелёными.
 * Пошаговое описание — в {@code homework/README.md}.
 *
 * <p>Проверка: {@code ./gradlew :modules:m00-fundamentals:homeworkTest}
 */
public final class Mod00Homework {

    private Mod00Homework() {
    }

    /** Задание 1: вернуть строку, перевёрнутую посимвольно. */
    public static String reverse(String s) {
        throw new UnsupportedOperationException("TODO задание 1: reverse");
    }

    /** Задание 2: число непустых строк (строка из одних пробелов считается пустой). */
    public static int countNonEmptyLines(String text) {
        throw new UnsupportedOperationException("TODO задание 2: countNonEmptyLines");
    }

    /** Задание 3: число слов в строке (разделители — пробелы; лишние пробелы игнорируются). */
    public static int wordCount(String line) {
        throw new UnsupportedOperationException("TODO задание 3: wordCount");
    }

    /** Задание 4: частоты слов без учёта регистра; слово — последовательность букв и цифр. */
    public static Map<String, Integer> wordFrequencies(String text) {
        throw new UnsupportedOperationException("TODO задание 4: wordFrequencies");
    }

    /** Задание 5: разобрать аргументы вида {@code --key=value} и {@code --key value}; одиночный флаг -> "true". */
    public static Map<String, String> parseArgs(String[] args) {
        throw new UnsupportedOperationException("TODO задание 5: parseArgs");
    }

    /** Задание 6: палиндром ли строка без учёта регистра и неалфанумерических символов. */
    public static boolean isPalindrome(String s) {
        throw new UnsupportedOperationException("TODO задание 6: isPalindrome");
    }
}
