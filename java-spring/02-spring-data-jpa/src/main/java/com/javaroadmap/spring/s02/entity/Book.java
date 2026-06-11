package com.javaroadmap.spring.s02.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Сторона «многие» — владелец связи с Author (статья 03).
 * @ManyToOne всегда делаем LAZY: дефолтный EAGER тянет автора
 * при каждой загрузке книги, даже когда он не нужен.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "publication_year")
    private int year;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    @JoinTable(name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();

    protected Book() {
        // для Hibernate
    }

    Book(String title, int year, Author author) {
        this.title = title;
        this.year = year;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title; // dirty checking: внутри транзакции UPDATE уйдёт сам (статья 05)
    }

    public int getYear() {
        return year;
    }

    public Author getAuthor() {
        return author;
    }

    public Set<Genre> getGenres() {
        return genres;
    }
}
