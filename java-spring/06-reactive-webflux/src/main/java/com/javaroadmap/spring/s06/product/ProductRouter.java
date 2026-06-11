package com.javaroadmap.spring.s06.product;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Functional endpoints (статья 03): второй стиль WebFlux — маршруты как код,
 * без аннотаций. Удобен, когда маршрутов много и они строятся динамически.
 */
@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> functionalRoutes(ProductRepository products) {
        return route(GET("/fn/ping"),
                request -> ServerResponse.ok().bodyValue(Map.of("pong", true)))
                .andRoute(GET("/fn/products/count"),
                        request -> products.count()
                                .flatMap(count -> ServerResponse.ok().bodyValue(Map.of("count", count))));
    }
}
