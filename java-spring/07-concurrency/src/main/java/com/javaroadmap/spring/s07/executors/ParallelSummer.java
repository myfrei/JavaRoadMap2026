package com.javaroadmap.spring.s07.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * ExecutorService vs ForkJoin (статья 03) на одной задаче — сумме массива.
 *
 * <p>ExecutorService: ты сам режешь работу на куски и раздаёшь.
 * ForkJoin: задача рекурсивно делит себя сама, свободные потоки
 * подворовывают чужие куски (work stealing).
 */
public class ParallelSummer {

    /** Ручная нарезка: parts кусков → invokeAll → сложить результаты. */
    public long sumWithExecutor(long[] numbers, int parts) throws InterruptedException, ExecutionException {
        try (ExecutorService pool = Executors.newFixedThreadPool(parts)) {
            int chunk = Math.ceilDiv(numbers.length, parts);
            List<Callable<Long>> tasks = new ArrayList<>();
            for (int from = 0; from < numbers.length; from += chunk) {
                int start = from;
                int end = Math.min(from + chunk, numbers.length);
                tasks.add(() -> {
                    long sum = 0;
                    for (int i = start; i < end; i++) {
                        sum += numbers[i];
                    }
                    return sum;
                });
            }

            long total = 0;
            for (Future<Long> part : pool.invokeAll(tasks)) {
                total += part.get();
            }
            return total;
        }
    }

    /** Рекурсивное деление: fork() — отдать половину пулу, compute() — досчитать свою. */
    public long sumWithForkJoin(long[] numbers) {
        return ForkJoinPool.commonPool().invoke(new SumTask(numbers, 0, numbers.length));
    }

    private static final class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;

        private final long[] numbers;
        private final int from;
        private final int to;

        SumTask(long[] numbers, int from, int to) {
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute() {
            if (to - from <= THRESHOLD) {
                long sum = 0;
                for (int i = from; i < to; i++) {
                    sum += numbers[i];
                }
                return sum;
            }
            int mid = (from + to) >>> 1;
            SumTask left = new SumTask(numbers, from, mid);
            SumTask right = new SumTask(numbers, mid, to);
            left.fork();                       // левая половина — в очередь пула
            return right.compute() + left.join(); // правую считаем сами
        }
    }
}
