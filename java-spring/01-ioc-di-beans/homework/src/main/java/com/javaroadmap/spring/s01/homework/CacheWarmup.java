package com.javaroadmap.spring.s01.homework;

/**
 * Задание 2. Сделай класс бином и повесь lifecycle-колбэки:
 * при старте контекста кэш «прогревается», при закрытии — гасится.
 */
public class CacheWarmup {

    private boolean warm;
    private boolean shutDown;

    // TODO: задание 2 — этот метод должен выполниться ПОСЛЕ создания бина (warm = true)
    void warmUp() {
        throw new UnsupportedOperationException("TODO: задание 2");
    }

    // TODO: задание 2 — этот метод должен выполниться при закрытии контекста (warm = false, shutDown = true)
    void shutdown() {
        throw new UnsupportedOperationException("TODO: задание 2");
    }

    public boolean isWarm() {
        return warm;
    }

    public boolean isShutDown() {
        return shutDown;
    }
}
