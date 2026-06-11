package com.javaroadmap.spring.s01.homework;

/**
 * Задание 5 (часть 3). Черновик-буфер: каждый, кто запрашивает его
 * у контейнера, должен получать НОВЫЙ экземпляр. Подбери scope.
 */
public class ScratchBuffer {

    private final StringBuilder content = new StringBuilder();

    public ScratchBuffer append(String text) {
        content.append(text);
        return this;
    }

    public String content() {
        return content.toString();
    }
}
