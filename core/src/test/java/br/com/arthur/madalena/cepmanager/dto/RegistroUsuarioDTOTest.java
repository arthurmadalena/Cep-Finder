package br.com.arthur.madalena.cepmanager.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistroUsuarioDTOTest {

    private RegistroUsuarioDTO dto;

    @BeforeEach
    void setUp() {
        dto = new RegistroUsuarioDTO();
    }

    @Test
    void deveSetarEGetarUsername() {
        dto.setUsername("user");
        assertThat(dto.getUsername()).isEqualTo("user");
    }

    @Test
    void deveSetarEGetarEmail() {
        dto.setEmail("user@test.com");
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void deveSetarEGetarPassword() {
        dto.setPassword("senha123");
        assertThat(dto.getPassword()).isEqualTo("senha123");
    }

    @Test
    void deveSetarEGetarConfirmPassword() {
        dto.setConfirmPassword("senha123");
        assertThat(dto.getConfirmPassword()).isEqualTo("senha123");
    }

    @Test
    void deveSetarEGetarNomeCompleto() {
        dto.setNomeCompleto("Nome Completo");
        assertThat(dto.getNomeCompleto()).isEqualTo("Nome Completo");
    }

    @Test
    void deveCriarDTOComConstrutorCompleto() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO(
                "user",
                "user@test.com",
                "senha123",
                "senha123",
                "Nome Completo"
        );

        assertThat(dto.getUsername()).isEqualTo("user");
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getPassword()).isEqualTo("senha123");
        assertThat(dto.getConfirmPassword()).isEqualTo("senha123");
        assertThat(dto.getNomeCompleto()).isEqualTo("Nome Completo");
    }

    @Test
    void deveCriarDTOVazioComConstrutorPadrao() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();

        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPassword()).isNull();
        assertThat(dto.getConfirmPassword()).isNull();
        assertThat(dto.getNomeCompleto()).isNull();
    }

    @Test
    void devePermitirAlteracaoDeValores() {
        dto.setUsername("user1");
        dto.setUsername("user2");

        assertThat(dto.getUsername()).isEqualTo("user2");
    }
}

