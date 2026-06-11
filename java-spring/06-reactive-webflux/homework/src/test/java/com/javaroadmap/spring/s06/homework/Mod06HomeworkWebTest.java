package com.javaroadmap.spring.s06.homework;

import com.javaroadmap.spring.s06.product.Product;
import com.javaroadmap.spring.s06.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * «Красные» интеграционные тесты домашки модуля 6 (R2DBC + WebFlux).
 * Менять их не нужно.
 */
@SpringBootTest
@AutoConfigureWebTestClient
class Mod06HomeworkWebTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository products;

    @BeforeEach
    void seed() {
        products.deleteAll()
                .thenMany(products.saveAll(Flux.just(
                        Product.create("Клавиатура", 350_00),
                        Product.create("Мышь", 120_00),
                        Product.create("Монитор", 999_00))))
                .blockLast();
    }

    @Test
    @DisplayName("Задание 3: affordable — фильтр по цене, дешёвые первыми")
    void task3_affordable() {
        ProductSearchService search = context.getBean(ProductSearchService.class);

        StepVerifier.create(search.affordable(400_00))
                .expectNextMatches(p -> p.name().equals("Мышь"))
                .expectNextMatches(p -> p.name().equals("Клавиатура"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Задание 3: totalValueCents — сумма всех цен")
    void task3_totalValue() {
        ProductSearchService search = context.getBean(ProductSearchService.class);

        StepVerifier.create(search.totalValueCents())
                .expectNext(1_469_00L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Задание 4: functional endpoint GET /hw/ping")
    void task4_pingRoute() {
        webTestClient.get().uri("/hw/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pong").isEqualTo(true);
    }
}
