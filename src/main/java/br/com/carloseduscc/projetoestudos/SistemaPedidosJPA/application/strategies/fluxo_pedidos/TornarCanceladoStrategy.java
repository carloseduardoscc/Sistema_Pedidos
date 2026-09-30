package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos;

import java.util.UUID;

public interface TornarCanceladoStrategy {
    public void executar(UUID idPedido);
}
