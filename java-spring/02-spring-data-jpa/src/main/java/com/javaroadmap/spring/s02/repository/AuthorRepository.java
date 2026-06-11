package com.javaroadmap.spring.s02.repository;

import com.javaroadmap.spring.s02.entity.Author;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Репозиторий (статья 02): интерфейс без единой строчки реализации —
 * её генерирует Spring Data по имени метода или по @Query.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /** Derived query: SQL выводится из имени метода. */
    Optional<Author> findByName(String name);

    /**
     * Лекарство от N+1 (статья 03): авторы и их книги — ОДНИМ запросом.
     * Без join fetch каждая обращённая коллекция books — отдельный SELECT.
     */
    @Query("select distinct a from Author a left join fetch a.books")
    List<Author> findAllWithBooks();
}
