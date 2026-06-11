package com.javaroadmap.spring.s04.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Секрет подписи JWT — отдельный неймспейс конфигурации (статья 06). */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret) {
}
