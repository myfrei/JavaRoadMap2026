package com.javaroadmap.spring.s07.homework;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Задания 3–4. Композиция CompletableFuture: параллельное объединение,
 * запасное значение, «кто первый — того и результат».
 */
public class AsyncCombiner {

    /** Задание 3а. Запусти оба supplier-а ПАРАЛЛЕЛЬНО и сложи результаты. */
    public CompletableFuture<Long> sumAsync(Supplier<Long> first, Supplier<Long> second) {
        throw new UnsupportedOperationException("TODO: задание 3а");
    }

    /** Задание 3б. Ошибка future -> запасное значение (исключение наружу не выходит). */
    public <T> CompletableFuture<T> withFallback(CompletableFuture<T> future, T fallback) {
        throw new UnsupportedOperationException("TODO: задание 3б");
    }

    /** Задание 4. Верни результат того supplier-а, который завершится ПЕРВЫМ. */
    public <T> CompletableFuture<T> firstOf(Supplier<T> first, Supplier<T> second) {
        throw new UnsupportedOperationException("TODO: задание 4");
    }
}
