package com.guisso.orderservice.model;
 
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
 
import java.util.List;
 
@Data
@Document(collection = "orders")
public class Order {
 
    @Id
    private String id;
    private Double totalAmount;
    private OrderStatus status;
    private List<Product> products;
}