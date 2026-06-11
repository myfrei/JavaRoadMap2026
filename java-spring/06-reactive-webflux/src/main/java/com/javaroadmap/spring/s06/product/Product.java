package com.javaroadmap.spring.s06.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * R2DBC-сущность (статья 04): легковесный маппинг строки на record —
 * без persistence context, lazy-коллекций и dirty checking из мира JPA.
 */
@Table("products")
public record Product(@Id Long id, String name, @Column("price_cents") long priceCents) {

    public static Product create(String name, long priceCents) {
        return new Product(null, name, priceCents); // id назначит база
    }
}
