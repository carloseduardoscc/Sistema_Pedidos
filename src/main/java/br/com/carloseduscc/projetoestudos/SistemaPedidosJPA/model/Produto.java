package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.exception.RegraDeNegocioException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.util.Formatador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "produto_tb", schema="stock")
@Check(constraints = "preco_unitario >= 0")
@EntityListeners(AuditingEntityListener.class)
public class Produto {

    private final static BigDecimal MAXIMO_PRECO_UNITARIO = new BigDecimal(10000);
    private final static BigDecimal MINIMO_PRECO_UNITARIO = new BigDecimal("0.01");

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "preco_unitario", nullable = false, precision = 18, scale = 2)
    private BigDecimal precoUnitario;

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

    // Setters
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        if (precoUnitario.compareTo(MINIMO_PRECO_UNITARIO) < 0){
            throw new RegraDeNegocioException("Não são permitidos preços unitários de itens de pedido menores que "+Formatador.formatarDinheiro(MINIMO_PRECO_UNITARIO));
        }
        if (precoUnitario.compareTo(MAXIMO_PRECO_UNITARIO) > 0){
            throw new RegraDeNegocioException("Não são permitidos preços unitários de itens de pedido maiores que "+ Formatador.formatarDinheiro(MAXIMO_PRECO_UNITARIO));
        }
        this.precoUnitario = precoUnitario;
    }
}
