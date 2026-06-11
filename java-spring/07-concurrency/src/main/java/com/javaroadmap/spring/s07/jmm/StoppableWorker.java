package com.javaroadmap.spring.s07.jmm;

import java.util.concurrent.CountDownLatch;

/**
 * Видимость и happens-before (статья 05).
 *
 * <p>Без volatile поток-работник имеет право НИКОГДА не увидеть новое значение
 * running: JIT кэширует поле в регистре, и цикл становится вечным.
 * volatile даёт happens-before: запись из stop() видна чтению в цикле.
 */
public class StoppableWorker {

    private volatile boolean running = true; // убери volatile — и stop() может не сработать

    private final CountDownLatch started = new CountDownLatch(1);
    private final Thread thread = new Thread(this::loop, "s07-stoppable");

    private long iterations;

    private void loop() {
        started.countDown();
        while (running) {
            iterations++; // имитация работы
        }
    }

    public void start() {
        thread.start();
    }

    /** Блокируется, пока работник реально не вошёл в цикл. */
    public void awaitStarted() throws InterruptedException {
        started.await();
    }

    public void stop() {
        running = false; // volatile-запись: happens-before для чтения в loop()
    }

    public void join(long millis) throws InterruptedException {
        thread.join(millis);
    }

    public boolean isAlive() {
        return thread.isAlive();
    }
}
