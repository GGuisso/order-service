package com.guisso.orderservice.service;

import com.guisso.orderservice.exception.OrderNotFoundException;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final OrderService orderService = new OrderService(orderRepository);

    @Test
    public void testGetOrderByIdFound() {
        String orderId = "order-123";
        Order order = new Order();
        order.setId(orderId);
        order.setTotalAmount(150.00);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    public void testGetOrderByIdNotFound() {
        String orderId = "order-123";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->
                orderService.getOrderById(orderId)
        );
        assertTrue(exception.getMessage().contains(orderId));
    }

    @Test
    public void testGetOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order();
        order.setId("order-123");

        Page<Order> page = new PageImpl<>(Collections.singletonList(order), pageable, 1);
        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<Order> result = orderService.getOrders(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
