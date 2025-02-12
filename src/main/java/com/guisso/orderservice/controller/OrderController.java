package com.guisso.orderservice.controller;
 
import com.guisso.orderservice.config.RabbitMQConfig;
import com.guisso.orderservice.dto.OrderRequestDTO;
import com.guisso.orderservice.dto.OrderResponseDTO;
import com.guisso.orderservice.mapper.OrderMapper;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import jakarta.validation.Valid;
 
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "Endpoints para gerenciamento de pedidos")
@Slf4j
public class OrderController {
 
    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
 
    public OrderController(RabbitTemplate rabbitTemplate, OrderService orderService, OrderMapper orderMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }
 
    @Operation(summary = "Recebe um pedido do Produto Externo A e envia para processamento assíncrono")
    @PostMapping
    public ResponseEntity<String> receiveOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Recebendo pedido via API: {}", orderRequestDTO.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                orderRequestDTO
        );
        log.info("Pedido {} enviado para a fila do RabbitMQ", orderRequestDTO.getId());
        return ResponseEntity.accepted().body("Pedido recebido e em processamento");
    }
 
    @Operation(summary = "Consulta um pedido processado pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String id) {
        log.info("Consulta de pedido realizada para o ID: {}", id);
        Order order = orderService.getOrderById(id);
        OrderResponseDTO responseDTO = orderMapper.orderToOrderResponseDTO(order);
        return ResponseEntity.ok(responseDTO);
    }
 
    @Operation(summary = "Lista os pedidos processados com paginação")
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(Pageable pageable) {
        log.info("Listagem de pedidos solicitada com paginação");
        Page<Order> ordersPage = orderService.getOrders(pageable);
        Page<OrderResponseDTO> dtoPage = ordersPage.map(orderMapper::orderToOrderResponseDTO);
        return ResponseEntity.ok(dtoPage);
    }
}