package com.javaroadmap.spring.s01.service;

import com.javaroadmap.spring.s01.model.Order;
import com.javaroadmap.spring.s01.notify.NotificationSender;
import com.javaroadmap.spring.s01.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Сервисный слой (статья 04): бизнес-правила живут здесь, а не в контроллере.
 *
 * <p>Обе зависимости — интерфейсы и приходят через конструктор (статья 01):
 * сервис не создаёт их сам и потому тестируется с любыми подменами.
 * NotificationSender внедряется без уточнения — контейнер берёт @Primary-бин.
 */
@Service
public class OrderService {

    private final OrderRepository repository;
    private final NotificationSender notifications;

    public OrderService(OrderRepository repository, NotificationSender notifications) {
        this.repository = repository;
        this.notifications = notifications;
    }

    public Order placeOrder(String customer, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма заказа должна быть положительной: " + amount);
        }
        Order order = repository.save(customer, amount);
        notifications.send(customer, "Заказ №%d на %d ₽ принят".formatted(order.id(), order.amount()));
        return order;
    }

    public Optional<Order> findOrder(long id) {
        return repository.findById(id);
    }

    public List<Order> allOrders() {
        return repository.findAll();
    }
}
