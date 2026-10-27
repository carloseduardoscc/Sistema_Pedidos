package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.RefreshTokenInvalidoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.util.HashGenerator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.util.RefreshTokenGenerator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.RefreshTokenRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.RefreshToken;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenGenerator tokenGeneration;
    private final RefreshTokenRepository repository;
    private final HashGenerator geradorHash;
    @Value("${spring.config.zone-id}")
    private String zoneId;
    @Value("${spring.security.refresh-token.expiration-time}")
    private long lifetime;

    @Transactional
    public RefreshTokenEmitidoDTO criar(Usuario usuario) {
        String tokenCru = tokenGeneration.generate();
        String tokenHash = geradorHash.sha256(tokenCru);

        Instant agora = Instant.now().atZone(ZoneId.of(zoneId)).toInstant();
        Instant expiraEm = agora.plusMillis(lifetime);

        RefreshToken token = RefreshToken.builder()
                .usuario(usuario)
                .tokenHash(tokenHash)
                .expiraEm(expiraEm)
                .criadoEm(agora)
                .build();

        repository.save(token);

        var expiraEmTimeZoned = expiraEm.atZone(ZoneId.of(zoneId));
        return new RefreshTokenEmitidoDTO(
                tokenCru,
                expiraEmTimeZoned
        );
    }

    @Transactional
    public RefreshTokenRotacionadoDTO rotacionar(String tokenCru) {
        String tokenHash = geradorHash.sha256(tokenCru);

        RefreshToken tokenAtual = repository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(RefreshTokenInvalidoException::new);

        Instant agora = Instant.now();

        if (tokenAtual.isRevogado()
                || !tokenAtual.getExpiraEm().isAfter(agora)) {
            throw new RefreshTokenInvalidoException();
        }

        Usuario usuario = tokenAtual.getUsuario();

        tokenAtual.revoke();

        RefreshTokenEmitidoDTO novoToken = criar(usuario);

        return new RefreshTokenRotacionadoDTO(usuario, novoToken);
    }

    @Transactional
    public void revogar(String tokenCru) {
        String tokenHash = geradorHash.sha256(tokenCru);

        Optional<RefreshToken> token = repository
                .findByTokenHashForUpdate(tokenHash);

        if (token.isPresent() && !token.get().isRevogado()) {
            token.get().revoke();
        }
    }

    public record RefreshTokenRotacionadoDTO(
            Usuario usuario,
            RefreshTokenEmitidoDTO refreshToken
    ) {
    }

    public record RefreshTokenEmitidoDTO(
            String token,
            ZonedDateTime expiresAt
    ) {
    }
}
