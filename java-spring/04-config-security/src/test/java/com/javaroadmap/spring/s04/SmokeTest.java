package com.javaroadmap.spring.s04;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** Smoke-тест: контекст с двумя security-цепочками и JWT-бинами поднимается. */
@SpringBootTest
class SmokeTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).as("Контекст модуля 4 поднимается").isNotNull();
    }
}
