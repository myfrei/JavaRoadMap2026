package com.javaroadmap.spring.s01.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaroadmap.spring.s01.pricing.PriceCalculator;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Java-конфигурация (статья 03): бины из @Bean-методов доступны как обычные. */
@SpringBootTest
class AppConfigTest {

    @Autowired
    private PriceCalculator priceCalculator;

    @Autowired
    private Clock clock;

    @Test
    void thirdPartyClassBecomesABeanViaBeanMethod() {
        assertThat(priceCalculator.withTax(100)).isEqualTo(120);
    }

    @Test
    void clockIsAvailableForInjection() {
        assertThat(clock.instant()).isNotNull();
    }
}
