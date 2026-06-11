package com.javaroadmap.spring.s02.repository;

import com.javaroadmap.spring.s02.entity.Genre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);
}
