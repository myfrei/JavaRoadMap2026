package com.javaroadmap.spring.s01.repository;

import com.javaroadmap.spring.s01.model.Order;
import java.util.List;
import java.util.Optional;

/**
 * Порт хранилища заказов (статья 01: зависимость объявляется интерфейсом,
 * реализацию подставляет контейнер — код сервиса не знает про «как хранится»).
 */
public interface OrderRepository {

    Order save(String customer, long amount);

    Optional<Order> findById(long id);

    List<Order> findAll();
}
