package com.javaroadmap.spring.s07.problems;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/** Типовые проблемы (статья 06): настоящий детектируемый дедлок и его лекарство. */
class ConcurrencyProblemsTest {

    @Test
    void crossOrderLockingProducesRealDeadlock() throws InterruptedException {
        Object lockA = new Object();
        Object lockB = new Object();
        CountDownLatch bothAcquiredFirst = new CountDownLatch(2);
        CountDownLatch crossSignal = new CountDownLatch(1);

        // Поток 1: A -> B; поток 2: B -> A. Латчи гарантируют крест.
        Thread t1 = DeadlockDemo.lockThenCross(lockA, lockB, bothAcquiredFirst, crossSignal);
        Thread t2 = DeadlockDemo.lockThenCross(lockB, lockA, bothAcquiredFirst, crossSignal);
        assertThat(bothAcquiredFirst.await(5, TimeUnit.SECONDS)).isTrue();
        crossSignal.countDown(); // оба идут за вторым замком — и встают навсегда

        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        long[] deadlocked = waitForDeadlock(threads);

        assertThat(deadlocked).as("JVM видит цикл ожидания мониторов").isNotNull();
        assertThat(deadlocked).contains(t1.threadId(), t2.threadId());
        // снять дедлок невозможно — потоки демонические и умрут вместе с JVM
    }

    @Test
    void orderedLockingPreventsDeadlock() throws InterruptedException {
        Account a = new Account(1, 10_000);
        Account b = new Account(2, 10_000);
        long totalBefore = a.balanceCents() + b.balanceCents();

        // Переводы в обе стороны одновременно: без порядка замков здесь был бы дедлок.
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < 4; i++) {
                pool.submit(() -> repeat(() -> Account.transfer(a, b, 5)));
                pool.submit(() -> repeat(() -> Account.transfer(b, a, 5)));
            }
            pool.shutdown();
            assertThat(pool.awaitTermination(30, TimeUnit.SECONDS))
                    .as("все переводы завершились — дедлока нет")
                    .isTrue();
        }

        assertThat(a.balanceCents() + b.balanceCents())
                .as("деньги не появились и не исчезли")
                .isEqualTo(totalBefore);
    }

    private static void repeat(Runnable action) {
        for (int i = 0; i < 2_000; i++) {
            action.run();
        }
    }

    private static long[] waitForDeadlock(ThreadMXBean threads) throws InterruptedException {
        for (int attempt = 0; attempt < 100; attempt++) {
            long[] ids = threads.findMonitorDeadlockedThreads();
            if (ids != null && ids.length >= 2) {
                return ids;
            }
            Thread.sleep(50); // опрос JVM-детектора, не синхронизация потоков
        }
        return null;
    }
}
