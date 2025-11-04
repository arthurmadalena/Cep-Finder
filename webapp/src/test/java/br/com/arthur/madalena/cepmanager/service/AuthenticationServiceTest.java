package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.exception.BusinessException;
import br.com.arthur.madalena.cepmanager.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setEmail("admin@test.com");
        usuario.setPassword("$2a$10$hashedPassword");
        usuario.setNomeCompleto("Administrador");
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        usuario.setPermissoes(Set.of("ROLE_ADMIN", "ROLE_USER"));
    }

    @Test
    void deveRealizarLoginComSucesso() {
        when(userDetailsService.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.encode(usuario)).thenReturn("token.jwt.valido");

        String token = authenticationService.login("admin", "senha123");

        assertThat(token).isNotNull();
        assertThat(token).isEqualTo("token.jwt.valido");
        verify(userDetailsService).findByUsername("admin");
        verify(passwordEncoder).matches("senha123", "$2a$10$hashedPassword");
        verify(jwtService).encode(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userDetailsService.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.login("inexistente", "senha"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Credenciais incorretas");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).encode(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        when(userDetailsService.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaerrada", "$2a$10$hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.login("admin", "senhaerrada"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Credenciais incorretas");

        verify(jwtService, never()).encode(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        usuario.setAtivo(false);

        when(userDetailsService.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$hashedPassword")).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.login("admin", "senha123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário inativo");

        verify(jwtService, never()).encode(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoVerificado() {
        usuario.setEmailVerificado(false);

        when(userDetailsService.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$hashedPassword")).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.login("admin", "senha123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email não verificado");

        verify(jwtService, never()).encode(any());
    }

    @Test
    void deveValidarCredenciaisNaOrdemCorreta() {
        usuario.setAtivo(false);
        usuario.setEmailVerificado(false);

        when(userDetailsService.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$hashedPassword")).thenReturn(true);

        // Deve verificar senha primeiro, depois status ativo, depois email verificado
        assertThatThrownBy(() -> authenticationService.login("admin", "senha123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário inativo"); // Primeira validação de status

        verify(passwordEncoder).matches("senha123", "$2a$10$hashedPassword");
    }

    @Test
    void deveGerarTokenApenasParaUsuarioValido() {
        when(userDetailsService.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.encode(usuario)).thenReturn("token.valido");

        authenticationService.login("admin", "senha123");

        verify(jwtService).encode(usuario);
    }
}

