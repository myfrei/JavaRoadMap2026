package com.javaroadmap.m06.homework;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Домашка модуля 06 — «Web и API» (без Spring: логика веб-слоя).
 * Проверка: {@code ./gradlew :modules:m06-web-api:homeworkTest}
 */
public final class Mod06Homework {

    private Mod06Homework() {
    }

    /** Задание 1: разобрать query-строку "a=1&b=2" -> {a=1, b=2}; пустая -> {}. */
    public static Map<String, String> parseQuery(String query) {
        throw new UnsupportedOperationException("TODO задание 1: parseQuery");
    }

    /** Задание 2: матчинг маршрута. "/users/{id}" + "/users/42" -> {id=42}; не совпало -> empty. */
    public static Optional<Map<String, String>> matchRoute(String pattern, String path) {
        throw new UnsupportedOperationException("TODO задание 2: matchRoute");
    }

    /** Задание 3: текст HTTP-статуса: 200 OK, 201 Created, 404 Not Found, 500 Internal Server Error, иначе Unknown. */
    public static String statusText(int code) {
        throw new UnsupportedOperationException("TODO задание 3: statusText");
    }

    /** Задание 4: валидация: пустое имя -> "name is required"; age<0 -> "age must be >= 0". */
    public static List<String> validate(String name, int age) {
        throw new UnsupportedOperationException("TODO задание 4: validate");
    }

    /** Задание 5: собрать URL: base + "?k=v&..." (ключи отсортированы); пустые params -> base. */
    public static String buildUrl(String base, Map<String, String> params) {
        throw new UnsupportedOperationException("TODO задание 5: buildUrl");
    }
}
