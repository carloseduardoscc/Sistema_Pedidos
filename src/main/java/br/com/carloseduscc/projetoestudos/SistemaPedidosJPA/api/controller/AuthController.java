package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common.GenericController;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.AuthenticationService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth.CadastrarUsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth.UsernamePasswordCredentialsDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
@Slf4j
public class AuthController implements GenericController {

    private final AuthenticationService service;
    @Value("${spring.security.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;
    @Value("${spring.security.jwt.expiration-time}")
    private long accessTokenExpirationTime;

    @PostMapping("login")
    @Operation(
            summary = "Realizar o login", description = "Realizar o login usando as credenciais debugrmadas no corpo da requisição para validar, e se estiver correta devolver um access token JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content())
    })
    public ResponseEntity<Void> login(
            @RequestBody @Valid UsernamePasswordCredentialsDTO credentials,
            HttpServletResponse response
    ) {
        log.debug("Realizando login para usuário: {}", credentials.username());
        var tokens = service.autenticar(credentials);
        setAccessTokenCookie(response, tokens.accessToken(), accessTokenExpirationTime);
        setRefreshTokenCookie(response, tokens.refreshToken(), refreshTokenExpirationTime);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("refresh")
    @Operation(
            summary = "Rotacionar tokens", description = "Rotaciona o refresh token para obter novos access token e refresh token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens rotacionados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado", content = @Content())
    })
    public ResponseEntity<Void> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = obterRefreshToken(request);
        String coveredTokenForLog = "..." + refreshToken.substring(refreshToken.length() - 3);
        log.debug("Rotacionando refresh token: {}", coveredTokenForLog);
        var tokens = service.rotacionar(refreshToken);
        setAccessTokenCookie(response, tokens.accessToken(), accessTokenExpirationTime);
        setRefreshTokenCookie(response, tokens.refreshToken(), refreshTokenExpirationTime);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("logout")
    @Operation(
            summary = "Realizar logout", description = "Revoga o refresh token debugrmado para realizar o logout do usuário"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado", content = @Content())
    })
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = obterRefreshToken(request);
        if (refreshToken != null) {
            String coveredTokenForLog = "..." + refreshToken.substring(refreshToken.length() - 3);
            log.debug("Revogando refresh token: {}", coveredTokenForLog);
            service.revogarRefreshToken(refreshToken);
        }
        invalidateAccessTokenCookie(response);
        invalidateRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("cadastrar")
    @Operation(
            summary = "Cadastro", description = "Realiza cadastro de usuários"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Cadastro realizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "O e-mail já está sendo utilizado", content = @Content()),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Apenas usuários administradores podem cadastrar novos funcionários", content = @Content())
    })
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody @Valid CadastrarUsuarioDTO usuarioCmd) {
        log.debug("Cadastrando novo usuário {}", usuarioCmd.email());
        UsuarioDTO usuarioSalvo = service.cadastrarUsuario(usuarioCmd);
        URI uri = gerarHeaderLocation(usuarioSalvo.id().toString());
        return ResponseEntity.created(uri).body(usuarioSalvo);
    }


}
