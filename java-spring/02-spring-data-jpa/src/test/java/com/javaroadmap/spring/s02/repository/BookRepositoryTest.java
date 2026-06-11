package com.javaroadmap.spring.s02.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaroadmap.spring.s02.entity.Author;
import com.javaroadmap.spring.s02.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Slice-тест репозитория (статья 02): @DataJpaTest поднимает только JPA-слой
 * на встроенной H2, каждый тест откатывается — изоляция бесплатно.
 */
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private AuthorRepository authors;

    @Autowired
    private BookRepository books;

    @BeforeEach
    void seed() {
        Author strugatsky = new Author("Стругацкие");
        strugatsky.addBook("Пикник на обочине", 1972);
        strugatsky.addBook("Трудно быть богом", 1964);
        authors.save(strugatsky);

        Author lem = new Author("Лем");
        lem.addBook("Солярис", 1961);
        authors.save(lem);
    }

    @Test
    void derivedQueryFindsByTitlePart() {
        assertThat(books.findByTitleContainingIgnoreCase("пикник"))
                .singleElement()
                .extracting(Book::getYear)
                .isEqualTo(1972);
    }

    @Test
    void derivedQueryFindsByYearRange() {
        assertThat(books.findByYearBetween(1960, 1965))
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Трудно быть богом", "Солярис");
    }
}
