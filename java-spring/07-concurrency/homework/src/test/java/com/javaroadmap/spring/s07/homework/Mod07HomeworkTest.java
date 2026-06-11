package com.javaroadmap.spring.s07.homework;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * «Красные» тесты домашки модуля 7. Менять их не нужно.
 * Все тесты детерминированы: общий старт — латчами, никакого sleep.
 */
class Mod07HomeworkTest {

    private static final int THREADS = 8;
    private static final int INCREMENTS = 25_000;

    @Test
    @DisplayName("Задание 1: SafeCounter не теряет инкременты под контенцией")
    void task1_safeCounter() throws InterruptedException {
        var counter = new SafeCounter();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        try (ExecutorService pool = Executors.newFixedThreadPool(THREADS)) {
            for (int t = 0; t < THREADS; t++) {
                pool.submit(() -> {
                    try {
                        start.await();
                        for (int i = 0; i < INCREMENTS; i++) {
                            counter.increment();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            start.countDown();
            assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(counter.value()).isEqualTo((long) THREADS * INCREMENTS);
    }

    @Test
    @DisplayName("Задание 2: ParallelMapper сохраняет порядок результатов")
    void task2_parallelMapper() throws InterruptedException {
        var mapper = new ParallelMapper();
        List<Integer> input = IntStream.rangeClosed(1, 100).boxed().toList();

        List<Integer> squares = mapper.map(input, x -> x * x, 6);

        assertThat(squares).hasSize(100);
        assertThat(squares.getFirst()).isEqualTo(1);
        assertThat(squares.get(9)).isEqualTo(100);
        assertThat(squares.getLast()).isEqualTo(10_000);
    }

    @Test
    @DisplayName("Задание 3а: sumAsync действительно параллелен")
    void task3a_sumAsyncRunsInParallel() {
        var combiner = new AsyncCombiner();
        // Рандеву: каждый supplier ждёт второго. Последовательное выполнение зависло бы.
        CountDownLatch rendezvous = new CountDownLatch(2);

        long sum = combiner.sumAsync(
                () -> meetAndReturn(rendezvous, 40L),
                () -> meetAndReturn(rendezvous, 2L)).join();

        assertThat(sum).isEqualTo(42L);
    }

    @Test
    @DisplayName("Задание 3б: withFallback гасит ошибку")
    void task3b_withFallback() {
        var combiner = new AsyncCombiner();
        CompletableFuture<String> broken = CompletableFuture.failedFuture(new IllegalStateException("упало"));

        assertThat(combiner.withFallback(broken, "запасной").join()).isEqualTo("запасной");
        assertThat(combiner.withFallback(CompletableFuture.completedFuture("основной"), "запасной").join())
                .isEqualTo("основной");
    }

    @Test
    @DisplayName("Задание 4: firstOf возвращает результат первого завершившегося")
    void task4_firstOf() {
        var combiner = new AsyncCombiner();
        CountDownLatch slowGate = new CountDownLatch(1);

        String winner = combiner.firstOf(
                () -> awaitAndReturn(slowGate, "медленный"),
                () -> "быстрый").join();

        slowGate.countDown(); // выпускаем «медленного», чтобы не висел после теста
        assertThat(winner).isEqualTo("быстрый");
    }

    @Test
    @DisplayName("Задание 5: VirtualBatch — виртуальные потоки, порядок сохранён")
    void task5_virtualBatch() throws InterruptedException {
        var batch = new VirtualBatch();
        List<Callable<String>> tasks = IntStream.rangeClosed(1, 100)
                .<Callable<String>>mapToObj(i -> () -> i + ":" + Thread.currentThread().isVirtual())
                .toList();

        List<String> results = batch.runAll(tasks);

        assertThat(results).hasSize(100);
        assertThat(results.getFirst()).isEqualTo("1:true");
        assertThat(results.getLast()).isEqualTo("100:true");
        assertThat(results).allMatch(r -> r.endsWith(":true"));
    }

    private static long meetAndReturn(CountDownLatch rendezvous, long value) {
        rendezvous.countDown();
        try {
            if (!rendezvous.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("второй supplier так и не стартовал — нет параллельности");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        return value;
    }

    private static String awaitAndReturn(CountDownLatch gate, String value) {
        try {
            gate.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return value;
    }
}
