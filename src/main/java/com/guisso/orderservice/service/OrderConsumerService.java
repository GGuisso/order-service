package com.guisso.orderservice.service;
 
import com.guisso.orderservice.config.RabbitMQConfig;
import com.guisso.orderservice.dto.OrderRequestDTO;
import com.guisso.orderservice.exception.DuplicateOrderException;
import com.guisso.orderservice.mapper.OrderMapper;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.OrderStatus;
import com.guisso.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Slf4j
@Service
public class OrderConsumerService {
 
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
 
    public OrderConsumerService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
 
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    @Transactional
    public void receiveMessage(OrderRequestDTO orderRequestDTO) {
        log.info("Mensagem recebida na fila para o pedido: {}", orderRequestDTO.getId());
        log.debug("Payload recebido: {}", orderRequestDTO);
 
        //Verifica se já existe, registra e ignora
        if (orderRepository.existsById(orderRequestDTO.getId())) {
            log.warn("Pedido duplicado identificado: {}", orderRequestDTO.getId());
            return;
        }
 
        try {
            //Converter o DTO para a entidade Order
            Order order = orderMapper.orderRequestDTOToOrder(orderRequestDTO);
            log.info("Conversão de DTO para entidade realizada para o pedido: {}", order.getId());
 
            //Calcular o valor total (soma dos preços de cada produto)
            double total = order.getProducts().stream()
                    .mapToDouble(product -> product.getPrice())
                    .sum();
            // Arredondar para duas casas decimais
            BigDecimal totalArredondado = BigDecimal.valueOf(total)
                    .setScale(2, RoundingMode.HALF_UP);
            order.setTotalAmount(totalArredondado.doubleValue());
            
            log.info("Valor total calculado para o pedido {}: {}", order.getId(), total);
 
            //Definir status do pedido
            order.setStatus(OrderStatus.PROCESSED);
 
            orderRepository.save(order);
            log.info("Pedido {} salvo com sucesso no MongoDB", order.getId());
 
        } catch (Exception ex) {
            log.error("Erro inesperado no processamento do pedido {}: {}", orderRequestDTO.getId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}