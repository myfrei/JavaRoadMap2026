package com.javaroadmap.spring.s04.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Биндинг конфигурации (статья 01): properties → типизированный record. */
@SpringBootTest
class AppPropertiesTest {

    @Autowired
    private AppProperties app;

    @Test
    void propertiesAreBoundToRecord() {
        assertThat(app.name()).isEqualTo("JavaRoadmap Security Demo");
        assertThat(app.features()).containsExactly("security", "jwt", "cors");
    }
}
