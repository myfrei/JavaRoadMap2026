package com.javaroadmap.spring.s06.homework;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * «Красные» тесты Reactor-каты домашки модуля 6 (без Spring).
 * Менять их не нужно.
 */
class Mod06HomeworkReactorTest {

    private final ReactorKata kata = new ReactorKata();

    @Test
    @DisplayName("Задание 1а: greet")
    void task1a_greet() {
        StepVerifier.create(kata.greet(Mono.just("Нео")))
                .expectNext("Привет, Нео!")
                .verifyComplete();
    }

    @Test
    @DisplayName("Задание 1б: topN")
    void task1b_topN() {
        StepVerifier.create(kata.topN(Flux.just(5, 1, 9, 3, 7), 3))
                .expectNext(9, 7, 5)
                .verifyComplete();
    }

    @Test
    @DisplayName("Задание 1в: sumOfPositive")
    void task1c_sumOfPositive() {
        StepVerifier.create(kata.sumOfPositive(Flux.just(3L, -2L, 7L, -100L)))
                .expectNext(10L)
                .verifyComplete();

        StepVerifier.create(kata.sumOfPositive(Flux.empty()))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Задание 2а: priceOrDefault не пропускает ошибку наружу")
    void task2a_priceOrDefault() {
        Mono<Double> broken = Mono.error(new IllegalStateException("источник упал"));

        StepVerifier.create(kata.priceOrDefault(broken, 42.0))
                .expectNext(42.0)
                .verifyComplete();

        StepVerifier.create(kata.priceOrDefault(Mono.just(7.0), 42.0))
                .expectNext(7.0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Задание 2б: ticker — три тика по расписанию (виртуальное время)")
    void task2b_ticker() {
        StepVerifier.withVirtualTime(() -> kata.ticker(Duration.ofMinutes(10)))
                .expectSubscription()
                .expectNoEvent(Duration.ofMinutes(10))
                .expectNext("tick-1")
                .thenAwait(Duration.ofMinutes(20))
                .expectNext("tick-2", "tick-3")
                .verifyComplete();
    }
}
