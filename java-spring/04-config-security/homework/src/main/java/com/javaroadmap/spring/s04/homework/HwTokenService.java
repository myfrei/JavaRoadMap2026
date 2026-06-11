package com.javaroadmap.spring.s04.homework;

import java.util.List;
import org.springframework.security.oauth2.jwt.JwtEncoder;

/**
 * Задание 5. Сделай класс бином и реализуй выпуск JWT (HS256) через
 * уже настроенный JwtEncoder (бин из JwtConfig):
 * - subject = username;
 * - claim "roles" = roles;
 * - срок жизни — 15 минут от момента выпуска (issuedAt/expiresAt).
 *
 * Образец работы с encoder-ом — TokenController в примерах модуля.
 */
public class HwTokenService {

    private final JwtEncoder encoder;

    public HwTokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String issueToken(String username, List<String> roles) {
        throw new UnsupportedOperationException("TODO: задание 5");
    }
}
