package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.AuthenticationService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.UsuarioService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth.CadastrarUsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.UsuarioMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.validator.UsuarioValidator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    final private UsuarioMapper mapper;
    private final UsuarioService usuarioService;
    final private UsuarioRepository repository;
    final private UsuarioValidator validator;
    final private AuthenticationService authenticationService;
    final private BCryptPasswordEncoder passwordEncoder;
    private final String SENHA_PADRAO_NOVO_USUARIO = "123456";
    @Value("${spring.config.frontend-url}")
    private String frontendUrl;
    @Value("${spring.security.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;
    @Value("${spring.security.jwt.expiration-time}")
    private long accessTokenExpirationTime;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {

            OAuth2User oauth2User = oAuth2AuthenticationToken.getPrincipal();

            String email = oauth2User.getAttribute("email");

            Optional<Usuario> usuarioEncontradoOpt = usuarioService.obterPorLogin(email);

            Usuario usuario = usuarioEncontradoOpt.orElseGet(() -> criarEPersistirNovoUsuario(oauth2User));

            var tokens = authenticationService.gerarTokens(usuario);

            String redirectUrl = frontendUrl + "/oauth/callback";

            long accessTokenExpirationTimeSeconds = accessTokenExpirationTime / 1000;
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    ResponseCookie
                            .from("access_token", tokens.accessToken())
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Lax")
                            .path("/")
                            .maxAge(accessTokenExpirationTimeSeconds)
                            .build().toString()
            );

            long refreshTokenExpirationTimeSeconds = refreshTokenExpirationTime / 1000;
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    ResponseCookie
                            .from("refresh_token", tokens.refreshToken())
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Lax")
                            .path("/")
                            .maxAge(refreshTokenExpirationTimeSeconds)
                            .build().toString()
            );

            response.sendRedirect(redirectUrl);
        } else {
            throw new RuntimeException("Tipo de autenticação inválido: esperado OAuth2AuthenticationToken, mas recebido " + authentication.getClass().getName());
        }
    }

    private Usuario criarEPersistirNovoUsuario(OAuth2User oAuth2User) {
        String oAuth2UserEmail = oAuth2User.getAttribute("email");
        CadastrarUsuarioDTO cadastrarUsuarioDTO = new CadastrarUsuarioDTO(
                gerarNomeApartirDeEmail(oAuth2UserEmail),
                oAuth2UserEmail,
                SENHA_PADRAO_NOVO_USUARIO,
                Role.getRolePadrao()
        );
        Usuario usuario = mapper.fromCommand(cadastrarUsuarioDTO);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        validator.validarCadastro(usuario);
        return repository.save(usuario);
    }

    private String gerarNomeApartirDeEmail(String email) {
        return email.substring(0, email.indexOf("@"));
    }

}