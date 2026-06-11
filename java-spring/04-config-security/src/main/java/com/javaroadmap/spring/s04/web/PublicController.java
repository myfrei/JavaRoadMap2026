package com.javaroadmap.spring.s04.web;

import com.javaroadmap.spring.s04.config.AppProperties;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Открытая часть API: permitAll в basicChain. */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final AppProperties app;

    public PublicController(AppProperties app) {
        this.app = app;
    }

    @GetMapping("/ping")
    public Map<String, Boolean> ping() {
        return Map.of("pong", true);
    }

    /** Конфигурация (статья 01) доступна как обычный бин. */
    @GetMapping("/info")
    public AppProperties info() {
        return app;
    }
}
