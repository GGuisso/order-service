package com.guisso.orderservice.mapper;
 
import com.guisso.orderservice.dto.OrderRequestDTO;
import com.guisso.orderservice.dto.OrderResponseDTO;
import com.guisso.orderservice.dto.ProductDTO;
import com.guisso.orderservice.model.Order;
import com.guisso.orderservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
 
@Mapper(componentModel = "spring")
public interface OrderMapper {
 
    //Converte o DTO de requisição para a entidade Order.
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order orderRequestDTOToOrder(OrderRequestDTO dto);
 
    //Converte a entidade Order para o DTO de resposta.
    OrderResponseDTO orderToOrderResponseDTO(Order order);
 
    //Conversões para Product
    Product productDTOToProduct(ProductDTO dto);
    ProductDTO productToProductDTO(Product product);
}