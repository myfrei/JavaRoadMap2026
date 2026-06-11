package com.javaroadmap.spring.s03.homework;

import java.util.List;

/**
 * Задания 1–3. Преврати класс в REST-контроллер на /api/notes
 * (аннотации на классе и методах) и реализуй методы через NotesService.
 */
public class NotesController {

    private final NotesService notes;

    public NotesController(NotesService notes) {
        this.notes = notes;
    }

    /** Задание 1. GET /api/notes и GET /api/notes?contains=... (фильтр опционален). */
    public List<Note> list(String contains) {
        throw new UnsupportedOperationException("TODO: задание 1");
    }

    /** Задание 1. GET /api/notes/{id}. Нет заметки -> 404 (через advice, задание 4). */
    public Note byId(long id) {
        throw new UnsupportedOperationException("TODO: задание 1");
    }

    /** Задание 2. POST /api/notes: тело валидируется, ответ — 201 Created. */
    public Note create(NewNoteRequest request) {
        throw new UnsupportedOperationException("TODO: задание 2");
    }

    /** Задание 3. DELETE /api/notes/{id}: ответ — 204 No Content. */
    public void delete(long id) {
        throw new UnsupportedOperationException("TODO: задание 3");
    }
}
