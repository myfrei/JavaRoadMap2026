package com.javaroadmap.spring.s01.homework;

import java.util.List;
import org.springframework.stereotype.Repository;

/** Задание 4. Готовая реализация хранилища — менять её не нужно. */
@Repository
public class InMemoryStatsRepository implements StatsRepository {

    @Override
    public List<Long> amounts() {
        return List.of(100L, 200L, 300L, 400L);
    }
}
