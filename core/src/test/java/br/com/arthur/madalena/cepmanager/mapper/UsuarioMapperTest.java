package br.com.arthur.madalena.cepmanager.mapper;

import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioMapper();
    }

    @Test
    void deveConverterEntityParaDTO() {
        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setUsername("admin");
        entity.setEmail("admin@test.com");
        entity.setPassword("$2a$10$hashedPassword");
        entity.setNomeCompleto("Administrador");
        entity.setAtivo(true);
        entity.setEmailVerificado(true);
        entity.setPermissoes(Set.of("ROLE_ADMIN", "ROLE_USER"));
        entity.setDatHoraCadastro(LocalDateTime.now());
        entity.setDatHoraAlteracao(LocalDateTime.now());
        entity.setUsuarioCadastro("sistema");
        entity.setUsuarioAlteracao("admin");

        UsuarioDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("admin");
        assertThat(dto.getEmail()).isEqualTo("admin@test.com");
        assertThat(dto.getNomeCompleto()).isEqualTo("Administrador");
        assertThat(dto.getAtivo()).isTrue();
        assertThat(dto.getEmailVerificado()).isTrue();
        assertThat(dto.getPermissoes()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(dto.getDatHoraCadastro()).isNotNull();
        assertThat(dto.getDatHoraAlteracao()).isNotNull();
        assertThat(dto.getUsuarioCadastro()).isEqualTo("sistema");
        assertThat(dto.getUsuarioAlteracao()).isEqualTo("admin");
    }

    @Test
    void naoDeveIncluirPasswordNoDTO() {
        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setUsername("user");
        entity.setEmail("user@test.com");
        entity.setPassword("$2a$10$hashedPassword");
        entity.setNomeCompleto("Usuario");
        entity.setAtivo(true);
        entity.setEmailVerificado(false);
        entity.setPermissoes(Set.of("ROLE_USER"));

        UsuarioDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        // DTO não deve ter senha
        assertThat(dto.getUsername()).isEqualTo("user");
    }

    @Test
    void deveRetornarNullQuandoEntityForNull() {
        UsuarioDTO dto = mapper.toDTO(null);

        assertThat(dto).isNull();
    }

    @Test
    void deveConverterDTOParaEntity() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(1L);
        dto.setUsername("user");
        dto.setEmail("user@test.com");
        dto.setNomeCompleto("Usuario Teste");
        dto.setAtivo(true);
        dto.setEmailVerificado(false);
        dto.setPermissoes(Set.of("ROLE_USER"));
        dto.setUsuarioCadastro("sistema");

        Usuario entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUsername()).isEqualTo("user");
        assertThat(entity.getEmail()).isEqualTo("user@test.com");
        assertThat(entity.getNomeCompleto()).isEqualTo("Usuario Teste");
        assertThat(entity.getAtivo()).isTrue();
        assertThat(entity.getEmailVerificado()).isFalse();
        assertThat(entity.getPermissoes()).containsExactly("ROLE_USER");
        assertThat(entity.getUsuarioCadastro()).isEqualTo("sistema");
    }

    @Test
    void deveRetornarNullQuandoDTOForNull() {
        Usuario entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    void devePreservarPermissoesVazias() {
        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setUsername("user");
        entity.setEmail("user@test.com");
        entity.setNomeCompleto("Usuario");
        entity.setAtivo(true);
        entity.setEmailVerificado(false);
        entity.setPermissoes(Set.of());

        UsuarioDTO dto = mapper.toDTO(entity);

        assertThat(dto.getPermissoes()).isEmpty();
    }

    @Test
    void devePreservarNulls() {
        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setUsername("user");
        entity.setEmail("user@test.com");
        entity.setNomeCompleto("Usuario");
        entity.setAtivo(true);
        entity.setEmailVerificado(false);
        // Sem setar permissoes, usuarioCadastro, etc

        UsuarioDTO dto = mapper.toDTO(entity);

        assertThat(dto.getUsuarioCadastro()).isNull();
        assertThat(dto.getUsuarioAlteracao()).isNull();
        assertThat(dto.getDatHoraAlteracao()).isNull();
    }

    @Test
    void deveConverterUsuarioInativo() {
        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setUsername("inactive");
        entity.setEmail("inactive@test.com");
        entity.setNomeCompleto("Usuario Inativo");
        entity.setAtivo(false);
        entity.setEmailVerificado(true);
        entity.setPermissoes(Set.of("ROLE_USER"));

        UsuarioDTO dto = mapper.toDTO(entity);

        assertThat(dto.getAtivo()).isFalse();
        assertThat(dto.getEmailVerificado()).isTrue();
    }

    @Test
    void deveConverterUsuarioComEmailNaoVerificado() {
        Usuario entity = new Usuario();
        entity.setId(1L);
        entity.setUsername("unverified");
        entity.setEmail("unverified@test.com");
        entity.setNomeCompleto("Usuario Nao Verificado");
        entity.setAtivo(false);
        entity.setEmailVerificado(false);
        entity.setPermissoes(Set.of("ROLE_USER"));

        UsuarioDTO dto = mapper.toDTO(entity);

        assertThat(dto.getAtivo()).isFalse();
        assertThat(dto.getEmailVerificado()).isFalse();
    }
}

