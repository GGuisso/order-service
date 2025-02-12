package com.guisso.orderservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void testHandleOrderNotFoundException() {
        OrderNotFoundException ex = new OrderNotFoundException("Pedido não encontrado: order-123");
        ResponseEntity<Object> response = handler.handleOrderNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map body = (Map) response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));
        assertEquals("Pedido não encontrado: order-123", body.get("message"));
    }

    @Test
    public void testHandleDuplicateOrderException() {
        DuplicateOrderException ex = new DuplicateOrderException("Pedido duplicado");
        ResponseEntity<Object> response = handler.handleDuplicateOrderException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map body = (Map) response.getBody();
        assertEquals(HttpStatus.CONFLICT.value(), body.get("status"));
        assertEquals("Pedido duplicado", body.get("message"));
    }

    @Test
    public void testHandleGeneralException() {
        Exception ex = new Exception("Erro genérico");
        ResponseEntity<Object> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map body = (Map) response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Erro interno no servidor", body.get("message"));
    }
}
