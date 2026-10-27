package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.AtualizacaoStatusRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.PedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Esse conjunto de implementações é do fluxo de pedidos para teste
 * Ao realizar alguma ação ele passa o pedido para um responsável do primeiro usuário do cargo correto
 *
 * Ex.: Ao tornar um pedido pago, se busca o primeiro Lojista do banco para atribuir o pedido a ele como responsável
 */

@Configuration
@AllArgsConstructor
@Profile("test")
public class FluxoPedidosTestImplConfig {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AtualizacaoStatusRepository atualizacaoStatusRepository;

    @Bean
    public TornarPagoStrategy tornarPagoStrategyTestImplementation(){
        return new TornarPagoStrategy() {
            @Override
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                List<Usuario> lojistas = usuarioRepository.findByRoleFetchPedido(Role.LOJISTA);
                Collections.shuffle(lojistas);
                Usuario lojista = lojistas.getFirst();

                lojista.getPedidos().add(pedido);
                pedido.setResponsavel(lojista);
                pedido.setDataHoraPedido(LocalDateTime.now());
                AtualizacaoStatus atualizacaoStatus = pedido.mudarStatus(StatusPedido.PAGO);

                atualizacaoStatusRepository.save(atualizacaoStatus);
                pedidoRepository.save(pedido);
                usuarioRepository.save(lojista);
            }
        };
    }

    @Bean
    public TornarEnviadoStrategy tornarEnviadoStrategyTestImplementation(){
        return new TornarEnviadoStrategy() {
            @Override
            @Transactional
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                Usuario lojistaResponsavel = pedido.getResponsavel();
                List<Usuario> entregadores = usuarioRepository.findByRoleFetchPedido(Role.ENTREGADOR);
                Collections.shuffle(entregadores);

                lojistaResponsavel.getPedidos().remove(pedido);
                Usuario entregador = entregadores.getFirst();
                pedido.setResponsavel(entregador);
                entregador.getPedidos().add(pedido);
                AtualizacaoStatus atualizacaoStatus = pedido.mudarStatus(StatusPedido.ENVIADO);

                atualizacaoStatusRepository.save(atualizacaoStatus);
                pedidoRepository.save(pedido);
                usuarioRepository.saveAll(List.of(lojistaResponsavel, entregador));
            }
        };
    }

    @Bean
    public TornarEntregueStrategy tornarEntregueStrategyTestImplementation(){
        return new TornarEntregueStrategy() {
            @Override
            @Transactional
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                Usuario entregadorResponsavel = pedido.getResponsavel();
                pedido.setResponsavel(null);
                AtualizacaoStatus atualizacaoStatus = pedido.mudarStatus(StatusPedido.ENTREGUE);

                atualizacaoStatusRepository.save(atualizacaoStatus);
                usuarioRepository.save(entregadorResponsavel);
                pedidoRepository.save(pedido);
            }
        };
    }

    @Bean
    public TornarCanceladoStrategy tornarCanceladoStrategyTestImplementation(){
        return new TornarCanceladoStrategy() {
            @Override
            @Transactional
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                AtualizacaoStatus atualizacaoStatus = pedido.mudarStatus(StatusPedido.CANCELADO);
                pedido.setResponsavel(null);

                pedidoRepository.save(pedido);
                atualizacaoStatusRepository.save(atualizacaoStatus);
            }
        };
    }

    private Pedido buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(UUID id) {
        return pedidoRepository.buscarPedidoFetchUsuarioFetchAtualizacaoPedido(id)
                .orElseThrow(()->new  NaoEncontradoException("Pedido com Id: " + id.toString() + " não encontrado"));
    }
}
