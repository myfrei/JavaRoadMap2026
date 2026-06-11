package com.javaroadmap.spring.s06.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

/**
 * Backpressure (статья 05): подписчик сам управляет потоком через request(n).
 * StepVerifier.create(..., 0) — подписка БЕЗ начального спроса.
 */
class BackpressureTest {

    @Test
    void subscriberControlsDemandWithRequest() {
        Flux<Integer> numbers = Flux.range(1, 100);

        StepVerifier.create(numbers, 0)
                .expectSubscription()
                .thenRequest(2)
                .expectNext(1, 2)
                .thenRequest(3)
                .expectNext(3, 4, 5)
                .thenCancel() // остальные 95 элементов даже не будут произведены
                .verify();
    }

    @Test
    void onBackpressureLatestDropsIntermediateValues() {
        PriceOps ops = new PriceOps();
        // Flux.just сам уважает спрос — нужен источник, который пушит без спроса
        // (как живая биржа). TestPublisher.createNoncompliant как раз такой.
        TestPublisher<Double> exchange = TestPublisher.createNoncompliant(
                TestPublisher.Violation.REQUEST_OVERFLOW);

        StepVerifier.create(ops.latestOnly(exchange.flux()), 0)
                .expectSubscription()
                .then(() -> exchange.next(1.0, 2.0, 3.0)) // спроса нет — копятся, выживает последняя
                .thenRequest(1)
                .expectNext(3.0)
                .then(() -> exchange.next(4.0, 5.0))      // снова без спроса
                .then(exchange::complete)
                .thenRequest(10)
                .expectNext(5.0)                           // 4.0 вытеснена
                .verifyComplete();
    }
}
