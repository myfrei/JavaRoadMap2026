package com.javaroadmap.spring.s03.homework;

/** Доменная ошибка — менять не нужно. В 404 её переводит твой advice (задание 4). */
public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(long id) {
        super("Заметка %d не найдена".formatted(id));
    }
}
