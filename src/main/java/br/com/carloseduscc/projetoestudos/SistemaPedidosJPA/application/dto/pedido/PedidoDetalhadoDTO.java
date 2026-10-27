package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.atualizacaoStatus.AtualizacaoStatusDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.item.ItemResumoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario.UsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(name = "Resposta detalhada de pedido")
public record PedidoDetalhadoDTO(
        UUID id,
        @Schema(description = "Data e hora de abertura do pedido", example = "2026-07-18T18:55:29.334996")
        LocalDateTime dataHoraPedido,
        @Schema(description = "Status do pedido")
        StatusPedido status,
        List<AtualizacaoStatusDTO> atualizacoesDeStatus,
        @Schema(example = "1")
        Integer totalItens,
        @Schema(example = "76.99")
        BigDecimal valorTotal,
        UsuarioDTO responsavel,
        @Schema(description = "Itens pertencentes ao pedido")
        Set<ItemResumoDTO> itens


) {
}
