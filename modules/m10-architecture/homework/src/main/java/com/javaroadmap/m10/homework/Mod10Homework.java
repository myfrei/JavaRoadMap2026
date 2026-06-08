package com.javaroadmap.m10.homework;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

/**
 * Домашка модуля 10 — «Архитектура» (паттерны: observer, strategy, pipeline, specification).
 * Проверка: {@code ./gradlew :modules:m10-architecture:homeworkTest}
 */
public final class Mod10Homework {

    private Mod10Homework() {
    }

    /** Задание 2: стратегия скидки. NONE -> price, PERCENT_10 -> price*0.9, HALF -> price*0.5. */
    public static double priceAfter(double price, DiscountType type) {
        throw new UnsupportedOperationException("TODO задание 2: priceAfter");
    }

    /** Задание 3: pipeline — применить стадии по порядку. */
    public static int pipeline(int input, List<IntUnaryOperator> stages) {
        throw new UnsupportedOperationException("TODO задание 3: pipeline");
    }

    /** Задание 4: первый элемент, удовлетворяющий предикату (specification). */
    public static <T> Optional<T> firstMatch(List<T> items, Predicate<T> predicate) {
        throw new UnsupportedOperationException("TODO задание 4: firstMatch");
    }

    /** Задание 5: группировка с подсчётом по ключу-классификатору. */
    public static <T, K> Map<K, Long> countBy(List<T> items, Function<T, K> classifier) {
        throw new UnsupportedOperationException("TODO задание 5: countBy");
    }
}

/** Тип скидки для задания 2. */
enum DiscountType {
    NONE,
    PERCENT_10,
    HALF
}

/** Задание 1: шина событий (observer). publish вызывает всех подписчиков. */
class EventBus<T> {

    void subscribe(Consumer<T> handler) {
        throw new UnsupportedOperationException("TODO задание 1: subscribe");
    }

    void publish(T event) {
        throw new UnsupportedOperationException("TODO задание 1: publish");
    }

    int subscriberCount() {
        throw new UnsupportedOperationException("TODO задание 1: subscriberCount");
    }
}
