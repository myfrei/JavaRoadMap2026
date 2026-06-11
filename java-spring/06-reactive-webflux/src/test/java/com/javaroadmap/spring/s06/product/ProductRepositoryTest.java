package com.javaroadmap.spring.s06.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Slice-тест R2DBC (статьи 04, 06): реальная реактивная H2,
 * проверки — StepVerifier-ом, без block().
 */
@DataR2dbcTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository products;

    @BeforeEach
    void seed() {
        products.deleteAll()
                .thenMany(products.saveAll(Flux.just(
                        Product.create("Клавиатура", 350_00),
                        Product.create("Мышь", 120_00),
                        Product.create("Монитор", 999_00))))
                .blockLast(); // в @BeforeEach блокировка допустима — это не реактивный код
    }

    @Test
    void derivedQueryFiltersByPrice() {
        StepVerifier.create(products.findByPriceCentsLessThanEqual(400_00))
                .expectNextMatches(p -> p.name().equals("Клавиатура"))
                .expectNextMatches(p -> p.name().equals("Мышь"))
                .verifyComplete();
    }

    @Test
    void derivedQuerySearchesByName() {
        StepVerifier.create(products.findByNameContainingIgnoreCase("мОнИт"))
                .expectNextMatches(p -> p.priceCents() == 999_00)
                .verifyComplete();
    }

    @Test
    void countReflectsSeededData() {
        StepVerifier.create(products.count())
                .expectNext(3L)
                .verifyComplete();
    }
}
