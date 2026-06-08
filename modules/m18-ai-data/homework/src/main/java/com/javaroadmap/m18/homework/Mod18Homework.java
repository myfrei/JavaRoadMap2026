package com.javaroadmap.m18.homework;

import java.util.List;
import java.util.Map;

/**
 * Домашка модуля 18 — «AI и данные» (векторы, эмбеддинги, retrieval).
 * Проверка: {@code ./gradlew :modules:m18-ai-data:homeworkTest}
 */
public final class Mod18Homework {

    private Mod18Homework() {
    }

    /** Задание 1: скалярное произведение векторов одинаковой длины. */
    public static double dotProduct(double[] a, double[] b) {
        throw new UnsupportedOperationException("TODO задание 1: dotProduct");
    }

    /** Задание 2: косинусная близость: dot(a,b) / (|a| * |b|). */
    public static double cosineSimilarity(double[] a, double[] b) {
        throw new UnsupportedOperationException("TODO задание 2: cosineSimilarity");
    }

    /** Задание 3: частоты термов: слова в нижнем регистре -> счётчики. */
    public static Map<String, Integer> termFrequencies(String text) {
        throw new UnsupportedOperationException("TODO задание 3: termFrequencies");
    }

    /** Задание 4: топ-k ключей по убыванию score (тай-брейк — по ключу по возрастанию). */
    public static List<String> topKByScore(Map<String, Double> scores, int k) {
        throw new UnsupportedOperationException("TODO задание 4: topKByScore");
    }

    /** Задание 5: L2-нормализация вектора (вернуть новый массив). */
    public static double[] normalize(double[] v) {
        throw new UnsupportedOperationException("TODO задание 5: normalize");
    }
}
