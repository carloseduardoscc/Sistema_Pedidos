package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.config;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarCanceladoStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarEntregueStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarEnviadoStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos.TornarPagoStrategy;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.PedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.StatusPedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Configuration
@AllArgsConstructor
public class ApplicationConfiguration {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

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
                pedido.mudarStatus(StatusPedido.PAGO);

                pedidoRepository.save(pedido);
                usuarioRepository.save(lojista);
            }
        };
    }

    @Bean
    public TornarEnviadoStrategy tornarEnviadoStrategyTestImplementation(){
        return new TornarEnviadoStrategy() {
            @Override
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                Usuario lojistaResponsavel = usuarioRepository.findByContainingPedidoAndRole(Role.LOJISTA, pedido);
                List<Usuario> entregadores = usuarioRepository.findByRoleFetchPedido(Role.ENTREGADOR);
                Collections.shuffle(entregadores);

                lojistaResponsavel.getPedidos().remove(pedido);
                Usuario entregador = entregadores.getFirst();
                pedido.setResponsavel(entregador);
                entregador.getPedidos().add(pedido);
                pedido.mudarStatus(StatusPedido.ENVIADO);

                pedidoRepository.save(pedido);
                usuarioRepository.save(lojistaResponsavel);
                usuarioRepository.save(entregador);
            }
        };
    }

    @Bean
    public TornarEntregueStrategy tornarEntregueStrategyTestImplementation(){
        return new TornarEntregueStrategy() {
            @Override
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                pedido.mudarStatus(StatusPedido.ENTREGUE);
            }
        };
    }

    @Bean
    public TornarCanceladoStrategy tornarCanceladoStrategyTestImplementation(){
        return new TornarCanceladoStrategy() {
            @Override
            public void executar(UUID idPedido) {
                Pedido pedido = buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(idPedido);
                pedido.mudarStatus(StatusPedido.CANCELADO);
            }
        };
    }

    private Pedido buscarPedidoPorIdFetchUsuarioFetchAtualizacaoPedido(UUID id) {
        return pedidoRepository.buscarPedidoFetchUsuarioFetchAtualizacaoPedido(id)
                .orElseThrow(()->new  NaoEncontradoException("Pedido com Id: " + id.toString() + " não encontrado"));
    }
}
