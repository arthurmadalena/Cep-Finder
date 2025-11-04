package br.com.arthur.madalena.cepmanager.security;

import br.com.arthur.madalena.cepmanager.dao.UsuarioDAO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioDAO usuarioDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        if (!usuario.getAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + username);
        }

        Set<GrantedAuthority> authorities = usuario.getPermissoes().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getAtivo(),
                true,
                true,
                true,
                authorities
        );
    }
}

