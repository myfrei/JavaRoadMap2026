package com.javaroadmap.spring.s04.homework;

/**
 * Задание 2б. Преврати класс в конфигурацию с собственной security-цепочкой
 * для путей /hw/** (цепочки модуля закрывают только /api/** — смотри
 * SecurityConfig как образец):
 *
 * - securityMatcher("/hw/**") + HTTP Basic;
 * - /hw/public/** — без аутентификации;
 * - /hw/admin/** — только роль ADMIN;
 * - всё остальное в /hw/** — любой аутентифицированный;
 * - csrf отключить.
 *
 * Пользователи уже есть (user/user123, admin/admin123 — бин users в SecurityConfig).
 */
public class HwSecurityConfig {

    // TODO: задание 2б — @Bean SecurityFilterChain
}
