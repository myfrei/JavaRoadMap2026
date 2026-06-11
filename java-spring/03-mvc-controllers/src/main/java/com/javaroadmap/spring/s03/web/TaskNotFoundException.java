package com.javaroadmap.spring.s03.web;

/** Доменная ошибка: в HTTP-статус её переводит GlobalExceptionHandler, а не сервис. */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(long id) {
        super("Задача %d не найдена".formatted(id));
    }
}
