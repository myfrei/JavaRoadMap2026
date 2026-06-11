package com.javaroadmap.spring.s07.executors;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

/** ExecutorService vs ForkJoin (статья 03): оба дают тот же результат, что и последовательный код. */
class ParallelSummerTest {

    private final ParallelSummer summer = new ParallelSummer();

    private final long[] numbers = LongStream.rangeClosed(1, 100_000).toArray();
    private final long expected = 100_000L * 100_001 / 2;

    @Test
    void executorSumMatchesSequential() throws Exception {
        assertThat(summer.sumWithExecutor(numbers, 7)).isEqualTo(expected);
    }

    @Test
    void executorHandlesPartsLargerThanArray() throws Exception {
        long[] tiny = {1, 2, 3};

        assertThat(summer.sumWithExecutor(tiny, 16)).isEqualTo(6);
    }

    @Test
    void forkJoinSumMatchesSequential() {
        assertThat(summer.sumWithForkJoin(numbers)).isEqualTo(expected);
    }
}
