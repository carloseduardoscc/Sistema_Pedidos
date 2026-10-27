package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.projections.ItemPedidoDetalhadoProjection;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.AtualizacaoStatus;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface AtualizacaoStatusRepository extends JpaRepository<AtualizacaoStatus, UUID> {
}
