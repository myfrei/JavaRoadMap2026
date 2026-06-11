package com.javaroadmap.spring.s05.homework;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * «Красные» юнит-тесты домашки модуля 5 (низ пирамиды — без Spring и Kafka).
 * Менять их не нужно.
 */
class Mod05HomeworkUnitTest {

    private final PaymentValidator validator = new PaymentValidator();
    private final PaymentSummaryService summaryService = new PaymentSummaryService();

    @Test
    @DisplayName("Задание 1: валидное событие проходит проверку")
    void task1_validEvent() {
        assertThat(validator.isValid(new PaymentEvent("p-1", 100, "OK"))).isTrue();
        assertThat(validator.isValid(new PaymentEvent("p-2", 1, "FAILED"))).isTrue();
    }

    @Test
    @DisplayName("Задание 1: мусор отбрасывается")
    void task1_invalidEvents() {
        assertThat(validator.isValid(new PaymentEvent(null, 100, "OK"))).isFalse();
        assertThat(validator.isValid(new PaymentEvent("   ", 100, "OK"))).isFalse();
        assertThat(validator.isValid(new PaymentEvent("p-3", 0, "OK"))).isFalse();
        assertThat(validator.isValid(new PaymentEvent("p-4", -5, "OK"))).isFalse();
        assertThat(validator.isValid(new PaymentEvent("p-5", 100, "PENDING"))).isFalse();
    }

    @Test
    @DisplayName("Задание 4: summarize считает OK/FAILED и сумму OK-платежей")
    void task4_summary() {
        var summary = summaryService.summarize(List.of(
                new PaymentEvent("p-1", 100, "OK"),
                new PaymentEvent("p-2", 250, "OK"),
                new PaymentEvent("p-3", 999, "FAILED")));

        assertThat(summary.okCount()).isEqualTo(2);
        assertThat(summary.failedCount()).isEqualTo(1);
        assertThat(summary.okTotalCents()).isEqualTo(350);
    }

    @Test
    @DisplayName("Задание 4: пустой поток — нулевой итог")
    void task4_emptySummary() {
        var summary = summaryService.summarize(List.of());

        assertThat(summary).isEqualTo(new PaymentSummaryService.Summary(0, 0, 0));
    }
}
