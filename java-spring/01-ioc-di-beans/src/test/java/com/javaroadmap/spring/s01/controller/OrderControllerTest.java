package com.javaroadmap.spring.s01.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.javaroadmap.spring.s01.model.Order;
import com.javaroadmap.spring.s01.service.OrderService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Slice-тест контроллера (статья 05): @WebMvcTest поднимает только MVC-слой,
 * сервис подменяется моком — проверяем именно HTTP-контракт.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getExistingOrderReturnsJson() throws Exception {
        when(orderService.findOrder(1)).thenReturn(Optional.of(new Order(1, "alice", 500)));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("alice"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    void getUnknownOrderReturns404() throws Exception {
        when(orderService.findOrder(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postCreatesOrderAndReturns201() throws Exception {
        when(orderService.placeOrder("bob", 300)).thenReturn(new Order(7, "bob", 300));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"bob\",\"amount\":300}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7));
    }
}
