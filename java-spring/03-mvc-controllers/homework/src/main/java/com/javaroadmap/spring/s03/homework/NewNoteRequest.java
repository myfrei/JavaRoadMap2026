package com.javaroadmap.spring.s03.homework;

/**
 * Задание 2. Тело POST-запроса. Добавь validation-аннотации:
 * text не пустой (сообщение: "text не должен быть пустым")
 * и не длиннее 140 символов (сообщение: "text длиннее 140 символов").
 */
public record NewNoteRequest(String text) {
}
