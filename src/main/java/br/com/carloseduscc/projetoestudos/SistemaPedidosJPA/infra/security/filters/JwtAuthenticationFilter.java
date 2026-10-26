package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.filters;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.UsuarioService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.CustomAuthentication;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.services.JwtService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService, UsuarioService usuarioService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (request.getCookies() == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = Arrays.stream(request.getCookies())
                    .filter(cookie -> "access_token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            final String userEmail = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                Optional<Usuario> usuarioOpt = usuarioService.obterPorLogin(userEmail);

                if (usuarioOpt.isEmpty()) {
                    log.info("Tentativa de login recusada, usuário não existe:\n{}", userEmail);
                    throw new UsernameNotFoundException("Usuário não encontrado");
                }

                Usuario usuario = usuarioOpt.get();

                if (jwtService.isTokenValid(jwt, usuario)) {

                    CustomAuthentication customAuthentication = new CustomAuthentication(usuario);

                    SecurityContextHolder.getContext().setAuthentication(customAuthentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}