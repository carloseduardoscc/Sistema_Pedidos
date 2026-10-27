package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {
    UUID id(UUID id);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Query("""
        SELECT DISTINCT u
        FROM Usuario u
        LEFT JOIN FETCH u.pedidos p
        WHERE u.role = :role
""")
    List<Usuario> findByRoleFetchPedido(@Param("role") Role role);

    List<Usuario> findByRole(Role role);

    @Query("""
        SELECT DISTINCT u
        FROM Usuario u
        LEFT JOIN FETCH u.pedidos p
        WHERE u.role = :role AND p = :pedido
        
""")
    Usuario findByContainingPedidoAndRole(@Param("role") Role role, @Param("pedido") Pedido pedido);
}