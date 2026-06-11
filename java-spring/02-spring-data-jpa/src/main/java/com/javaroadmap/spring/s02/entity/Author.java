package com.javaroadmap.spring.s02.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Сторона «один» (статья 03): владелец связи — Book (там FK-колонка),
 * здесь mappedBy. cascade + orphanRemoval: книги живут и умирают вместе
 * с автором — сохраняется весь граф одним save().
 */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    protected Author() {
        // для Hibernate
    }

    public Author(String name) {
        this.name = name;
    }

    /** Держит обе стороны связи согласованными — иначе FK не запишется. */
    public Book addBook(String title, int year) {
        Book book = new Book(title, year, this);
        books.add(book);
        return book;
    }

    public void removeBook(Book book) {
        books.remove(book); // orphanRemoval удалит строку из books
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return books;
    }
}
