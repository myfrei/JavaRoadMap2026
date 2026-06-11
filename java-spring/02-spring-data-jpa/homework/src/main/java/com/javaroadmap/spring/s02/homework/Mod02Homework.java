package com.javaroadmap.spring.s02.homework;

import com.javaroadmap.spring.s02.entity.Author;
import com.javaroadmap.spring.s02.repository.AuthorRepository;
import com.javaroadmap.spring.s02.repository.BookRepository;
import com.javaroadmap.spring.s02.repository.GenreRepository;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Домашка модуля 2. Зависимости уже внедрены — реализуй методы
 * (формулировки и ожидаемые результаты: homework/README.md).
 *
 * <p>Подсказка: некоторым методам нужна транзакция — вспомни, какая
 * аннотация её открывает и почему без неё ленивые коллекции взрываются.
 */
public class Mod02Homework {

    private final AuthorRepository authors;
    private final BookRepository books;
    private final GenreRepository genres;
    private final JdbcTemplate jdbc;

    public Mod02Homework(AuthorRepository authors, BookRepository books,
                         GenreRepository genres, JdbcTemplate jdbc) {
        this.authors = authors;
        this.books = books;
        this.genres = genres;
        this.jdbc = jdbc;
    }

    /** Задание 1. Сохранить автора со всеми книгами ОДНИМ вызовом репозитория (каскад). */
    public Author saveAuthorWithBooks(String name, Map<String, Integer> titleToYear) {
        throw new UnsupportedOperationException("TODO: задание 1");
    }

    /** Задание 2. Названия книг автора по алфавиту. Автор не найден -> пустой список. */
    public List<String> titlesOfAuthor(String authorName) {
        throw new UnsupportedOperationException("TODO: задание 2");
    }

    /** Задание 3. Имена авторов по убыванию числа книг (при равенстве — по алфавиту). */
    public List<String> authorsByBookCountDesc() {
        throw new UnsupportedOperationException("TODO: задание 3");
    }

    /** Задание 4. Переименовать книгу. Вернуть новое название. Книга не найдена -> IllegalArgumentException. */
    public String renameBook(long bookId, String newTitle) {
        throw new UnsupportedOperationException("TODO: задание 4");
    }

    /** Задание 5. Имена жанров из Liquibase-сида по алфавиту (через GenreRepository). */
    public List<String> seededGenres() {
        throw new UnsupportedOperationException("TODO: задание 5");
    }

    /** Задание 6. Число книг, изданных не раньше года, — ЧЕРЕЗ JdbcTemplate (SQL руками). */
    public long countBooksPublishedSinceViaJdbc(int year) {
        throw new UnsupportedOperationException("TODO: задание 6");
    }
}
