package com.javaroadmap.spring.s02.report;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaroadmap.spring.s02.entity.Author;
import com.javaroadmap.spring.s02.repository.AuthorRepository;
import com.javaroadmap.spring.s02.report.JdbcBookReportDao.AuthorBookCount;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * JdbcTemplate vs Hibernate (статья 06): данные кладём через JPA,
 * отчёт читаем чистым SQL — обе технологии смотрят в одну H2
 * и работают в одной транзакции теста (поэтому нужен flush).
 */
@SpringBootTest
@Transactional
class JdbcBookReportDaoTest {

    @Autowired
    private AuthorRepository authors;

    @Autowired
    private JdbcBookReportDao reportDao;

    @Autowired
    private EntityManager em;

    @Test
    void sqlReportSeesDataSavedThroughJpa() {
        Author strugatsky = new Author("Стругацкие");
        strugatsky.addBook("Пикник на обочине", 1972);
        strugatsky.addBook("Трудно быть богом", 1964);
        authors.save(strugatsky);

        Author lem = new Author("Лем");
        lem.addBook("Солярис", 1961);
        authors.save(lem);

        em.flush(); // INSERT-ы должны реально уйти в базу до SQL-запроса

        assertThat(reportDao.booksPerAuthor()).containsExactly(
                new AuthorBookCount("Стругацкие", 2),
                new AuthorBookCount("Лем", 1));
    }
}
