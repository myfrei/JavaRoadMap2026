package com.javaroadmap.m19.homework;

import java.util.List;

/**
 * Домашка модуля 19 — «Парсинг и компиляторы» (токенизация, скобки, RPN, вычисление).
 * Проверка: {@code ./gradlew :modules:m19-parsing:homeworkTest}
 */
public final class Mod19Homework {

    private Mod19Homework() {
    }

    /** Задание 1: токенизировать выражение: числа (многозначные), + - * / ( ); пробелы пропустить. */
    public static List<String> tokenize(String expr) {
        throw new UnsupportedOperationException("TODO задание 1: tokenize");
    }

    /** Задание 2: сбалансированы ли круглые скобки. */
    public static boolean matchParens(String s) {
        throw new UnsupportedOperationException("TODO задание 2: matchParens");
    }

    /** Задание 3: инфикс -> ОПЗ (алгоритм сортировочной станции), операторы + - * /. */
    public static List<String> toReversePolish(List<String> tokens) {
        throw new UnsupportedOperationException("TODO задание 3: toReversePolish");
    }

    /** Задание 4: вычислить выражение с приоритетами и скобками; вернуть double. */
    public static double evaluate(String expr) {
        throw new UnsupportedOperationException("TODO задание 4: evaluate");
    }

    /** Задание 5: число токенов в выражении (== размеру tokenize). */
    public static int countTokens(String expr) {
        throw new UnsupportedOperationException("TODO задание 5: countTokens");
    }
}
