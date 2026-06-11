package com.javaroadmap.spring.s06.reactor;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Операторы Reactor на примере потока цен (статья 02).
 * Ничего не выполняется до подписки — методы лишь СОБИРАЮТ конвейер.
 */
@Component
public class PriceOps {

    /** Скользящее среднее: окно из window последних цен (buffer + avg). */
    public Flux<Double> movingAverage(Flux<Double> prices, int window) {
        return prices.buffer(window, 1)
                .filter(batch -> batch.size() == window)
                .map(batch -> batch.stream().mapToDouble(Double::doubleValue).average().orElse(0));
    }

    /**
     * Фильтр выбросов: цена дальше maxJump от последней принятой отбрасывается.
     * defer даёт КАЖДОЙ подписке своё состояние — шарить его между подписками нельзя.
     */
    public Flux<Double> dropSpikes(Flux<Double> prices, double maxJump) {
        return Flux.defer(() -> {
            AtomicReference<Double> lastAccepted = new AtomicReference<>();
            return prices.filter(price -> {
                Double prev = lastAccepted.get();
                if (prev != null && Math.abs(price - prev) > maxJump) {
                    return false;
                }
                lastAccepted.set(price);
                return true;
            });
        });
    }

    /** Ошибка источника не валит поток — подставляется запасная цена. */
    public Mono<Double> latestPriceOrFallback(Mono<Double> source, double fallback) {
        return source.onErrorReturn(fallback);
    }

    /** Котировки с таймаутом: источник молчит дольше timeout — даём ошибку TimeoutException. */
    public Flux<Double> withTimeout(Flux<Double> prices, Duration timeout) {
        return prices.timeout(timeout);
    }

    /** Backpressure (статья 05): медленный подписчик получает только последние значения. */
    public Flux<Double> latestOnly(Flux<Double> prices) {
        return prices.onBackpressureLatest();
    }
}
