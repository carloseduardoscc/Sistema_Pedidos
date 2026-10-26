package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.comand.AdicionarItemPedidoCommand;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.ItemAdicionadoResponseDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.PedidoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.PedidoDetalhadoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.ItemPedidoMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.PedidoMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.query_filters.RequisicaoFiltroPedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarCanceladoStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarEntregueStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarEnviadoStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarPagoStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.ItemPedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.PedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.ProdutoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.ItemPedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Produto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoMapper itemPedidoMapper;
    private final PedidoMapper pedidoMapper;
    private final TornarPagoStrategy tornarPagoStrategy;
    private final TornarEnviadoStrategy tornarEnviadoStrategy;
    private final TornarEntregueStrategy tornarEntregueStrategy;
    private final TornarCanceladoStrategy tornarCanceladoStrategy;

    public PedidoDetalhadoDTO obterDetalhes(UUID id) {
        Pedido pedido = pedidoRepository.buscarPedidoFetchProdutoItemAtualizacaostatus(id)
                .orElseThrow(getPedidoNaoEncontradoException(id));

        return pedidoMapper.toDTODetalhado(pedido);
    }

    public Page<PedidoDTO> pesquisarListagem(Pageable pageable, RequisicaoFiltroPedido parametros) {
        Page<Pedido> page = pedidoRepository.findAll(parametros.toSpecification(), pageable);
        return page.map(pedidoMapper::toDTO);
    }

    @Transactional
    public ItemAdicionadoResponseDTO adicionarItem(UUID idPedido, AdicionarItemPedidoCommand dadosNovoItem) {
        Pedido pedido = pedidoRepository.buscarPedidoFetchProdutoItemAtualizacaostatus(idPedido)
                .orElseThrow(getPedidoNaoEncontradoException(idPedido));

        Produto produto = produtoRepository.findById(dadosNovoItem.produtoId())
                .orElseThrow(getProdutoNaoEncontradoException(dadosNovoItem.produtoId()));

        ItemPedido itemPedido = new ItemPedido(dadosNovoItem.quantidade(), produto, pedido);

        // Caso já exista um item com o mesmo produto, apenas atualiza a quantidade
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto().getId().equals(produto.getId())) {
                item.acrescentarQuantidade(dadosNovoItem.quantidade());
                ItemPedido itemSalvo = itemPedidoRepository.save(item);
                return itemPedidoMapper.toPedidoAdicionadoResponseDTO(itemSalvo);
            }
        }

        pedido.adicionarItem(itemPedido);
        ItemPedido itemSalvo = itemPedidoRepository.save(itemPedido);
        ItemAdicionadoResponseDTO respostaDTO = itemPedidoMapper.toPedidoAdicionadoResponseDTO(itemSalvo);
        return respostaDTO;
    }

    public void tornarPago(UUID id) {
        tornarPagoStrategy.executar(id);
    }

    public void tornarEnviado(UUID id) {
        tornarEnviadoStrategy.executar(id);
    }

    public void tornarEntregue(UUID id) {
        tornarEntregueStrategy.executar(id);
    }

    @Transactional
    public void tornarCancelado(UUID id) {
        tornarCanceladoStrategy.executar(id);
    }

    private static Supplier<NaoEncontradoException> getPedidoNaoEncontradoException(UUID id) {
        return () -> new NaoEncontradoException("Pedido com Id: " + id.toString() + " não encontrado");
    }

    private static Supplier<NaoEncontradoException> getProdutoNaoEncontradoException(UUID id) {
        return () -> new NaoEncontradoException("Produto com Id: " + id.toString() + " não encontrado");
    }
}

