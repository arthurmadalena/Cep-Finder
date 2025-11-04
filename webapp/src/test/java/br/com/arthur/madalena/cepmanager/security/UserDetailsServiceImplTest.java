package br.com.arthur.madalena.cepmanager.security;

import br.com.arthur.madalena.cepmanager.dao.UsuarioDAO;
import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

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
    void deveCarregarUsuarioPorUsername() {
        when(usuarioDAO.findByUsername("admin")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getAuthorities()).hasSize(2);
        verify(usuarioDAO).findByUsername("admin");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioDAO.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("inexistente"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado: inexistente");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        usuario.setAtivo(false);
        when(usuarioDAO.findByUsername("admin")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("admin"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("Usuário inativo");
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoVerificado() {
        usuario.setEmailVerificado(false);
        when(usuarioDAO.findByUsername("admin")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("admin"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("Email não verificado");
    }

    @Test
    void deveBuscarUsuarioPorUsername() {
        when(usuarioDAO.findByUsername("admin")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = userDetailsService.findByUsername("admin");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo("admin");
        verify(usuarioDAO).findByUsername("admin");
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        when(usuarioDAO.findByUsername(anyString())).thenReturn(Optional.empty());

        Optional<Usuario> resultado = userDetailsService.findByUsername("inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveCriarNovoUsuario() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setUsername("newuser");
        dto.setEmail("newuser@test.com");
        dto.setPassword("senha123");
        dto.setNomeCompleto("Novo Usuario");

        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$encoded");
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });

        Usuario resultado = userDetailsService.createUser(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("newuser");
        assertThat(resultado.getEmail()).isEqualTo("newuser@test.com");
        assertThat(resultado.getPassword()).isEqualTo("$2a$10$encoded");
        assertThat(resultado.getNomeCompleto()).isEqualTo("Novo Usuario");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioDAO).save(any(Usuario.class));
    }

    @Test
    void deveCriptografarSenhaAoCriarUsuario() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setUsername("user");
        dto.setEmail("user@test.com");
        dto.setPassword("senhaTextoPlano");
        dto.setNomeCompleto("Usuario");

        when(passwordEncoder.encode("senhaTextoPlano")).thenReturn("$2a$10$senhaCriptografada");
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = userDetailsService.createUser(dto);

        assertThat(resultado.getPassword()).isEqualTo("$2a$10$senhaCriptografada");
        assertThat(resultado.getPassword()).isNotEqualTo("senhaTextoPlano");
        verify(passwordEncoder).encode("senhaTextoPlano");
    }

    @Test
    void deveRetornarUserDetailsComPermissoesCorretas() {
        when(usuarioDAO.findByUsername("admin")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertThat(userDetails.getAuthorities()).hasSize(2);
        assertThat(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void devePermitirAcessoApenasParaUsuariosAtivosEVerificados() {
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        when(usuarioDAO.findByUsername("admin")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }
}

