package com.javaroadmap.spring.s07.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Thread, Runnable, Callable (статья 01).
 * Runnable — «сделай и забудь», Callable — «сделай и верни результат/исключение».
 */
public final class ThreadBasics {

    /** Запускает Runnable в новом потоке и ждёт завершения (join). */
    public static void runAndJoin(Runnable task) throws InterruptedException {
        Thread thread = new Thread(task, "s07-worker");
        thread.start();   // не run()! run() выполнился бы в ТЕКУЩЕМ потоке
        thread.join();
    }

    /**
     * Callable возвращает значение — но Thread умеет запускать только Runnable.
     * Мост между ними — FutureTask: он и Runnable, и Future одновременно.
     */
    public static <T> T callInThread(Callable<T> task) throws Exception {
        FutureTask<T> future = new FutureTask<>(task);
        new Thread(future, "s07-callable").start();
        return future.get(); // блокируется до результата или исключения
    }

    private ThreadBasics() {
    }
}
