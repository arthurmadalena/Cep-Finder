package br.com.arthur.madalena.cepmanager.security;

import br.com.arthur.madalena.cepmanager.dao.UsuarioDAO;
import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        
        if (!usuario.getAtivo()) {
            throw new org.springframework.security.authentication.DisabledException("Usuário inativo");
        }
        
        if (!usuario.getEmailVerificado()) {
            throw new org.springframework.security.authentication.DisabledException("Email não verificado");
        }
        
        return new UserDetailsImpl(usuario);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioDAO.findByUsername(username);
    }

    public Usuario createUser(@Valid RegistroUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEmail(dto.getEmail());
        usuario.setNomeCompleto(dto.getNomeCompleto());
        return usuarioDAO.save(usuario);
    }
}

