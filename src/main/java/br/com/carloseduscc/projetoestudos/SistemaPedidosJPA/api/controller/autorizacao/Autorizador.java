package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.autorizacao;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.ItemPedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.PedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.CustomAuthentication;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.ItemPedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component("autorizador")
@RequiredArgsConstructor
public class Autorizador {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public boolean isUsuarioDonoItem(UUID itemId, UUID usuarioId) {
        Optional<ItemPedido> itemOpt = itemPedidoRepository.findById(itemId);
        return itemOpt.map(itemPedido -> itemPedido.getPedido().getUsuario().getId().equals(usuarioId)).orElse(false);
    }

    public boolean isUsuarioDonoPedido(UUID pedidoId, UUID usuarioId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        return pedidoOpt.map(pedido -> pedido.getUsuario().getId().equals(usuarioId)).orElse(false);
    }

    public boolean podeAbrirNovoPedido(Authentication authentication, UUID usuarioAfetadoId) {
        if (authentication instanceof CustomAuthentication customAuthentication) {
            Usuario usuarioAutenticado = customAuthentication.getUsuario();
            if (usuarioAutenticado.getRole().equals(Role.CLIENTE) && usuarioAutenticado.getId().equals(usuarioAfetadoId)) {
                return true;
            }

            Usuario usuarioAfetado = usuarioRepository.findById(usuarioAfetadoId).orElseThrow(() ->
                    new NaoEncontradoException("Usuário informado não existe"));

            if (usuarioAutenticado.getRole().equals(Role.ADMIN) && usuarioAfetado.getRole().equals(Role.CLIENTE)){
                return true;
            }
            return false;
        }else{
            throw new RuntimeException("Não foi registrada uma instância de CustomAuthentication no SecurityContextHolder como Authentication");
        }
    }

}
