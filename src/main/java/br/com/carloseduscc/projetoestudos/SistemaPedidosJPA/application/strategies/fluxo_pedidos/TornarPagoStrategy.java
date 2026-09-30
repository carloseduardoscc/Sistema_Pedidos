package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.strategies.fluxo_pedidos;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;

import java.util.UUID;

public interface TornarPagoStrategy {
    public void executar(UUID idPedido);
}
