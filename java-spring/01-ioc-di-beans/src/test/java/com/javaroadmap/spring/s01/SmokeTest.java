package com.javaroadmap.spring.s01;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** Smoke-тест: Spring-контекст модуля поднимается. Заменяй реальными тестами. */
@SpringBootTest
class SmokeTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).as("Контекст модуля 1 поднимается").isNotNull();
    }
}
