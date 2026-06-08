package com.javaroadmap.m13.homework;

import java.util.List;

/**
 * Домашка модуля 13 — «Внутренности JVM» (биты, выравнивание, UTF-8 — то, что под капотом).
 * Проверка: {@code ./gradlew :modules:m13-jvm-internals:homeworkTest}
 */
public final class Mod13Homework {

    private Mod13Homework() {
    }

    /**
     * Задание 1: декодировать access-flags в имена модификаторов (в порядке битов):
     * PUBLIC=0x1, PRIVATE=0x2, PROTECTED=0x4, STATIC=0x8, FINAL=0x10.
     */
    public static List<String> accessModifiers(int flags) {
        throw new UnsupportedOperationException("TODO задание 1: accessModifiers");
    }

    /** Задание 2: округлить размер вверх до кратного 8 (выравнивание объекта). */
    public static int align8(int size) {
        throw new UnsupportedOperationException("TODO задание 2: align8");
    }

    /** Задание 3: число установленных битов (population count). */
    public static int popcount(long x) {
        throw new UnsupportedOperationException("TODO задание 3: popcount");
    }

    /** Задание 4: степень ли двойки (x > 0 и только один бит). */
    public static boolean isPowerOfTwo(long x) {
        throw new UnsupportedOperationException("TODO задание 4: isPowerOfTwo");
    }

    /**
     * Задание 5: длина UTF-8 последовательности по ведущему байту:
     * 0xxxxxxx -> 1, 110xxxxx -> 2, 1110xxxx -> 3, 11110xxx -> 4; иначе IllegalArgumentException.
     */
    public static int utf8Length(int firstByte) {
        throw new UnsupportedOperationException("TODO задание 5: utf8Length");
    }
}
