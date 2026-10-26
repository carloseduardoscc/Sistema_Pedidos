package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth.CadastrarUsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth.UsernamePasswordCredentialsDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario.UsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.UsuarioMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.validator.UsuarioValidator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.CustomAuthentication;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.RefreshTokenService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.services.JwtService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    final private UsuarioRepository repository;
    final private UsuarioMapper mapper;
    final private UsuarioValidator validator;
    final private JwtService jwtService;
    final private BCryptPasswordEncoder passwordEncoder;
    final private RefreshTokenService refreshTokenService;
    final private AuthenticationConfiguration authenticationConfiguration;


    public UsuarioDTO cadastrarUsuario(CadastrarUsuarioDTO usuarioCmd) {
        Usuario usuario = mapper.fromCommand(usuarioCmd);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        validator.validarCadastro(usuario);
        Usuario usuarioSalvo = repository.save(usuario);
        return mapper.toDTO(usuarioSalvo);
    }

    public TokensResponseDTO autenticar(UsernamePasswordCredentialsDTO credentials) {
        Authentication authentication = authenticationConfiguration.getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.username(),
                        credentials.password()
                )
        );

        if (authentication instanceof CustomAuthentication customAuthentication) {
            return gerarTokens(customAuthentication.getUsuario());
        } else {
            throw new RuntimeException("A authentication registrada no security context holder não é uma CustomAuthentication como previsto, é na verdade uma: " + authentication.getClass().getName());
        }
    }

    public TokensResponseDTO gerarTokens(Usuario usuario){
        Map<String, Object> userRoleClaim = Map.of("role", usuario.getRole().toString());

        String accessToken = jwtService.generateAccessToken(userRoleClaim, usuario);
        var refreshToken = refreshTokenService.criar(usuario);

        return new TokensResponseDTO(
                accessToken,
                refreshToken.token(),
                jwtService.extractExpiration(accessToken),
                refreshToken.expiresAt()
        );
    }

    @Transactional
    public TokensResponseDTO rotacionar(String token) {;
        var rotated = refreshTokenService.rotacionar(token);

        Usuario usuario = rotated.usuario();

        String accessToken = jwtService.generateAccessToken(usuario);
        var refreshToken = rotated.refreshToken();

        return new TokensResponseDTO(
                accessToken,
                refreshToken.token(),
                jwtService.extractExpiration(accessToken),
                refreshToken.expiresAt()
        );
    }

    public void revogarRefreshToken(String refreshToken) {
        refreshTokenService.revogar(refreshToken);
    }

    public record TokensResponseDTO(
            String accessToken,
            String refreshToken,
            ZonedDateTime accessTokenExpiresAt,
            ZonedDateTime refreshTokenExpiresAt
    )
    {}

}
