package com.javaroadmap.m22.homework;

import java.util.List;

/**
 * Домашка модуля 22 — «PostgreSQL» (advance-трек): индексы и SQL-запросы, собираемые из Java.
 * Проверка: {@code ./gradlew :modules:m22-postgres:homeworkTest}
 */
public final class Mod22Homework {

    private Mod22Homework() {
    }

    /**
     * Задание 1: собрать SELECT из Java. Пустой cols -> "SELECT *"; пустой whereCols -> без WHERE.
     * Пример: buildSelect("users", [id,name], [age,city]) -> "SELECT id, name FROM users WHERE age = ? AND city = ?".
     */
    public static String buildSelect(String table, List<String> cols, List<String> whereCols) {
        throw new UnsupportedOperationException("TODO задание 1: buildSelect");
    }

    /**
     * Задание 2: keyset-пагинация (быстрее OFFSET на больших таблицах):
     * "SELECT * FROM &lt;table&gt; WHERE &lt;key&gt; > ? ORDER BY &lt;key&gt; ASC LIMIT &lt;limit&gt;".
     */
    public static String keysetPage(String table, String keyColumn, int limit) {
        throw new UnsupportedOperationException("TODO задание 2: keysetPage");
    }

    /** Задание 3: DDL составного индекса: "CREATE INDEX idx_&lt;table&gt;_&lt;c1&gt;_&lt;c2&gt; ON &lt;table&gt; (c1, c2)". */
    public static String createIndex(String table, List<String> cols) {
        throw new UnsupportedOperationException("TODO задание 3: createIndex");
    }

    /**
     * Задание 4: sargable ли предикат (может ли использовать индекс).
     * Функция на колонке ("lower(name) = ...") или LIKE с ведущим '%' ("name LIKE '%x'") -> false.
     */
    public static boolean isSargable(String predicate) {
        throw new UnsupportedOperationException("TODO задание 4: isSargable");
    }

    /** Задание 5: порядок колонок составного индекса — сначала равенства (EQ) в исходном порядке, потом диапазоны (RANGE). */
    public static List<String> indexColumnOrder(List<Predicate> predicates) {
        throw new UnsupportedOperationException("TODO задание 5: indexColumnOrder");
    }
}

/** Предикат запроса для задания 5: {@code type} = "EQ" или "RANGE". */
record Predicate(String column, String type) {
}
