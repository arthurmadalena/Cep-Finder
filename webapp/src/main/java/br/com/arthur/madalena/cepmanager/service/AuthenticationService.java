package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.exception.BusinessException;
import br.com.arthur.madalena.cepmanager.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(String username, String password) {
        log.info("Tentando login para username: {}", username);
        
        final Usuario usuario = userDetailsService.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", username);
                    return new BusinessException("Credenciais incorretas");
                });
        
        log.info("Usuário encontrado: {}", usuario.getUsername());
        log.debug("Password fornecida: {}", password);
        log.debug("Password no banco: {}", usuario.getPassword());
        
        boolean matches = passwordEncoder.matches(password, usuario.getPassword());
        log.info("Password matches: {}", matches);

        if (!matches) {
            log.error("Senha incorreta para usuário: {}", username);
            throw new BusinessException("Credenciais incorretas");
        }

        if (!usuario.getAtivo()) {
            log.error("Usuário inativo: {}", username);
            throw new BusinessException("Usuário inativo");
        }

        if (!usuario.getEmailVerificado()) {
            log.error("Email não verificado para usuário: {}", username);
            throw new BusinessException("Email não verificado");
        }

        log.info("Login bem-sucedido para usuário: {}", username);
        return jwtService.encode(usuario);
    }

    public String register(@Valid RegistroUsuarioDTO dto) {
        userDetailsService.findByUsername(dto.getUsername()).ifPresent(usuario -> {
            throw new BusinessException("Usuário já existe");
        });

        final Usuario usuario = userDetailsService.createUser(dto);
        return jwtService.encode(usuario);
    }
}

