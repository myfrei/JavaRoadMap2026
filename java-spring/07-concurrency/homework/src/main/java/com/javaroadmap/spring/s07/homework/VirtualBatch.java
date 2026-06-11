package com.javaroadmap.spring.s07.homework;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Задание 5. Выполни все задачи на ВИРТУАЛЬНЫХ потоках
 * (Executors.newVirtualThreadPerTaskExecutor) и верни результаты
 * в исходном порядке. Образец обработки Future — AsyncPipeline из примеров.
 */
public class VirtualBatch {

    public <T> List<T> runAll(List<Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: задание 5");
    }
}
