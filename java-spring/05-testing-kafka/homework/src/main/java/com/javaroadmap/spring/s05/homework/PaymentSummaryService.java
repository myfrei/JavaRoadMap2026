package com.javaroadmap.spring.s05.homework;

import java.util.List;

/**
 * Задание 4. Проекция по потоку событий (Event-Driven, статья 02):
 * агрегат строится из событий, а не читается из таблицы.
 */
public class PaymentSummaryService {

    /** Итог по списку событий: сколько OK, сколько FAILED, сумма OK-платежей. */
    public record Summary(long okCount, long failedCount, long okTotalCents) {
    }

    public Summary summarize(List<PaymentEvent> events) {
        throw new UnsupportedOperationException("TODO: задание 4");
    }
}
