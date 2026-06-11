package com.javaroadmap.spring.s04.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Профили (статья 01): application-dev.properties перекрывает базовые значения. */
@SpringBootTest
@ActiveProfiles("dev")
class DevProfilePropertiesTest {

    @Autowired
    private AppProperties app;

    @Test
    void devProfileOverridesName() {
        assertThat(app.name()).isEqualTo("JavaRoadmap Security Demo (dev)");
        assertThat(app.features()).as("незатронутые значения наследуются").hasSize(3);
    }
}
