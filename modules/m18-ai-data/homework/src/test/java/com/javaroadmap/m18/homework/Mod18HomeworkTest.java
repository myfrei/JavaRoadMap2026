package com.javaroadmap.m18.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 18 — AI и данные")
class Mod18HomeworkTest {

    @Test
    @DisplayName("Задание 1: dotProduct")
    void step1_dotProduct() {
        assertEquals(32.0, Mod18Homework.dotProduct(new double[] {1, 2, 3}, new double[] {4, 5, 6}), 1e-9);
    }

    @Test
    @DisplayName("Задание 2: cosineSimilarity")
    void step2_cosineSimilarity() {
        assertEquals(1.0, Mod18Homework.cosineSimilarity(new double[] {1, 0}, new double[] {1, 0}), 1e-9);
        assertEquals(0.0, Mod18Homework.cosineSimilarity(new double[] {1, 0}, new double[] {0, 1}), 1e-9);
    }

    @Test
    @DisplayName("Задание 3: termFrequencies")
    void step3_termFrequencies() {
        Map<String, Integer> tf = Mod18Homework.termFrequencies("the cat the dog the");
        assertEquals(3, tf.get("the"));
        assertEquals(1, tf.get("cat"));
        assertEquals(1, tf.get("dog"));
    }

    @Test
    @DisplayName("Задание 4: topKByScore")
    void step4_topKByScore() {
        Map<String, Double> scores = Map.of("a", 0.9, "b", 0.5, "c", 0.7);
        assertEquals(List.of("a", "c"), Mod18Homework.topKByScore(scores, 2));
    }

    @Test
    @DisplayName("Задание 5: normalize (L2)")
    void step5_normalize() {
        double[] r = Mod18Homework.normalize(new double[] {3, 4});
        assertEquals(0.6, r[0], 1e-9);
        assertEquals(0.8, r[1], 1e-9);
    }
}
