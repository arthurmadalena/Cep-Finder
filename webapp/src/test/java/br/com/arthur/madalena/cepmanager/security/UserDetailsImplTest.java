package br.com.arthur.madalena.cepmanager.security;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    private Usuario usuario;
    private UserDetailsImpl userDetails;

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

        userDetails = new UserDetailsImpl(usuario);
    }

    @Test
    void deveRetornarUsernameCorreto() {
        assertThat(userDetails.getUsername()).isEqualTo("admin");
    }

    @Test
    void deveRetornarPasswordCorreto() {
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$hashedPassword");
    }

    @Test
    void deveRetornarAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).hasSize(2);
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void deveRetornarContaNaoExpirada() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void deveRetornarContaNaoBloqueadaQuandoAtivo() {
        usuario.setAtivo(true);
        userDetails = new UserDetailsImpl(usuario);

        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void deveRetornarContaBloqueadaQuandoInativo() {
        usuario.setAtivo(false);
        userDetails = new UserDetailsImpl(usuario);

        assertThat(userDetails.isAccountNonLocked()).isFalse();
    }

    @Test
    void deveRetornarCredenciaisNaoExpiradas() {
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void deveRetornarHabilitadoQuandoAtivoEVerificado() {
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        userDetails = new UserDetailsImpl(usuario);

        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void deveRetornarDesabilitadoQuandoInativo() {
        usuario.setAtivo(false);
        usuario.setEmailVerificado(true);
        userDetails = new UserDetailsImpl(usuario);

        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void deveRetornarDesabilitadoQuandoEmailNaoVerificado() {
        usuario.setAtivo(true);
        usuario.setEmailVerificado(false);
        userDetails = new UserDetailsImpl(usuario);

        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void deveRetornarDesabilitadoQuandoInativoENaoVerificado() {
        usuario.setAtivo(false);
        usuario.setEmailVerificado(false);
        userDetails = new UserDetailsImpl(usuario);

        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void deveRetornarUsuarioOriginal() {
        assertThat(userDetails.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void deveConterTodasAsPermissoes() {
        usuario.setPermissoes(Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_GERENTE"));
        userDetails = new UserDetailsImpl(usuario);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).hasSize(3);
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN", "ROLE_GERENTE");
    }

    @Test
    void devePermitirPermissoesVazias() {
        usuario.setPermissoes(Set.of());
        userDetails = new UserDetailsImpl(usuario);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).isEmpty();
    }
}

