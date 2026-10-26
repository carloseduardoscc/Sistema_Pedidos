package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@ToString(exclude = "pedido")
@Table(name = "atualizacao_status_tb", schema = "order_management")
@NoArgsConstructor
public class AtualizacaoStatus {

    public AtualizacaoStatus(StatusPedido status) {
        this.status = status;
        this.dataHora = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    // Relação
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
}
