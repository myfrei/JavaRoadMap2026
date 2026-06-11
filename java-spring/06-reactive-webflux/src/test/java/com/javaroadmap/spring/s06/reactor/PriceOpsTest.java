package com.javaroadmap.spring.s06.reactor;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * StepVerifier (статьи 02, 06): тест описывает ОЖИДАЕМЫЙ сценарий сигналов —
 * элементы, ошибки, завершение — и проигрывает его по requestam.
 */
class PriceOpsTest {

    private final PriceOps ops = new PriceOps();

    @Test
    void movingAverageOverWindowOfThree() {
        Flux<Double> prices = Flux.just(1.0, 2.0, 3.0, 4.0, 5.0);

        StepVerifier.create(ops.movingAverage(prices, 3))
                .expectNext(2.0, 3.0, 4.0)
                .verifyComplete();
    }

    @Test
    void spikesAreDropped() {
        Flux<Double> prices = Flux.just(100.0, 102.0, 250.0, 104.0);

        StepVerifier.create(ops.dropSpikes(prices, 50.0))
                .expectNext(100.0, 102.0, 104.0)
                .verifyComplete();
    }

    @Test
    void errorIsReplacedByFallback() {
        Mono<Double> broken = Mono.error(new IllegalStateException("биржа недоступна"));

        StepVerifier.create(ops.latestPriceOrFallback(broken, 99.9))
                .expectNext(99.9)
                .verifyComplete();
    }

    @Test
    void silenceLongerThanTimeoutFails() {
        // Виртуальное время (статья 06): час «ожидания» проходит мгновенно.
        StepVerifier.withVirtualTime(() ->
                        ops.withTimeout(Flux.never(), Duration.ofSeconds(5)))
                .expectSubscription()
                .thenAwait(Duration.ofHours(1))
                .expectError(TimeoutException.class)
                .verify();
    }
}
