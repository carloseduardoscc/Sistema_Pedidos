package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.services;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UsuarioRepository repository;

    public Usuario obterUsuarioLogado() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        return null;
    }

    public boolean usuarioLogadoContemRole(Role role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(role.toString()));
    }
}
