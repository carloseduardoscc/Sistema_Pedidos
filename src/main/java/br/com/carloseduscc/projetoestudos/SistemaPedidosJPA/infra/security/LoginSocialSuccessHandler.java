package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.UsuarioService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LoginSocialSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UsuarioService usuarioService;
    private final String SENHA_PADRAO_NOVO_USUARIO = "123456";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken){
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

            String email = oAuth2User.getAttribute("email");

            Optional<Usuario> usuarioEncontradoOpt = usuarioService.obterPorLogin(email);

            Usuario usuario = usuarioEncontradoOpt.orElseGet(() -> criarEPersistirNovoUsuario(oAuth2User));

            CustomAuthentication customAuthentication = new CustomAuthentication(usuario);

            SecurityContextHolder.getContext().setAuthentication(customAuthentication);

            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private Usuario criarEPersistirNovoUsuario(OAuth2User oAuth2User) {
        String oAuth2UserEmail = oAuth2User.getAttribute("email");
        Usuario usuarioViaOauth2 = new Usuario();
        usuarioViaOauth2.setNome(gerarNomeApartirDeEmail(oAuth2UserEmail));
        usuarioViaOauth2.setEmail(oAuth2UserEmail);
        usuarioViaOauth2.setSenha(SENHA_PADRAO_NOVO_USUARIO);
        usuarioViaOauth2.setRole(Role.getRolePadrao());
        return usuarioViaOauth2;
    }

    private String gerarNomeApartirDeEmail(String email) {
        return email.substring(0, email.indexOf("@"));
    }
}

