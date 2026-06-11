package com.javaroadmap.spring.s06.homework;

import com.javaroadmap.spring.s06.product.Product;
import com.javaroadmap.spring.s06.product.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Задание 3. Сделай класс бином и реализуй методы РЕАКТИВНОЙ композицией
 * поверх репозитория — без block() и без загрузки всего в список.
 */
public class ProductSearchService {

    private final ProductRepository products;

    public ProductSearchService(ProductRepository products) {
        this.products = products;
    }

    /** Товары не дороже maxCents, отсортированные по цене (дешёвые первыми). */
    public Flux<Product> affordable(long maxCents) {
        throw new UnsupportedOperationException("TODO: задание 3");
    }

    /** Суммарная стоимость всех товаров в центах; пусто -> 0. */
    public Mono<Long> totalValueCents() {
        throw new UnsupportedOperationException("TODO: задание 3");
    }
}
