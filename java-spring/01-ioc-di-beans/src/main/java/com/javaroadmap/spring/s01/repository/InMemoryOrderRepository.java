package com.javaroadmap.spring.s01.repository;

import com.javaroadmap.spring.s01.model.Order;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * In-memory реализация — бин находится component scan-ом по @Repository.
 * В модуле 2 её место займёт Spring Data JPA, а сервисный слой не изменится.
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Order save(String customer, long amount) {
        long id = sequence.incrementAndGet();
        Order order = new Order(id, customer, amount);
        storage.put(id, order);
        return order;
    }

    @Override
    public Optional<Order> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(storage.values());
    }
}
