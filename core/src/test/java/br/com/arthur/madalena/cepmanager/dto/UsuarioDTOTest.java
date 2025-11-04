package br.com.arthur.madalena.cepmanager.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioDTOTest {

    private UsuarioDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UsuarioDTO();
    }

    @Test
    void deveSetarEGetarId() {
        dto.setId(1L);
        assertThat(dto.getId()).isEqualTo(1L);
    }

    @Test
    void deveSetarEGetarUsername() {
        dto.setUsername("admin");
        assertThat(dto.getUsername()).isEqualTo("admin");
    }

    @Test
    void deveSetarEGetarEmail() {
        dto.setEmail("admin@test.com");
        assertThat(dto.getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void deveSetarEGetarNomeCompleto() {
        dto.setNomeCompleto("Administrador");
        assertThat(dto.getNomeCompleto()).isEqualTo("Administrador");
    }

    @Test
    void deveSetarEGetarAtivo() {
        dto.setAtivo(true);
        assertThat(dto.getAtivo()).isTrue();

        dto.setAtivo(false);
        assertThat(dto.getAtivo()).isFalse();
    }

    @Test
    void deveSetarEGetarEmailVerificado() {
        dto.setEmailVerificado(true);
        assertThat(dto.getEmailVerificado()).isTrue();

        dto.setEmailVerificado(false);
        assertThat(dto.getEmailVerificado()).isFalse();
    }

    @Test
    void deveSetarEGetarPermissoes() {
        Set<String> permissoes = Set.of("ROLE_ADMIN", "ROLE_USER");
        dto.setPermissoes(permissoes);
        
        assertThat(dto.getPermissoes()).isEqualTo(permissoes);
        assertThat(dto.getPermissoes()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void deveSetarEGetarDatasDeAuditoria() {
        LocalDateTime dataCadastro = LocalDateTime.now().minusDays(1);
        LocalDateTime dataAlteracao = LocalDateTime.now();

        dto.setDatHoraCadastro(dataCadastro);
        dto.setDatHoraAlteracao(dataAlteracao);

        assertThat(dto.getDatHoraCadastro()).isEqualTo(dataCadastro);
        assertThat(dto.getDatHoraAlteracao()).isEqualTo(dataAlteracao);
    }

    @Test
    void deveSetarEGetarUsuariosAuditoria() {
        dto.setUsuarioCadastro("sistema");
        dto.setUsuarioAlteracao("admin");

        assertThat(dto.getUsuarioCadastro()).isEqualTo("sistema");
        assertThat(dto.getUsuarioAlteracao()).isEqualTo("admin");
    }

    @Test
    void deveCriarDTOComConstrutorCompleto() {
        LocalDateTime now = LocalDateTime.now();
        Set<String> permissoes = Set.of("ROLE_USER");

        UsuarioDTO dto = new UsuarioDTO(
                1L,
                "user",
                "user@test.com",
                "Usuario Teste",
                true,
                true,
                permissoes,
                now,
                now,
                "sistema",
                "admin"
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("user");
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getNomeCompleto()).isEqualTo("Usuario Teste");
        assertThat(dto.getAtivo()).isTrue();
        assertThat(dto.getEmailVerificado()).isTrue();
        assertThat(dto.getPermissoes()).isEqualTo(permissoes);
        assertThat(dto.getDatHoraCadastro()).isEqualTo(now);
        assertThat(dto.getDatHoraAlteracao()).isEqualTo(now);
        assertThat(dto.getUsuarioCadastro()).isEqualTo("sistema");
        assertThat(dto.getUsuarioAlteracao()).isEqualTo("admin");
    }

    @Test
    void deveCriarDTOVazioComConstrutorPadrao() {
        UsuarioDTO dto = new UsuarioDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getNomeCompleto()).isNull();
        assertThat(dto.getAtivo()).isNull();
        assertThat(dto.getEmailVerificado()).isNull();
        assertThat(dto.getPermissoes()).isNotNull(); // HashSet inicializado
        assertThat(dto.getDatHoraCadastro()).isNull();
        assertThat(dto.getDatHoraAlteracao()).isNull();
    }

    @Test
    void deveInicializarPermissoesComoHashSetVazio() {
        UsuarioDTO dto = new UsuarioDTO();

        assertThat(dto.getPermissoes()).isNotNull();
        assertThat(dto.getPermissoes()).isEmpty();
    }

    @Test
    void devePermitirModificarPermissoes() {
        dto.getPermissoes().add("ROLE_USER");
        dto.getPermissoes().add("ROLE_ADMIN");

        assertThat(dto.getPermissoes()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");

        dto.getPermissoes().remove("ROLE_ADMIN");

        assertThat(dto.getPermissoes()).containsExactly("ROLE_USER");
    }
}

