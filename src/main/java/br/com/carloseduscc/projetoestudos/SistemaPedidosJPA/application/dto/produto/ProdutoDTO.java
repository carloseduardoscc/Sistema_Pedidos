package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "DTO de representação de produto")
public record ProdutoDTO(
        @Schema(description = "Identificador único do produto", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @NotBlank(message = "Nome do produto não pode estar em branco")
        @Schema(description = "Nome do produto", example = "Notebook")
        String nome,
        @Schema(description = "Preço unitário do produto", example = "2500.00")
        @DecimalMin(value = "0.01", message = "Preço unitário do produto deve ser maior que 0.01")
        @NotNull(message = "Preço unitário não deve ser nulo")
        BigDecimal precoUnitario
)
{}
