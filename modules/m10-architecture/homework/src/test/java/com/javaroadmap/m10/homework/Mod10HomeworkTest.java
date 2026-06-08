package com.javaroadmap.m10.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 10 — Архитектура")
class Mod10HomeworkTest {

    @Test
    @DisplayName("Задание 1: EventBus")
    void step1_eventBus() {
        EventBus<String> bus = new EventBus<>();
        List<String> seen = new ArrayList<>();
        bus.subscribe(seen::add);
        bus.subscribe(seen::add);
        assertEquals(2, bus.subscriberCount());
        bus.publish("x");
        assertEquals(List.of("x", "x"), seen);
    }

    @Test
    @DisplayName("Задание 2: priceAfter")
    void step2_priceAfter() {
        assertEquals(100.0, Mod10Homework.priceAfter(100, DiscountType.NONE), 1e-9);
        assertEquals(90.0, Mod10Homework.priceAfter(100, DiscountType.PERCENT_10), 1e-9);
        assertEquals(50.0, Mod10Homework.priceAfter(100, DiscountType.HALF), 1e-9);
    }

    @Test
    @DisplayName("Задание 3: pipeline")
    void step3_pipeline() {
        assertEquals(8, Mod10Homework.pipeline(3, List.of(x -> x + 1, x -> x * 2)));
    }

    @Test
    @DisplayName("Задание 4: firstMatch")
    void step4_firstMatch() {
        assertEquals(2, Mod10Homework.firstMatch(List.of(1, 2, 3, 4), x -> x % 2 == 0).orElseThrow());
        assertTrue(Mod10Homework.firstMatch(List.of(1, 3), x -> x % 2 == 0).isEmpty());
    }

    @Test
    @DisplayName("Задание 5: countBy")
    void step5_countBy() {
        var byLen = Mod10Homework.countBy(List.of("a", "bb", "cc", "d"), String::length);
        assertEquals(2L, byLen.get(1));
        assertEquals(2L, byLen.get(2));
    }
}
