package com.javaroadmap.spring.s06.homework;

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Задания 1–2. Операторы Reactor: собери конвейеры. Блокироваться
 * (block/toIterable) НЕЛЬЗЯ — только операторы.
 */
public class ReactorKata {

    /** Задание 1а. Имя -> "Привет, <Имя>!" (map). */
    public Mono<String> greet(Mono<String> name) {
        throw new UnsupportedOperationException("TODO: задание 1а");
    }

    /** Задание 1б. Топ-N максимальных чисел по убыванию (sort + take). */
    public Flux<Integer> topN(Flux<Integer> numbers, int n) {
        throw new UnsupportedOperationException("TODO: задание 1б");
    }

    /** Задание 1в. Сумма только положительных чисел; пустой поток -> 0 (filter + reduce). */
    public Mono<Long> sumOfPositive(Flux<Long> numbers) {
        throw new UnsupportedOperationException("TODO: задание 1в");
    }

    /** Задание 2а. Ошибка источника -> запасное значение (onErrorReturn). */
    public Mono<Double> priceOrDefault(Mono<Double> price, double fallback) {
        throw new UnsupportedOperationException("TODO: задание 2а");
    }

    /**
     * Задание 2б. Тикер: каждые period эмитит "tick-1", "tick-2", "tick-3" и завершается
     * (interval + map + take). Тест проверит его виртуальным временем — мгновенно.
     */
    public Flux<String> ticker(Duration period) {
        throw new UnsupportedOperationException("TODO: задание 2б");
    }
}
