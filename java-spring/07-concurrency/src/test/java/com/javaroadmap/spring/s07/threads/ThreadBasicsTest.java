package com.javaroadmap.spring.s07.threads;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/** Thread/Runnable/Callable (статья 01). */
class ThreadBasicsTest {

    @Test
    void runnableExecutesInSeparateThread() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();

        ThreadBasics.runAndJoin(() -> threadName.set(Thread.currentThread().getName()));

        assertThat(threadName.get())
                .isEqualTo("s07-worker")
                .isNotEqualTo(Thread.currentThread().getName());
    }

    @Test
    void callableReturnsValueFromAnotherThread() throws Exception {
        int result = ThreadBasics.callInThread(() -> 6 * 7);

        assertThat(result).isEqualTo(42);
    }

    @Test
    void callableExceptionTravelsBackThroughFuture() {
        assertThatExceptionOfType(ExecutionException.class)
                .isThrownBy(() -> ThreadBasics.callInThread(() -> {
                    throw new IllegalStateException("сломалось в другом потоке");
                }))
                .withCauseInstanceOf(IllegalStateException.class);
    }
}
