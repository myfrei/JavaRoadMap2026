package com.javaroadmap.spring.s07.async;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * CompletableFuture и виртуальные потоки (статья 04):
 * композиция асинхронных шагов вместо ручного join-а потоков.
 */
public class AsyncPipeline {

    /** Два независимых вызова идут ПАРАЛЛЕЛЬНО, результаты складывает thenCombine. */
    public CompletableFuture<Long> totalPrice(Supplier<Long> basePrice, Supplier<Long> deliveryPrice) {
        CompletableFuture<Long> base = CompletableFuture.supplyAsync(basePrice);
        CompletableFuture<Long> delivery = CompletableFuture.supplyAsync(deliveryPrice);
        return base.thenCombine(delivery, Long::sum);
    }

    /** Цепочка зависимых шагов: результат первого нужен второму (thenCompose). */
    public CompletableFuture<String> userGreeting(Supplier<Long> userId,
                                                  java.util.function.LongFunction<CompletableFuture<String>> nameById) {
        return CompletableFuture.supplyAsync(userId)
                .thenCompose(id -> nameById.apply(id))
                .thenApply("Привет, %s!"::formatted);
    }

    /** Ошибка любого шага не роняет пайплайн — exceptionally подставляет запасной результат. */
    public CompletableFuture<Long> priceOrDefault(Supplier<Long> price, long fallback) {
        return CompletableFuture.supplyAsync(price)
                .exceptionally(error -> fallback);
    }

    /**
     * Виртуальные потоки (Project Loom): тысячи БЛОКИРУЮЩИХСЯ задач без
     * пула — каждой задаче свой дешёвый поток, паркующийся на I/O.
     */
    public <T> List<T> runOnVirtualThreads(List<Callable<T>> tasks) throws InterruptedException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return executor.invokeAll(tasks).stream()
                    .map(AsyncPipeline::join)
                    .toList();
        }
    }

    private static <T> T join(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Задача прервана", e);
        } catch (java.util.concurrent.ExecutionException e) {
            throw new IllegalStateException("Задача упала", e.getCause());
        }
    }
}
