package com.javaroadmap.spring.s04.homework;

/**
 * Задание 3. Сделай класс бином и закрой exportAll() метод-секьюрити:
 * вызвать его должен только пользователь с ролью ADMIN — независимо от того,
 * каким путём дошёл вызов (HTTP, шедулер, другой сервис).
 */
public class HwReportService {

    public String exportAll() {
        return "Выгрузка всех данных";
    }
}
