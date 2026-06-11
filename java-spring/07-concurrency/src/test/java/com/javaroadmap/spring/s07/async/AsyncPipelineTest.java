package com.javaroadmap.spring.s07.async;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/** CompletableFuture и виртуальные потоки (статья 04). */
class AsyncPipelineTest {

    private final AsyncPipeline pipeline = new AsyncPipeline();

    @Test
    void independentCallsAreCombined() {
        long total = pipeline.totalPrice(() -> 1000L, () -> 250L).join();

        assertThat(total).isEqualTo(1250L);
    }

    @Test
    void dependentStepsAreComposed() {
        String greeting = pipeline.userGreeting(
                () -> 7L,
                id -> CompletableFuture.completedFuture("user-" + id)).join();

        assertThat(greeting).isEqualTo("Привет, user-7!");
    }

    @Test
    void failureIsReplacedByFallback() {
        long price = pipeline.priceOrDefault(() -> {
            throw new IllegalStateException("прайс-сервис лежит");
        }, 9_999L).join();

        assertThat(price).isEqualTo(9_999L);
    }

    @Test
    void tasksRunOnVirtualThreadsAndKeepOrder() throws InterruptedException {
        List<Callable<String>> tasks = IntStream.rangeClosed(1, 50)
                .<Callable<String>>mapToObj(i -> () ->
                        i + ":" + Thread.currentThread().isVirtual())
                .toList();

        List<String> results = pipeline.runOnVirtualThreads(tasks);

        assertThat(results).hasSize(50);
        // порядок результатов соответствует порядку задач, и все — на виртуальных потоках
        assertThat(results.getFirst()).isEqualTo("1:true");
        assertThat(results.getLast()).isEqualTo("50:true");
        assertThat(results).allMatch(r -> r.endsWith(":true"));
    }
}
