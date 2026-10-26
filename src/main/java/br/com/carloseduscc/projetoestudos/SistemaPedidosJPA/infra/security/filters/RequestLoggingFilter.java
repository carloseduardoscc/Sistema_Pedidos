package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<Authentication> authenticationOpt = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        String user = authenticationOpt.isPresent() ? authenticationOpt.get().getName() : "ANONYMOUS";
        String roles = authenticationOpt.isPresent() ? authenticationOpt.get().getAuthorities().toString() : "ANONYMOUS";
        MDC.put("user", user);
        MDC.put("role", roles);

        filterChain.doFilter(request, response);
    }
}
