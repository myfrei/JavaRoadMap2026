package com.javaroadmap.spring.s02.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Справочник жанров: строки приезжают из Liquibase-сида (v1.1-seed-genres.sql). */
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Genre() {
        // для Hibernate
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
