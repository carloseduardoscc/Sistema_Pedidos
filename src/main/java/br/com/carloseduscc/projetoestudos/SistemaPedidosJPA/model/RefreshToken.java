package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "refresh_token", schema="auth")
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    @Builder
    public RefreshToken(UUID id, Usuario usuario, String tokenHash, Instant expiraEm, Instant criadoEm, boolean revogado) {
        this.id = id;
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.expiraEm = expiraEm;
        this.criadoEm = criadoEm;
        this.revogado = revogado;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @CreatedDate
    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "revogado", nullable = false)
    private boolean revogado = false;

    public void revoke(){
        this.revogado = true;
    }
}
