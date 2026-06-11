package com.javaroadmap.spring.s07.sync;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import org.junit.jupiter.api.Test;

/**
 * Синхронизация под настоящей контенцией (статья 02): старт всех потоков —
 * по латчу одновременно, никакого sleep. UnsafeCounter здесь сознательно
 * не тестируется на «потерю» инкрементов: гонка недетерминирована,
 * и такой тест был бы флаки — его место в статье и в Main-демо.
 */
class CountersTest {

    private static final int THREADS = 8;
    private static final int INCREMENTS = 25_000;

    @Test
    void synchronizedCounterSurvivesContention() throws InterruptedException {
        var counter = new Counters.SynchronizedCounter();

        hammer(counter::increment);

        assertThat(counter.value()).isEqualTo((long) THREADS * INCREMENTS);
    }

    @Test
    void atomicCounterSurvivesContention() throws InterruptedException {
        var counter = new Counters.AtomicCounter();

        hammer(counter::increment);

        assertThat(counter.value()).isEqualTo((long) THREADS * INCREMENTS);
    }

    /** THREADS потоков бьют по счётчику одновременно: общий старт по латчу. */
    private void hammer(Runnable increment) throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);
        try (ExecutorService pool = Executors.newFixedThreadPool(THREADS)) {
            for (int t = 0; t < THREADS; t++) {
                pool.submit(() -> {
                    try {
                        startSignal.await();
                        for (int i = 0; i < INCREMENTS; i++) {
                            increment.run();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            startSignal.countDown();
            assertThat(done.await(30, TimeUnit.SECONDS)).as("потоки завершились").isTrue();
        }
    }
}
