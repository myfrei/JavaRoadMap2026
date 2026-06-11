package com.javaroadmap.spring.s02;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/** Миграции (статья 04): схема и сид созданы Liquibase, а не Hibernate. */
@SpringBootTest
class LiquibaseMigrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void changelogCreatedAllTables() {
        var tables = jdbc.queryForList("""
                select table_name from information_schema.tables
                where table_schema = 'PUBLIC'
                """, String.class);

        assertThat(tables)
                .contains("AUTHORS", "BOOKS", "GENRES", "BOOK_GENRES")
                .as("Liquibase ведёт свой журнал применённых чейнджсетов")
                .contains("DATABASECHANGELOG", "DATABASECHANGELOGLOCK");
    }

    @Test
    void seedChangesetInsertedGenres() {
        var genres = jdbc.queryForList("select name from genres order by name", String.class);

        assertThat(genres).containsExactly("Детектив", "Классика", "Фантастика");
    }
}
