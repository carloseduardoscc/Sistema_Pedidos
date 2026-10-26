package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.comand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "Requisição para adicionar item")
public record AdicionarItemPedidoCommand(
        @NotNull(message = "Id do produto é obrigatório")
        UUID produtoId,
        @NotNull(message = "Quantidade do item é obrigatório")
        @Min(value = 1, message = "Quantidade de itens não pode ser menor que um")
        @Max(value = 100, message = "Quantidade de itens não pode ser maior que 100")
        @Schema(example = "5")
        Integer quantidade
) {
}
