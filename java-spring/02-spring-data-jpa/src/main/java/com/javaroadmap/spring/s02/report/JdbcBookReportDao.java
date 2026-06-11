package com.javaroadmap.spring.s02.report;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * JdbcTemplate vs Hibernate (статья 06): для отчётов ORM не нужен —
 * пишем SQL руками, маппим строки в record. Ни сущностей, ни persistence
 * context, ни dirty checking — только запрос и результат.
 */
@Repository
public class JdbcBookReportDao {

    public record AuthorBookCount(String authorName, long bookCount) {
    }

    private final JdbcTemplate jdbc;

    public JdbcBookReportDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<AuthorBookCount> booksPerAuthor() {
        return jdbc.query("""
                        select a.name, count(b.id) as cnt
                        from authors a
                        left join books b on b.author_id = a.id
                        group by a.name
                        order by cnt desc, a.name
                        """,
                (rs, rowNum) -> new AuthorBookCount(rs.getString("name"), rs.getLong("cnt")));
    }
}
