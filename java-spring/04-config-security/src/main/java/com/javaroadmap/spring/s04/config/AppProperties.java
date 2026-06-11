package com.javaroadmap.spring.s04.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Типобезопасная конфигурация (статья 01): record + @ConfigurationProperties
 * вместо россыпи @Value. Биндинг проверяется на старте, опечатка в типе —
 * это упавший контекст, а не NPE в проде.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(String name, List<String> features) {
}
