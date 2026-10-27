package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.specs;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PedidoSpecs {
    public static Specification<Pedido> initialize(){
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Pedido> isBefore(LocalDateTime maxDate){
        return (root, query, cb) -> {
            return cb.lessThanOrEqualTo(root.get("dataHoraPedido"), maxDate);
        };
    }

    public static Specification<Pedido> isAfter(LocalDateTime minDate){
        return (root, query, cb) -> {
            return cb.greaterThanOrEqualTo(root.get("dataHoraPedido"), minDate);
        };
    }

    public static Specification<Pedido> usuarioOrResponsavelEmailIsLike(String usuarioEmail) {
        return (root, query, cb) -> {
            var usuarioJoin = root.join("usuario", JoinType.LEFT);
            var responsavelJoin = root.join("responsavel", JoinType.LEFT);
            return cb.or(
                    cb.like(
                            cb.upper(usuarioJoin.get("email")),
                            "%" + usuarioEmail.toUpperCase() + "%"
                    ),
                    cb.like(
                            cb.upper(responsavelJoin.get("email")),
                            "%" + usuarioEmail.toUpperCase() + "%"
                    )
            );
        };
    }
}
