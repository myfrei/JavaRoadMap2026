package com.javaroadmap.spring.s02.homework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.javaroadmap.spring.s02.entity.Author;
import com.javaroadmap.spring.s02.repository.AuthorRepository;
import com.javaroadmap.spring.s02.repository.BookRepository;
import jakarta.persistence.EntityManager;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/**
 * «Красные» тесты домашки модуля 2. Менять их не нужно.
 * Каждый тест выполняется в транзакции и откатывается — данные не пересекаются.
 */
@SpringBootTest
@Transactional
@Import(Mod02Homework.class)
class Mod02HomeworkTest {

    @Autowired
    private Mod02Homework homework;

    @Autowired
    private AuthorRepository authors;

    @Autowired
    private BookRepository books;

    @Autowired
    private EntityManager em;

    private void seedLibrary() {
        Author strugatsky = new Author("Стругацкие");
        strugatsky.addBook("Пикник на обочине", 1972);
        strugatsky.addBook("Трудно быть богом", 1964);
        authors.save(strugatsky);

        Author lem = new Author("Лем");
        lem.addBook("Солярис", 1961);
        authors.save(lem);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Задание 1: saveAuthorWithBooks — каскадное сохранение графа одним save")
    void task1_cascadeSave() {
        Author saved = homework.saveAuthorWithBooks("Брэдбери",
                Map.of("451° по Фаренгейту", 1953, "Марсианские хроники", 1950));
        em.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(books.count()).isEqualTo(2);
        assertThat(authors.findByName("Брэдбери")).isPresent();
    }

    @Test
    @DisplayName("Задание 2: titlesOfAuthor — названия книг автора по алфавиту")
    void task2_titlesOfAuthor() {
        seedLibrary();

        assertThat(homework.titlesOfAuthor("Стругацкие"))
                .containsExactly("Пикник на обочине", "Трудно быть богом");
        assertThat(homework.titlesOfAuthor("Неизвестный")).isEmpty();
    }

    @Test
    @DisplayName("Задание 3: authorsByBookCountDesc — авторы по убыванию числа книг")
    void task3_authorsByBookCount() {
        seedLibrary();

        assertThat(homework.authorsByBookCountDesc())
                .containsExactly("Стругацкие", "Лем");
    }

    @Test
    @DisplayName("Задание 4: renameBook — обновление сущности")
    void task4_renameBook() {
        seedLibrary();
        long bookId = books.findByTitleContainingIgnoreCase("Солярис").getFirst().getId();

        assertThat(homework.renameBook(bookId, "Solaris")).isEqualTo("Solaris");
        em.flush();
        em.clear();

        assertThat(books.findById(bookId).orElseThrow().getTitle()).isEqualTo("Solaris");
        assertThatIllegalArgumentException().isThrownBy(() -> homework.renameBook(-1, "x"));
    }

    @Test
    @DisplayName("Задание 5: seededGenres — жанры из Liquibase-сида по алфавиту")
    void task5_seededGenres() {
        assertThat(homework.seededGenres())
                .containsExactly("Детектив", "Классика", "Фантастика");
    }

    @Test
    @DisplayName("Задание 6: countBooksPublishedSinceViaJdbc — SQL руками через JdbcTemplate")
    void task6_countViaJdbc() {
        seedLibrary();

        assertThat(homework.countBooksPublishedSinceViaJdbc(1964)).isEqualTo(2);
        assertThat(homework.countBooksPublishedSinceViaJdbc(2000)).isZero();
    }
}
