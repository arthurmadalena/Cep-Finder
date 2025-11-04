package br.com.arthur.madalena.cepmanager.dao;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioDAOTest {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario1;
    private Usuario usuario2;
    private Usuario usuario3;

    @BeforeEach
    void setUp() {
        usuarioDAO.deleteAll();

        usuario1 = new Usuario();
        usuario1.setUsername("admin");
        usuario1.setEmail("admin@test.com");
        usuario1.setPassword("$2a$10$hashedPassword1");
        usuario1.setNomeCompleto("Administrador do Sistema");
        usuario1.setAtivo(true);
        usuario1.setEmailVerificado(true);
        usuario1.setPermissoes(Set.of("ROLE_ADMIN", "ROLE_USER"));
        entityManager.persistAndFlush(usuario1);

        usuario2 = new Usuario();
        usuario2.setUsername("user");
        usuario2.setEmail("user@test.com");
        usuario2.setPassword("$2a$10$hashedPassword2");
        usuario2.setNomeCompleto("Usuario Comum");
        usuario2.setAtivo(true);
        usuario2.setEmailVerificado(false);
        usuario2.setTokenVerificacao("token123");
        usuario2.setDataExpiracaoToken(LocalDateTime.now().plusHours(24));
        usuario2.setPermissoes(Set.of("ROLE_USER"));
        entityManager.persistAndFlush(usuario2);

        usuario3 = new Usuario();
        usuario3.setUsername("inactive");
        usuario3.setEmail("inactive@test.com");
        usuario3.setPassword("$2a$10$hashedPassword3");
        usuario3.setNomeCompleto("Usuario Inativo");
        usuario3.setAtivo(false);
        usuario3.setEmailVerificado(true);
        usuario3.setPermissoes(Set.of("ROLE_USER"));
        entityManager.persistAndFlush(usuario3);

        entityManager.clear();
    }

    @Test
    void deveBuscarUsuarioPorUsername() {
        Optional<Usuario> resultado = usuarioDAO.findByUsername("admin");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo("admin");
        assertThat(resultado.get().getEmail()).isEqualTo("admin@test.com");
        assertThat(resultado.get().getNomeCompleto()).isEqualTo("Administrador do Sistema");
    }

    @Test
    void deveRetornarVazioQuandoUsernameNaoExiste() {
        Optional<Usuario> resultado = usuarioDAO.findByUsername("naoexiste");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        Optional<Usuario> resultado = usuarioDAO.findByEmail("user@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo("user");
        assertThat(resultado.get().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void deveRetornarVazioQuandoEmailNaoExiste() {
        Optional<Usuario> resultado = usuarioDAO.findByEmail("naoexiste@test.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveBuscarUsuarioPorTokenVerificacao() {
        Optional<Usuario> resultado = usuarioDAO.findByTokenVerificacao("token123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo("user");
        assertThat(resultado.get().getTokenVerificacao()).isEqualTo("token123");
        assertThat(resultado.get().getEmailVerificado()).isFalse();
    }

    @Test
    void deveRetornarVazioQuandoTokenNaoExiste() {
        Optional<Usuario> resultado = usuarioDAO.findByTokenVerificacao("tokeninvalido");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveVerificarSeUsernameExiste() {
        boolean existe = usuarioDAO.existsByUsername("admin");

        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarFalsoQuandoUsernameNaoExiste() {
        boolean existe = usuarioDAO.existsByUsername("naoexiste");

        assertThat(existe).isFalse();
    }

    @Test
    void deveVerificarSeEmailExiste() {
        boolean existe = usuarioDAO.existsByEmail("user@test.com");

        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarFalsoQuandoEmailNaoExiste() {
        boolean existe = usuarioDAO.existsByEmail("naoexiste@test.com");

        assertThat(existe).isFalse();
    }

    @Test
    void deveBuscarUsuariosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByAtivo(true, pageable);

        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent())
                .extracting(Usuario::getUsername)
                .containsExactlyInAnyOrder("admin", "user");
    }

    @Test
    void deveBuscarUsuariosInativos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByAtivo(false, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getUsername()).isEqualTo("inactive");
    }

    @Test
    void deveBuscarUsuariosPorNomeCompleto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByNomeCompletoContaining("Comum", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNomeCompleto()).isEqualTo("Usuario Comum");
    }

    @Test
    void deveBuscarUsuariosPorNomeCompletoIgnorandoCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByNomeCompletoContaining("sistema", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getUsername()).isEqualTo("admin");
    }

    @Test
    void deveRetornarVazioQuandoNomeNaoEncontrado() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByNomeCompletoContaining("Inexistente", pageable);

        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void deveBuscarUsuariosPorPermissao() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByPermissao("ROLE_ADMIN", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getUsername()).isEqualTo("admin");
    }

    @Test
    void deveBuscarTodosUsuariosComRoleUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByPermissao("ROLE_USER", pageable);

        assertThat(resultado.getContent()).hasSize(3);
        assertThat(resultado.getContent())
                .extracting(Usuario::getUsername)
                .containsExactlyInAnyOrder("admin", "user", "inactive");
    }

    @Test
    void deveRetornarVazioQuandoPermissaoNaoEncontrada() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> resultado = usuarioDAO.findByPermissao("ROLE_INEXISTENTE", pageable);

        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void deveSalvarNovoUsuario() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername("novo");
        novoUsuario.setEmail("novo@test.com");
        novoUsuario.setPassword("$2a$10$hashedPassword");
        novoUsuario.setNomeCompleto("Novo Usuario");
        novoUsuario.setAtivo(true);
        novoUsuario.setEmailVerificado(false);
        novoUsuario.setPermissoes(Set.of("ROLE_USER"));

        Usuario salvo = usuarioDAO.saveAndFlush(novoUsuario);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getUsername()).isEqualTo("novo");
        assertThat(salvo.getDatHoraCadastro()).isNotNull();
    }

    @Test
    void deveAtualizarUsuarioExistente() {
        Usuario usuario = usuarioDAO.findByUsername("user").orElseThrow();
        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacao(null);

        Usuario atualizado = usuarioDAO.save(usuario);

        assertThat(atualizado.getEmailVerificado()).isTrue();
        assertThat(atualizado.getTokenVerificacao()).isNull();
        assertThat(atualizado.getDatHoraAlteracao()).isNotNull();
    }

    @Test
    void deveDeletarUsuario() {
        Long id = usuario3.getId();
        assertThat(usuarioDAO.findById(id)).isPresent();

        usuarioDAO.deleteById(id);

        assertThat(usuarioDAO.findById(id)).isEmpty();
    }

    @Test
    void deveCarregarPermissoesEagermente() {
        Usuario usuario = usuarioDAO.findByUsername("admin").orElseThrow();
        
        // Não deve lançar LazyInitializationException
        assertThat(usuario.getPermissoes()).isNotEmpty();
        assertThat(usuario.getPermissoes()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
}

