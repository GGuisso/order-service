package com.guisso.orderservice.dto;
 
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
 
@Data
@Schema(description = "Objeto de transferência de dados de produto")
public class ProductDTO {
 
    @NotBlank(message = "O ID do produto não pode ser vazio")
    @Schema(description = "Id do produto", example = "prod-001")
    private String id;
 
    @NotBlank(message = "O nome do produto não pode ser vazio")
    @Schema(description = "Nome do produto", example = "Camiseta")
    private String name;
 
    @NotNull(message = "O preço do produto não pode ser nulo")
    @Positive(message = "O preço do produto deve ser positivo")
    @Schema(description = "Preço do produto", example = "49.99")
    private Double price;
}