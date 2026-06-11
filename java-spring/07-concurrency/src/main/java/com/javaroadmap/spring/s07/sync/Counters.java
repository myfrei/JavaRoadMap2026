package com.javaroadmap.spring.s07.sync;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Три счётчика (статьи 02 и 06): гонка и два способа её убрать.
 * count++ — это ТРИ операции (прочитать, прибавить, записать),
 * и между ними может вклиниться другой поток.
 */
public final class Counters {

    /** Сломан под нагрузкой: теряет инкременты (lost update). Не используй. */
    public static class UnsafeCounter {
        private long count;

        public void increment() {
            count++; // гонка: read-modify-write без синхронизации
        }

        public long value() {
            return count;
        }
    }

    /** Взаимное исключение: в increment() заходит один поток за раз. */
    public static class SynchronizedCounter {
        private long count;

        public synchronized void increment() {
            count++;
        }

        public synchronized long value() {
            return count;
        }
    }

    /** Без блокировок: CAS-инструкция процессора. Быстрее под низкой контенцией. */
    public static class AtomicCounter {
        private final AtomicLong count = new AtomicLong();

        public void increment() {
            count.incrementAndGet();
        }

        public long value() {
            return count.get();
        }
    }

    private Counters() {
    }
}
