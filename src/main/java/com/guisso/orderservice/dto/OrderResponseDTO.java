package com.guisso.orderservice.dto;
 
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
 
import java.util.List;
 
@Data
@Schema(description = "Objeto de resposta com os dados do pedido processado")
public class OrderResponseDTO {
 
    @Schema(description = "Id do pedido", example = "order-123")
    private String id;
 
    @Schema(description = "Valor total do pedido", example = "150.50")
    private Double totalAmount;
 
    @Schema(description = "Status do pedido", example = "PROCESSED")
    private String status;
 
    @Schema(description = "Lista de produtos do pedido")
    private List<ProductDTO> products;
}