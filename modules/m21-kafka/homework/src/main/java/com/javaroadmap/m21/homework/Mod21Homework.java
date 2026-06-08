package com.javaroadmap.m21.homework;

import java.util.List;
import java.util.Map;

/**
 * Домашка модуля 21 — «Kafka» (advance-трек): идемпотентность, надёжность, оффсеты, компакция.
 * Проверка: {@code ./gradlew :modules:m21-kafka:homeworkTest}
 */
public final class Mod21Homework {

    private Mod21Homework() {
    }

    /** Задание 1: партиция для ключа: floorMod(key.hashCode(), partitions). Один ключ -> одна партиция -> порядок. */
    public static int partitionFor(String key, int partitions) {
        throw new UnsupportedOperationException("TODO задание 1: partitionFor");
    }

    /**
     * Задание 3: безопасный оффсет для коммита (at-least-once): наименьший НЕобработанный оффсет >= 0
     * (первый «разрыв»). {0,1,2} -> 3; {0,1,2,4} -> 3; {} -> 0; {1,2} -> 0.
     */
    public static long nextCommitOffset(List<Long> processedOffsets) {
        throw new UnsupportedOperationException("TODO задание 3: nextCommitOffset");
    }

    /** Задание 4: маршрут после ошибки: attempt < maxRetries -> "RETRY", иначе "DLQ". */
    public static String retryRoute(int attempt, int maxRetries) {
        throw new UnsupportedOperationException("TODO задание 4: retryRoute");
    }

    /** Задание 5: компакция лога — последнее значение по каждому ключу (по наибольшему offset). */
    public static Map<String, String> latestByKey(List<KafkaRecord> records) {
        throw new UnsupportedOperationException("TODO задание 5: latestByKey");
    }
}

/** Запись Kafka для задания 5. */
record KafkaRecord(String key, String value, long offset) {
}

/** Задание 2: идемпотентный обработчик. process(id): true в первый раз, false для дубликата. */
class IdempotentProcessor {

    boolean process(String messageId) {
        throw new UnsupportedOperationException("TODO задание 2: process");
    }

    int processedCount() {
        throw new UnsupportedOperationException("TODO задание 2: processedCount");
    }
}
