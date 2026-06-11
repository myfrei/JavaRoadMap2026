package com.javaroadmap.spring.s04.homework;

/**
 * Задание 1. Сделай record типобезопасной конфигурацией с префиксом "hw".
 * Значения уже лежат в application.properties (hw.motd, hw.max-attempts) —
 * @ConfigurationPropertiesScan в Main подхватит класс сам.
 */
public record HwProperties(String motd, int maxAttempts) {
}
