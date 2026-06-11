package com.javaroadmap.spring.s07.problems;

import java.util.concurrent.CountDownLatch;

/**
 * Дедлок руками (статья 06): два потока берут те же замки в РАЗНОМ порядке.
 * Латчи делают сценарий детерминированным — оба гарантированно успевают
 * взять первый замок до попытки взять второй.
 */
public final class DeadlockDemo {

    /**
     * Поток: берёт first, сигналит, ждёт отмашки и пытается взять second.
     * Поток демонический: дедлокнутые потоки нельзя остановить — пусть
     * хотя бы не мешают JVM завершиться.
     */
    public static Thread lockThenCross(Object first, Object second,
                                       CountDownLatch firstAcquired, CountDownLatch crossSignal) {
        Thread thread = new Thread(() -> {
            synchronized (first) {
                firstAcquired.countDown();
                try {
                    crossSignal.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                synchronized (second) {
                    // при крест-накрест порядке замков сюда не дойти — deadlock выше
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private DeadlockDemo() {
    }
}
