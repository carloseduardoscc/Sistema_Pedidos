package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.config;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.services.SecurityService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class JpaConfiguration {

    @Bean
    public AuditorAware<UUID> auditorProvider(SecurityService securityService) {
        return () -> {
            Usuario usuario = securityService.obterUsuarioLogado();
            if (usuario == null) {
                return Optional.empty();
            }
            return Optional.of(usuario.getId());
        };
    }

}
