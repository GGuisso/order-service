package com.guisso.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisso.orderservice.config.RabbitMQConfig;
import com.guisso.orderservice.dto.OrderRequestDTO;
import com.guisso.orderservice.dto.OrderResponseDTO;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.OrderStatus;
import com.guisso.orderservice.service.OrderService;
import com.guisso.orderservice.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testReceiveOrder() throws Exception {
        //Cria um JSON de requisição com um pedido e um produto
        String jsonRequest = "{ \"id\": \"order-123\", " +
                "\"products\": [{ \"id\": \"prod-1\", \"name\": \"Test Product\", \"price\": 10.00 }] }";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Pedido recebido e em processamento"));

        //Verifica se o método do rabbitTemplate foi chamado com os parâmetros corretos
        verify(rabbitTemplate).convertAndSend(
        	    eq(RabbitMQConfig.ORDER_EXCHANGE),
        	    eq(RabbitMQConfig.ORDER_ROUTING_KEY),
        	    any(OrderRequestDTO.class)
        	);
    }

    @Test
    public void testGetOrder() throws Exception {
        String orderId = "order-123";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PROCESSED);
        order.setTotalAmount(150.00);

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(orderId);
        responseDTO.setStatus("PROCESSED");
        responseDTO.setTotalAmount(150.00);

        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.totalAmount").value(150.00));
    }

    @Test
    public void testGetOrders() throws Exception {
        Order order = new Order();
        order.setId("order-123");
        order.setStatus(OrderStatus.PROCESSED);
        order.setTotalAmount(150.00);

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId("order-123");
        responseDTO.setStatus("PROCESSED");
        responseDTO.setTotalAmount(150.00);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Order> page = new PageImpl<>(Collections.singletonList(order), pageable, 1);

        Mockito.when(orderService.getOrders(any(Pageable.class))).thenReturn(page);
        Mockito.when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("order-123"))
                .andExpect(jsonPath("$.content[0].status").value("PROCESSED"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(150.00));
    }
}
