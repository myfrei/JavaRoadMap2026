package com.javaroadmap.m09.homework;

import java.util.Map;

/**
 * Домашка модуля 09 — «DevOps» (semver, .env, теги образов).
 * Проверка: {@code ./gradlew :modules:m09-devops:homeworkTest}
 */
public final class Mod09Homework {

    private Mod09Homework() {
    }

    /** Задание 1: разобрать "1.2.3" -> [1,2,3]; некорректная строка -> IllegalArgumentException. */
    public static int[] parseSemver(String version) {
        throw new UnsupportedOperationException("TODO задание 1: parseSemver");
    }

    /** Задание 2: сравнить версии: <0, 0, >0. */
    public static int compareSemver(String a, String b) {
        throw new UnsupportedOperationException("TODO задание 2: compareSemver");
    }

    /** Задание 3: разобрать .env: строки KEY=VALUE; пустые строки и комментарии (#) пропустить. */
    public static Map<String, String> parseEnv(String content) {
        throw new UnsupportedOperationException("TODO задание 3: parseEnv");
    }

    /** Задание 4: docker-тег: '/' в ветке -> '-', плюс первые 7 символов sha. */
    public static String dockerTag(String branch, String sha) {
        throw new UnsupportedOperationException("TODO задание 4: dockerTag");
    }

    /** Задание 5: валидное ли имя образа (нижний регистр, цифры, '-', '.', '/', опц. ':tag'). */
    public static boolean isValidImageName(String name) {
        throw new UnsupportedOperationException("TODO задание 5: isValidImageName");
    }
}
