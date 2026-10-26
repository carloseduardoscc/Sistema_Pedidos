package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.RefreshTokenInvalidoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.util.HashGenerator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.util.RefreshTokenGenerator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.RefreshTokenRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.RefreshTokenService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.RefreshToken;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenGenerator mockTokenGenerator;
    @Mock
    private RefreshTokenRepository mockRepository;
    @Mock
    private HashGenerator mockHashGenerator;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("Ao criar token deve armazenar somente hash")
    void aoCriarTokenDeveArmazenarSomenteHash() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Test User");
        usuario.setEmail("user@test.com");

        String tokenCru = "rawTokenString";
        String tokenHash = "hashedTokenString";

        when(mockTokenGenerator.generate()).thenReturn(tokenCru);
        when(mockHashGenerator.sha256(tokenCru)).thenReturn(tokenHash);

        ArgumentCaptor<RefreshToken> capturador = ArgumentCaptor.forClass(RefreshToken.class);

        // Act
        RefreshTokenService.RefreshTokenEmitidoDTO dto = refreshTokenService.criar(usuario);

        // Assert
        verify(mockRepository).save(capturador.capture());
        RefreshToken tokenSalvo = capturador.getValue();
        assertEquals(tokenHash, tokenSalvo.getTokenHash());
        assertNotNull(dto);
        assertEquals(tokenCru, dto.token());
    }

    @Test
    @DisplayName("Ao rotacionar token deve revogar antigo e emitir um novo válido")
    void aoRotacionarTokenDeveRevogarAntigoEEmitirUmNovoValido() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Test User");

        String tokenCruAntigo = "oldToken";
        String tokenHashAntigo = "oldHashedToken";

        when(mockHashGenerator.sha256(tokenCruAntigo)).thenReturn(tokenHashAntigo);

        RefreshToken tokenAntigo = RefreshToken.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .tokenHash(tokenHashAntigo)
                .expiraEm(Instant.now().plusSeconds(3600))
                .criadoEm(Instant.now())
                .build();

        when(mockRepository.findByTokenHashForUpdate(tokenHashAntigo)).thenReturn(Optional.of(tokenAntigo));

        String tokenCruNovo = "newToken";
        String tokenHashNovo = "newHashedToken";

        when(mockTokenGenerator.generate()).thenReturn(tokenCruNovo);
        when(mockHashGenerator.sha256(tokenCruNovo)).thenReturn(tokenHashNovo);

        // Act
        RefreshTokenService.RefreshTokenRotacionadoDTO tokenRotacionado = refreshTokenService.rotacionar(tokenCruAntigo);

        // Assert
        assertTrue(tokenAntigo.isRevogado());
        assertNotNull(tokenRotacionado.refreshToken());

        verify(mockRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Ao rotacionar token expirado deve lançar exceção")
    void aoRotacionarTokenExpiradoDeveLancarExcecao() {
        // Arrange
        String tokenCru = "rawToken";
        String tokenHash = "hashedToken";

        when(mockHashGenerator.sha256(tokenCru)).thenReturn(tokenHash);

        RefreshToken tokenExpirado = RefreshToken.builder()
                .id(UUID.randomUUID())
                .usuario(new Usuario())
                .tokenHash(tokenHash)
                .expiraEm(Instant.now().minusSeconds(1))
                .criadoEm(Instant.now())
                .revogado(false)
                .build();

        when(mockRepository.findByTokenHashForUpdate(tokenHash)).thenReturn(Optional.of(tokenExpirado));

        // Act & Assert
        assertThrows(RefreshTokenInvalidoException.class, () -> refreshTokenService.rotacionar(tokenCru));
        assertFalse(tokenExpirado.isRevogado());
    }

    @Test
    @DisplayName("Ao rotacionar token revogado deve lançar exceção")
    void aoRotacionarTokenRevogadoDeveLancarExcecao() {
        // Arrange
        String tokenCru = "rawToken";
        String tokenHash = "hashedToken";

        when(mockHashGenerator.sha256(tokenCru)).thenReturn(tokenHash);

        RefreshToken tokenExpirado = RefreshToken.builder()
                .id(UUID.randomUUID())
                .usuario(new Usuario())
                .tokenHash(tokenHash)
                .expiraEm(Instant.now().plusSeconds(3600))
                .criadoEm(Instant.now())
                .revogado(true)
                .build();

        when(mockRepository.findByTokenHashForUpdate(tokenHash)).thenReturn(Optional.of(tokenExpirado));

        // Act & Assert
        assertThrows(RefreshTokenInvalidoException.class, () -> refreshTokenService.rotacionar(tokenCru));
        assertTrue(tokenExpirado.isRevogado());
    }


}