package com.javaroadmap.spring.s04.homework;

import java.util.Map;
import org.springframework.security.core.Authentication;

/**
 * Задание 2а. Преврати класс в REST-контроллер и реализуй методы.
 * Защиту путей настроишь в HwSecurityConfig (задание 2б).
 */
public class HwController {

    /** GET /hw/public/ping -> {"pong": true} — доступен без аутентификации. */
    public Map<String, Boolean> ping() {
        throw new UnsupportedOperationException("TODO: задание 2а");
    }

    /** GET /hw/secret -> {"user": "<имя>"} — только аутентифицированным. */
    public Map<String, String> secret(Authentication auth) {
        throw new UnsupportedOperationException("TODO: задание 2а");
    }

    /** GET /hw/admin/panel -> {"panel": "ok"} — только роли ADMIN. */
    public Map<String, String> adminPanel() {
        throw new UnsupportedOperationException("TODO: задание 2а");
    }
}
