package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(exclude = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "usuario_tb", schema = "order_management")
public class Usuario {

    {
        this.pedidos = new ArrayList<>();
        this.ativo = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "email", length = 254, unique = true)
    private String email;

    @Column
    private String senha;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    // Relação
    @OneToMany(mappedBy = "usuario")
    List<Pedido> pedidos;

    // Estado
    @Column(name = "ativo")
    Boolean ativo;

    // Auditoria
    @CreatedDate
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @CreatedBy
    @Column(name = "criado_por")
    private UUID criadoPor;

    // Controle de concorrência otimista
    @Version
    private Long version;

}
