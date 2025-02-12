package com.guisso.orderservice.dto;
 
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
 
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
 
@Data
@Schema(description = "Objeto de requisição para criação de um pedido")
public class OrderRequestDTO {
 
    @NotBlank(message = "O ID do pedido não pode ser vazio")
    @Schema(description = "Id do pedido", example = "order-123")
    private String id;
 
    @NotNull(message = "A lista de produtos não pode ser nula")
    @Size(min = 1, message = "O pedido deve conter pelo menos um produto")
    @Valid
    @Schema(description = "Lista de produtos do pedido")
    private List<ProductDTO> products;
}