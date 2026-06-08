package com.javaroadmap.m05.homework;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Домашка модуля 05 — «Базы данных» (без реальной БД: repository-паттерн, пагинация, SQL-строки).
 * Проверка: {@code ./gradlew :modules:m05-databases:homeworkTest}
 */
public final class Mod05Homework {

    private Mod05Homework() {
    }

    /** Задание 2: страница списка (page — с нуля). pageOf([1..10],1,3) -> [4,5,6]. */
    public static <T> List<T> pageOf(List<T> items, int page, int size) {
        throw new UnsupportedOperationException("TODO задание 2: pageOf");
    }

    /** Задание 3: SELECT-запрос. cols пуст -> "SELECT * FROM t". */
    public static String buildSelect(String table, List<String> cols) {
        throw new UnsupportedOperationException("TODO задание 3: buildSelect");
    }

    /** Задание 4: n наибольших по убыванию. */
    public static List<Integer> topN(List<Integer> xs, int n) {
        throw new UnsupportedOperationException("TODO задание 4: topN");
    }

    /** Задание 5: GROUP BY COUNT — частоты значений. */
    public static Map<String, Long> groupCount(List<String> values) {
        throw new UnsupportedOperationException("TODO задание 5: groupCount");
    }
}

/** Задание 1: простой in-memory репозиторий (CRUD). */
class InMemoryRepository<K, V> {

    void save(K id, V entity) {
        throw new UnsupportedOperationException("TODO задание 1: save");
    }

    Optional<V> findById(K id) {
        throw new UnsupportedOperationException("TODO задание 1: findById");
    }

    List<V> findAll() {
        throw new UnsupportedOperationException("TODO задание 1: findAll");
    }

    void deleteById(K id) {
        throw new UnsupportedOperationException("TODO задание 1: deleteById");
    }

    int count() {
        throw new UnsupportedOperationException("TODO задание 1: count");
    }
}
