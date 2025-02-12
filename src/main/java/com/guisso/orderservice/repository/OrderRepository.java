package com.guisso.orderservice.repository;
 
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.OrderStatus;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
 
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
	Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}