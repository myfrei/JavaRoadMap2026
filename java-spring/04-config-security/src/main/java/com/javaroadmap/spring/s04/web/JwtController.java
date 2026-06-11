package com.javaroadmap.spring.s04.web;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Ресурс под JWT-цепочкой: principal здесь — расшифрованный токен. */
@RestController
public class JwtController {

    @GetMapping("/api/jwt/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "sub", jwt.getSubject(),
                "roles", jwt.getClaimAsStringList("roles"));
    }
}
