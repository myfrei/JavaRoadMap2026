package com.javaroadmap.spring.s06.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Реактивный репозиторий (статья 04): те же derived queries, что в JPA,
 * но возвращаемые типы — Flux/Mono: данные приходят потоком по мере чтения.
 */
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findByPriceCentsLessThanEqual(long maxPriceCents);

    Flux<Product> findByNameContainingIgnoreCase(String namePart);
}
