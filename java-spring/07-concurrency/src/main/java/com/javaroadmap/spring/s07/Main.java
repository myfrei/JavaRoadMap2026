package com.javaroadmap.spring.s07;

import com.javaroadmap.spring.s07.executors.ParallelSummer;
import java.util.stream.LongStream;

/**
 * Точка входа модуля 7 — «Многопоточность».
 * Главное содержимое — классы примеров и их тесты (src/test).
 *
 * <p>Запуск: {@code ./gradlew :java-spring:07-concurrency:run}
 */
public final class Main {

    public static void main(String[] args) throws Exception {
        long[] numbers = LongStream.rangeClosed(1, 1_000_000).toArray();
        var summer = new ParallelSummer();

        System.out.println("Сумма 1..1_000_000 (ExecutorService): " + summer.sumWithExecutor(numbers, 8));
        System.out.println("Сумма 1..1_000_000 (ForkJoin):        " + summer.sumWithForkJoin(numbers));
        System.out.println("Текущий поток: " + Thread.currentThread());
    }

    private Main() {
    }
}
