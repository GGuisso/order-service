package com.guisso.orderservice.model;
 
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
 
@Data
@Document(collection = "products")
public class Product {
 
    @Id
    private String id;
    private String name;
    private Double price;
}