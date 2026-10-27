package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.atualizacaoStatus.AtualizacaoStatusDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.AbrirPedidoResponseDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.PedidoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.PedidoDetalhadoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.AtualizacaoStatus;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class, UsuarioMapper.class})
public interface PedidoMapper {

    @Mapping(target = "totalItens", expression = "java( pedido.getItens().size() )")
    @Mapping(target = "valorTotal", expression = "java( pedido.getTotal() )")
    PedidoDetalhadoDTO toDTODetalhado(Pedido pedido);

    PedidoDTO toDTO(Pedido pedido);

    // TO RESPONSES
    AbrirPedidoResponseDTO toAbrirPedidoResponseDto(Pedido pedido);

    // Atualização de status
    AtualizacaoStatusDTO toDTO(AtualizacaoStatus atualizacaoStatus);
}
