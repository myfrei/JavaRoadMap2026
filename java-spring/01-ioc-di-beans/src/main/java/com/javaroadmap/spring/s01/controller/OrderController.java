package com.javaroadmap.spring.s01.controller;

import com.javaroadmap.spring.s01.model.Order;
import com.javaroadmap.spring.s01.service.OrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST-контроллер (статья 05): тонкий слой — принял запрос, делегировал сервису,
 * вернул результат. Никакой бизнес-логики здесь нет.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /** Тело POST-запроса: id назначает сервер, поэтому клиент его не передаёт. */
    public record NewOrderRequest(String customer, long amount) {
    }

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> all() {
        return orderService.allOrders();
    }

    @GetMapping("/{id}")
    public Order byId(@PathVariable long id) {
        return orderService.findOrder(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ %d не найден".formatted(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody NewOrderRequest request) {
        return orderService.placeOrder(request.customer(), request.amount());
    }
}
