package com.guisso.orderservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitMQConfigTest {

    private final RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();

    @Test
    public void testQueue() {
        Queue queue = rabbitMQConfig.queue();
        assertNotNull(queue);
        assertEquals(RabbitMQConfig.ORDER_QUEUE, queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    public void testExchange() {
        DirectExchange exchange = rabbitMQConfig.exchange();
        assertNotNull(exchange);
        assertEquals(RabbitMQConfig.ORDER_EXCHANGE, exchange.getName());
    }

    @Test
    public void testBinding() {
        Queue queue = rabbitMQConfig.queue();
        DirectExchange exchange = rabbitMQConfig.exchange();
        Binding binding = rabbitMQConfig.binding(queue, exchange);
        assertNotNull(binding);
        assertEquals(RabbitMQConfig.ORDER_ROUTING_KEY, binding.getRoutingKey());
    }

    @Test
    public void testMessageConverter() {
        MessageConverter converter = rabbitMQConfig.jsonMessageConverter();
        assertNotNull(converter);
        assertTrue(converter instanceof org.springframework.amqp.support.converter.Jackson2JsonMessageConverter);
    }
}
