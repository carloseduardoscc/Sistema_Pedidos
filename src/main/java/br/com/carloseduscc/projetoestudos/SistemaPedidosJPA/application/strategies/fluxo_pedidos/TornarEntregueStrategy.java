package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos;

import java.util.UUID;

public interface TornarEntregueStrategy {
    public void executar(UUID idPedido);
}
