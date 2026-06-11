package com.javaroadmap.spring.s02.repository;

import com.javaroadmap.spring.s02.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Derived queries (статья 02): поиск по подстроке и по диапазону года. */
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String titlePart);

    List<Book> findByYearBetween(int fromInclusive, int toInclusive);
}
