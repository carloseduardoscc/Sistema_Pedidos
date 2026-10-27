package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.comand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(name = "Requisição para atualizar item")
public record AtualizarItemCommand(
        @Min(value = 1, message = "Quantidade de itens não pode ser menor que um")
        @Max(value = 100, message = "Quantidade de itens não pode ser maior que 100")
        @Schema(minLength = 1, maxLength = 100, example = "5")
        Integer quantidade
) {
}
