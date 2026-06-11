package com.javaroadmap.spring.s02.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.javaroadmap.spring.s02.entity.Author;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/** Связи и ленивая загрузка (статья 03) — поведение, а не конфигурация на веру. */
@DataJpaTest
class EntityRelationshipsTest {

    @Autowired
    private AuthorRepository authors;

    @Autowired
    private BookRepository books;

    @Autowired
    private TestEntityManager em;

    @Test
    void cascadeSavesWholeGraphWithOneCall() {
        Author author = new Author("Брэдбери");
        author.addBook("451° по Фаренгейту", 1953);
        author.addBook("Марсианские хроники", 1950);

        authors.save(author); // книги отдельно не сохраняем — cascade = ALL

        assertThat(books.count()).isEqualTo(2);
    }

    @Test
    void orphanRemovalDeletesBookRemovedFromCollection() {
        Author author = new Author("Брэдбери");
        author.addBook("451° по Фаренгейту", 1953);
        author.addBook("Марсианские хроники", 1950);
        Long id = authors.save(author).getId();
        em.flush();
        em.clear();

        Author reloaded = authors.findById(id).orElseThrow();
        reloaded.removeBook(reloaded.getBooks().getFirst());
        em.flush();

        assertThat(books.count()).as("orphanRemoval удалил строку из books").isEqualTo(1);
    }

    @Test
    void lazyCollectionExplodesOutsidePersistenceContext() {
        Author author = new Author("Брэдбери");
        author.addBook("451° по Фаренгейту", 1953);
        Long id = authors.save(author).getId();
        em.flush();
        em.clear();

        Author detached = authors.findById(id).orElseThrow();
        em.detach(detached); // имитируем «сущность пережила транзакцию»

        assertThatExceptionOfType(LazyInitializationException.class)
                .as("ленивая коллекция вне persistence context недоступна")
                .isThrownBy(() -> detached.getBooks().size());
    }

    @Test
    void joinFetchLoadsBooksEagerlyInOneQuery() {
        Author author = new Author("Брэдбери");
        author.addBook("451° по Фаренгейту", 1953);
        authors.save(author);
        em.flush();
        em.clear();

        var loaded = authors.findAllWithBooks();
        loaded.forEach(a -> em.detach(a)); // после detach доступны только уже загруженные данные

        assertThat(loaded.getFirst().getBooks())
                .as("join fetch загрузил книги сразу — после detach они на месте")
                .hasSize(1);
    }
}
