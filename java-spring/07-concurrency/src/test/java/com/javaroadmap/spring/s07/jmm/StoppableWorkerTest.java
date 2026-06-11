package com.javaroadmap.spring.s07.jmm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Видимость (статья 05): volatile-флаг гарантированно останавливает цикл.
 * Без volatile этот тест имел бы ПРАВО зависнуть навсегда — JMM не обещает,
 * что поток увидит запись другого потока без happens-before.
 */
class StoppableWorkerTest {

    @Test
    void volatileFlagStopsBusyLoop() throws InterruptedException {
        var worker = new StoppableWorker();
        worker.start();
        worker.awaitStarted(); // ждём входа в цикл — без sleep

        worker.stop();
        worker.join(5_000);

        assertThat(worker.isAlive())
                .as("volatile-запись стала видна циклу, поток завершился")
                .isFalse();
    }
}
