package com.javaroadmap.spring.s03.model;

/** Задача в списке дел — DTO, которое контроллер отдаёт наружу как JSON. */
public record Task(long id, String title, boolean done) {

    public Task complete() {
        return new Task(id, title, true);
    }
}
