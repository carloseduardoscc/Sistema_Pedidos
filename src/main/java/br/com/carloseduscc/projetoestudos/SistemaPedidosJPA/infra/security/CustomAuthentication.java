package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomAuthentication implements Authentication {

    private final Usuario usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(usuario.getRole().toString()));
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getDetails() {
        return this.usuario;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.usuario;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return usuario.getEmail();
    }
}
