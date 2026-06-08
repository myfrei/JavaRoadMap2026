package com.javaroadmap.m04.homework;

import java.time.LocalDate;
import java.util.List;

/**
 * Домашка модуля 04 — «Core APIs» (java.time, regex, строки).
 * Проверка: {@code ./gradlew :modules:m04-core-apis:homeworkTest}
 */
public final class Mod04Homework {

    private Mod04Homework() {
    }

    /** Задание 1: число дней между двумя датами (b - a). */
    public static long daysBetween(LocalDate a, LocalDate b) {
        throw new UnsupportedOperationException("TODO задание 1: daysBetween");
    }

    /** Задание 2: выходной ли день (суббота или воскресенье). */
    public static boolean isWeekend(LocalDate d) {
        throw new UnsupportedOperationException("TODO задание 2: isWeekend");
    }

    /** Задание 3: извлечь все e-mail из текста (регулярное выражение), в порядке появления. */
    public static List<String> extractEmails(String text) {
        throw new UnsupportedOperationException("TODO задание 3: extractEmails");
    }

    /** Задание 4: slug: нижний регистр, неалфанумерики -> '-', без ведущих/хвостовых дефисов. */
    public static String slugify(String s) {
        throw new UnsupportedOperationException("TODO задание 4: slugify");
    }

    /** Задание 5: секунды -> "H:MM:SS" (часы без ведущего нуля, минуты и секунды двузначные). */
    public static String secondsToClock(long totalSeconds) {
        throw new UnsupportedOperationException("TODO задание 5: secondsToClock");
    }
}
