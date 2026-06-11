package com.javaroadmap.spring.s06.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaroadmap.spring.s06.product.Product;
import com.javaroadmap.spring.s06.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import reactor.test.StepVerifier;

/**
 * Интеграционный тест WebClient (статья 04): настоящий Netty на случайном
 * порту, настоящий HTTP-клиент — реактивность от базы до клиента.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductClientIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository products;

    @BeforeEach
    void seed() {
        products.deleteAll()
                .then(products.save(Product.create("Ноутбук", 5_500_00)))
                .block();
    }

    @Test
    void clientFetchesProductsOverHttp() {
        var client = new ProductClient("http://localhost:" + port);

        StepVerifier.create(client.fetchAll())
                .assertNext(p -> {
                    assertThat(p.name()).isEqualTo("Ноутбук");
                    assertThat(p.priceCents()).isEqualTo(5_500_00);
                })
                .verifyComplete();
    }

    @Test
    void functionalEndpointAnswersPing() {
        var client = org.springframework.web.reactive.function.client.WebClient
                .create("http://localhost:" + port);

        StepVerifier.create(client.get().uri("/fn/ping").retrieve().bodyToMono(String.class))
                .expectNext("{\"pong\":true}")
                .verifyComplete();
    }
}
