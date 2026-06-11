package com.javaroadmap.spring.s01.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.javaroadmap.spring.s01.notify.NotificationSender;
import com.javaroadmap.spring.s01.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.Test;

/**
 * Юнит-тест сервисного слоя БЕЗ Spring-контекста: зависимости — интерфейсы,
 * поэтому подставляем реальный in-memory репозиторий и мок отправителя.
 * Это плата за конструкторное внедрение — и она нулевая.
 */
class OrderServiceTest {

    private final NotificationSender notifications = mock(NotificationSender.class);
    private final OrderService service = new OrderService(new InMemoryOrderRepository(), notifications);

    @Test
    void placeOrderSavesAndNotifies() {
        var order = service.placeOrder("alice", 500);

        assertThat(order.id()).isPositive();
        assertThat(service.findOrder(order.id())).contains(order);
        verify(notifications).send(anyString(), contains("Заказ №" + order.id()));
    }

    @Test
    void rejectsNonPositiveAmountAndStaysSilent() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.placeOrder("bob", 0));

        assertThat(service.allOrders()).isEmpty();
        verifyNoInteractions(notifications);
    }
}
