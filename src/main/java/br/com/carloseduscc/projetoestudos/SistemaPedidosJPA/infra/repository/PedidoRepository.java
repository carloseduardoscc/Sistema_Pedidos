package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.StatusPedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByStatus(StatusPedido status);

    @Query("""
        SELECT ped
        FROM Pedido ped
        JOIN ped.itens i
        JOIN i.produto prod
        GROUP BY ped
        HAVING SUM(prod.precoUnitario * i.quantidade) > ?1
""")
    List<Pedido> buscarPedidoComTotalMaiorQue(BigDecimal valorMinimo);

    @Query("""
    SELECT DISTINCT ped
    FROM Pedido ped
    LEFT JOIN FETCH ped.itens i
    LEFT JOIN FETCH i.produto
    LEFT JOIN FETCH ped.atualizacoesDeStatus
    WHERE ped.id = :id
""")
    Optional<Pedido> buscarPedidoFetchProdutoItemAtualizacaostatus(@Param("id") UUID id);

    @Modifying
    @Query("""
        UPDATE Pedido p
        SET p.status = :status
        WHERE p.id = :id
""")
    void atualizarStatus(@Param("id") UUID id, @Param("status") StatusPedido status);

    boolean existsByUsuarioAndStatusIn(Usuario usuario, List<StatusPedido> status);

    boolean existsByUsuarioAndStatus(Usuario usuario, StatusPedido status);

    @Query("""
    SELECT p
    FROM Pedido p
    JOIN FETCH p.itens i
    WHERE i.id = :id
""")
    Optional<Pedido> buscarPedidoPorItemId(@Param("id") UUID id);

    @Query("""
    SELECT DISTINCT p
    FROM Pedido p
    JOIN FETCH p.usuario
    JOIN FETCH p.atualizacoesDeStatus
    WHERE p.id = :id
""")
    Optional<Pedido> buscarPedidoFetchUsuarioFetchAtualizacaoPedido(@Param("id") UUID id);
}
