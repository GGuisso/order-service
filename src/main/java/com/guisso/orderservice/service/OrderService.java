package com.guisso.orderservice.service;
 
import com.guisso.orderservice.exception.OrderNotFoundException;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.OrderStatus;
import com.guisso.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Slf4j
@Service
public class OrderService {
 
    private final OrderRepository orderRepository;
 
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
 
    @Cacheable(value = "orders", key = "#id")
    @Transactional(readOnly = true)
    public Order getOrderById(String id) {
        log.info("Buscando pedido no banco para o ID: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido não encontrado para o ID: {}", id);
                    return new OrderNotFoundException("Pedido não encontrado: " + id);
                });
    }
 
    @Transactional(readOnly = true)
    public Page<Order> getOrders(Pageable pageable) {
        log.info("Recuperando página de pedidos");
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.info("Recuperando pedidos com status: {}", status);
        return orderRepository.findByStatus(status, pageable);
    }
}