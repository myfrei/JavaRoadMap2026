package com.javaroadmap.spring.s04.security;

import com.javaroadmap.spring.s04.config.JwtProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * Симметричный JWT (статья 06): один секрет и подписывает, и проверяет (HS256).
 * Для нескольких сервисов берут асимметрию (RS256): приватный ключ подписывает,
 * публичный раздаётся проверяющим.
 */
@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(JwtProperties props) {
        return new SecretKeySpec(props.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    /** Проверка входящих Bearer-токенов — его использует цепочка /api/jwt/**. */
    @Bean
    public JwtDecoder jwtDecoder(SecretKey key) {
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /** Выпуск токенов — его использует TokenController. */
    @Bean
    public JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }
}
