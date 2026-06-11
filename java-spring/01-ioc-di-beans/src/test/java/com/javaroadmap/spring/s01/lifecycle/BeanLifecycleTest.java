package com.javaroadmap.spring.s01.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Жизненный цикл проверяем на «ручном» контексте: его можно закрыть прямо
 * в тесте и увидеть @PreDestroy (в @SpringBootTest контекст закрывается
 * после всех тестов — момент destroy-колбэка не пронаблюдать).
 */
class BeanLifecycleTest {

    @Test
    void callbacksFireInDocumentedOrder() {
        ConnectionPool pool;
        try (var context = new AnnotationConfigApplicationContext(ConnectionPool.class)) {
            pool = context.getBean(ConnectionPool.class);

            assertThat(pool.events()).containsExactly("constructor", "postConstruct");
            assertThat(pool.isOpen()).as("после @PostConstruct пул открыт").isTrue();
        }

        assertThat(pool.events()).containsExactly("constructor", "postConstruct", "preDestroy");
        assertThat(pool.isOpen()).as("закрытие контекста вызывает @PreDestroy").isFalse();
    }

    @Test
    void beanPostProcessorSeesEveryBean() {
        try (var context = new AnnotationConfigApplicationContext(
                InitTrackingBeanPostProcessor.class, ConnectionPool.class)) {

            var tracker = context.getBean(InitTrackingBeanPostProcessor.class);

            assertThat(tracker.initializedBeans()).contains("connectionPool");
        }
    }
}
