package br.com.arthur.madalena.cepmanager.security;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    
    private final Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getPermissoes().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getAtivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getAtivo() && usuario.getEmailVerificado();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}

