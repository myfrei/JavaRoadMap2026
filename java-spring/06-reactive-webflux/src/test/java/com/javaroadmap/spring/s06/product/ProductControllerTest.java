package com.javaroadmap.spring.s06.product;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Slice-тест WebFlux (статья 06): @WebFluxTest поднимает только реактивный
 * веб-слой, обращения идут через WebTestClient, репозиторий — мок.
 */
@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductRepository products;

    @Test
    void getAllStreamsProducts() {
        when(products.findAll()).thenReturn(Flux.just(
                new Product(1L, "Клавиатура", 350_00),
                new Product(2L, "Мышь", 120_00)));

        webTestClient.get().uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class).hasSize(2);
    }

    @Test
    void emptyMonoBecomes404() {
        when(products.findById(anyLong())).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/products/77")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void postCreatesProduct() {
        when(products.save(Product.create("Монитор", 999_00)))
                .thenReturn(Mono.just(new Product(5L, "Монитор", 999_00)));

        webTestClient.post().uri("/api/products")
                .bodyValue(new ProductController.NewProduct("Монитор", 999_00))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(5);
    }
}
