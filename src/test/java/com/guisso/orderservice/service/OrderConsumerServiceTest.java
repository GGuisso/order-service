package com.guisso.orderservice.service;

import com.guisso.orderservice.dto.OrderRequestDTO;
import com.guisso.orderservice.mapper.OrderMapper;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.OrderStatus;
import com.guisso.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderConsumerServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final OrderMapper orderMapper = Mockito.mock(OrderMapper.class);
    private final OrderConsumerService consumerService = new OrderConsumerService(orderRepository, orderMapper);

    @Test
    public void testReceiveMessage_DuplicateOrder() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setId("order-123");
        dto.setProducts(Collections.emptyList());

        when(orderRepository.existsById(dto.getId())).thenReturn(true);

        consumerService.receiveMessage(dto);

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testReceiveMessage_Success() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setId("order-123");
        com.guisso.orderservice.dto.ProductDTO productDTO = new com.guisso.orderservice.dto.ProductDTO();
        productDTO.setId("prod-1");
        productDTO.setName("Test Product");
        productDTO.setPrice(10.0);
        dto.setProducts(Collections.singletonList(productDTO));

        when(orderRepository.existsById(dto.getId())).thenReturn(false);

        Order order = new Order();
        order.setId(dto.getId());
        order.setProducts(new java.util.ArrayList<>());
        //Simula a conversão do DTO para entidade
        com.guisso.orderservice.model.Product product = new com.guisso.orderservice.model.Product();
        product.setId("prod-1");
        product.setName("Test Product");
        product.setPrice(10.0);
        order.getProducts().add(product);

        when(orderMapper.orderRequestDTOToOrder(dto)).thenReturn(order);

        consumerService.receiveMessage(dto);

        //Captura o pedido salvo
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        //Verifica se o total foi calculado corretamente
        assertEquals(10.0, savedOrder.getTotalAmount());
        //Verifica se o status foi definido como PROCESSED
        assertEquals(OrderStatus.PROCESSED, savedOrder.getStatus());
    }
}
