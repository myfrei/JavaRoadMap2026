package com.javaroadmap.spring.s01.config;

import com.javaroadmap.spring.s01.pricing.PriceCalculator;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-конфигурация (статья 03): способ объявить бины кодом, когда
 * аннотацию на класс не повесить (чужая библиотека) или сборка бина
 * требует параметров/логики.
 */
@Configuration
public class AppConfig {

    /** Время — всегда бином: в тестах его подменяют на Clock.fixed(...). */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public PriceCalculator priceCalculator() {
        return new PriceCalculator(0.20); // НДС 20% — параметр конструктора, аннотацией так не собрать
    }
}
