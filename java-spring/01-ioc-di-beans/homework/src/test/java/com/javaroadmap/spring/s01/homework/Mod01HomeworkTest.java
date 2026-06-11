package com.javaroadmap.spring.s01.homework;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * «Красные» тесты домашки модуля 1. Менять их не нужно.
 *
 * <p>Каждый тест сканирует пакет домашки — бины появляются в контексте,
 * как только ты вешаешь правильные аннотации (см. homework/README.md).
 */
class Mod01HomeworkTest {

    private static final String HOMEWORK_PACKAGE = "com.javaroadmap.spring.s01.homework";

    private static AnnotationConfigApplicationContext scanHomework() {
        return new AnnotationConfigApplicationContext(HOMEWORK_PACKAGE);
    }

    @Test
    @DisplayName("Задание 1: ReceiptService — бин с конструкторным внедрением PriceFormatter")
    void task1_receiptService() {
        try (var context = scanHomework()) {
            var receipts = context.getBean(ReceiptService.class);

            assertThat(receipts.receipt("alice", 100)).isEqualTo("Чек для alice: 100 ₽");
            assertThat(context.getBean(PriceFormatter.class)).isInstanceOf(RubPriceFormatter.class);
        }
    }

    @Test
    @DisplayName("Задание 2: CacheWarmup — @PostConstruct при старте, @PreDestroy при закрытии")
    void task2_lifecycleCallbacks() {
        CacheWarmup cache;
        try (var context = scanHomework()) {
            cache = context.getBean(CacheWarmup.class);

            assertThat(cache.isWarm()).as("после старта контекста кэш прогрет").isTrue();
            assertThat(cache.isShutDown()).as("до закрытия контекста shutdown не вызывался").isFalse();
        }

        assertThat(cache.isWarm()).as("после закрытия контекста кэш погашен").isFalse();
        assertThat(cache.isShutDown()).as("закрытие контекста вызвало shutdown").isTrue();
    }

    @Test
    @DisplayName("Задание 3: HomeworkConfig — TaxCalculator(0.20) объявлен @Bean-методом")
    void task3_beanFromJavaConfig() {
        try (var context = scanHomework()) {
            var calculator = context.getBean(TaxCalculator.class);

            assertThat(calculator.withTax(100)).isEqualTo(120);
            assertThat(calculator.withTax(0)).isZero();
        }
    }

    @Test
    @DisplayName("Задание 4: OrderStatsService — сервис-бин со статистикой по репозиторию")
    void task4_serviceLayer() {
        try (var context = scanHomework()) {
            var stats = context.getBean(OrderStatsService.class);

            assertThat(stats.totalRevenue()).isEqualTo(1000);
            assertThat(stats.averageCheck()).isEqualTo(250);
            assertThat(stats.maxAmount()).isEqualTo(400);
        }
    }

    @Test
    @DisplayName("Задание 5а: SequentialIdGenerator — @Primary и счёт с единицы")
    void task5a_primaryImplementation() {
        try (var context = scanHomework()) {
            var generator = context.getBean(IdGenerator.class);

            assertThat(generator).isInstanceOf(SequentialIdGenerator.class);
            assertThat(generator.nextId()).isEqualTo(1);
            assertThat(generator.nextId()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Задание 5б: FixedIdGenerator — бин по имени \"fixed\", всегда 42")
    void task5b_qualifiedImplementation() {
        try (var context = scanHomework()) {
            var generator = context.getBean("fixed", IdGenerator.class);

            assertThat(generator.nextId()).isEqualTo(42);
            assertThat(generator.nextId()).isEqualTo(42);
        }
    }

    @Test
    @DisplayName("Задание 5в: ScratchBuffer — prototype: каждый запрос — новый экземпляр")
    void task5c_prototypeScope() {
        try (var context = scanHomework()) {
            var first = context.getBean(ScratchBuffer.class);
            var second = context.getBean(ScratchBuffer.class);

            assertThat(first).isNotSameAs(second);
            first.append("a");
            assertThat(second.content()).as("буферы независимы").isEmpty();
        }
    }
}
