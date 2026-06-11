package com.javaroadmap.spring.s06.client;

import com.javaroadmap.spring.s06.product.Product;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * WebClient (статья 04): реактивная замена RestTemplate. Запрос не занимает
 * поток на время ожидания ответа — подписка получит данные, когда они придут.
 */
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public Flux<Product> fetchAll() {
        return webClient.get().uri("/api/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }

    public Mono<Product> fetchById(long id) {
        return webClient.get().uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }
}
