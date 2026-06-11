package com.javaroadmap.spring.s06.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Аннотационный WebFlux-контроллер (статья 03): сигнатуры как в MVC,
 * но возвращаются Flux/Mono — ничего не блокируется, элементы уходят
 * клиенту по мере готовности.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    public record NewProduct(String name, long priceCents) {
    }

    private final ProductRepository products;

    public ProductController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping
    public Flux<Product> all() {
        return products.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Product> byId(@PathVariable long id) {
        return products.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Товар %d не найден".formatted(id))));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@RequestBody NewProduct request) {
        return products.save(Product.create(request.name(), request.priceCents()));
    }
}
