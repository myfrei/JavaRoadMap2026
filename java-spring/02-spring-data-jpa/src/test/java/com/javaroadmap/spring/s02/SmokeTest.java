package com.javaroadmap.spring.s02;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** Smoke-тест: контекст поднимается = Liquibase накатился и схема прошла validate. */
@SpringBootTest
class SmokeTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).as("Контекст модуля 2 поднимается").isNotNull();
    }
}
