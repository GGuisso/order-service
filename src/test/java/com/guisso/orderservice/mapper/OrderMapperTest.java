package com.guisso.orderservice.mapper;

import com.guisso.orderservice.dto.OrderRequestDTO;
import com.guisso.orderservice.dto.OrderResponseDTO;
import com.guisso.orderservice.dto.ProductDTO;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    public void testOrderRequestDTOToOrder() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setId("order-123");
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId("prod-1");
        productDTO.setName("Test Product");
        productDTO.setPrice(10.0);
        dto.setProducts(Collections.singletonList(productDTO));

        Order order = orderMapper.orderRequestDTOToOrder(dto);
        assertNotNull(order);
        assertEquals(dto.getId(), order.getId());
        //Campos totalAmount e status foram ignorados no mapping
        assertNull(order.getTotalAmount());
        assertNull(order.getStatus());
        //Verifica o mapeamento dos produtos
        assertNotNull(order.getProducts());
        assertEquals(1, order.getProducts().size());
        Product product = order.getProducts().get(0);
        assertEquals(productDTO.getId(), product.getId());
        assertEquals(productDTO.getName(), product.getName());
        assertEquals(productDTO.getPrice(), product.getPrice());
    }

    @Test
    public void testOrderToOrderResponseDTO() {
        Order order = new Order();
        order.setId("order-123");
        order.setTotalAmount(150.00);
        order.setStatus(com.guisso.orderservice.model.OrderStatus.PROCESSED);
        Product product = new Product();
        product.setId("prod-1");
        product.setName("Test Product");
        product.setPrice(10.0);
        order.setProducts(Collections.singletonList(product));

        OrderResponseDTO dto = orderMapper.orderToOrderResponseDTO(order);
        assertNotNull(dto);
        assertEquals(order.getId(), dto.getId());
        assertEquals(order.getTotalAmount(), dto.getTotalAmount());
        assertEquals(order.getStatus().name(), dto.getStatus());
        assertNotNull(dto.getProducts());
        assertEquals(1, dto.getProducts().size());
        ProductDTO productDTO = dto.getProducts().get(0);
        assertEquals(product.getId(), productDTO.getId());
        assertEquals(product.getName(), productDTO.getName());
        assertEquals(product.getPrice(), productDTO.getPrice());
    }
}
