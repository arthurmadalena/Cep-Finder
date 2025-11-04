package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dao.UsuarioDAO;
import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.exception.BusinessException;
import br.com.arthur.madalena.cepmanager.exception.ResourceNotFoundException;
import br.com.arthur.madalena.cepmanager.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private RegistroUsuarioDTO registroDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user");
        usuario.setEmail("user@test.com");
        usuario.setPassword("$2a$10$hashedPassword");
        usuario.setNomeCompleto("Usuario Teste");
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        usuario.setPermissoes(Set.of("ROLE_USER"));

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setUsername("user");
        usuarioDTO.setEmail("user@test.com");
        usuarioDTO.setNomeCompleto("Usuario Teste");
        usuarioDTO.setAtivo(true);
        usuarioDTO.setEmailVerificado(true);
        usuarioDTO.setPermissoes(Set.of("ROLE_USER"));

        registroDTO = new RegistroUsuarioDTO();
        registroDTO.setUsername("newuser");
        registroDTO.setEmail("newuser@test.com");
        registroDTO.setPassword("senha123");
        registroDTO.setConfirmPassword("senha123");
        registroDTO.setNomeCompleto("Novo Usuario");
    }

    @Test
    void deveRegistrarNovoUsuarioComSucesso() {
        when(usuarioDAO.existsByUsername(anyString())).thenReturn(false);
        when(usuarioDAO.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.registrarUsuario(registroDTO);

        assertThat(resultado).isNotNull();
        verify(usuarioDAO).existsByUsername("newuser");
        verify(usuarioDAO).existsByEmail("newuser@test.com");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioDAO).save(any(Usuario.class));
        verify(emailService).enviarEmailVerificacao(eq("newuser@test.com"), eq("Novo Usuario"), anyString());
    }

    @Test
    void deveLancarExcecaoQuandoSenhasNaoCoincidirem() {
        registroDTO.setConfirmPassword("senhadiferente");

        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("As senhas não coincidem");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsernameJaExistir() {
        when(usuarioDAO.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Username já está em uso");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir() {
        when(usuarioDAO.existsByUsername(anyString())).thenReturn(false);
        when(usuarioDAO.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já está cadastrado");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveGerarTokenDeVerificacaoAoRegistrar() {
        when(usuarioDAO.existsByUsername(anyString())).thenReturn(false);
        when(usuarioDAO.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        usuarioService.registrarUsuario(registroDTO);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioDAO).save(usuarioCaptor.capture());
        
        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo.getTokenVerificacao()).isNotNull();
        assertThat(usuarioSalvo.getDataExpiracaoToken()).isNotNull();
        assertThat(usuarioSalvo.getDataExpiracaoToken()).isAfter(LocalDateTime.now());
    }

    @Test
    void deveVerificarEmailComSucesso() {
        String token = "token123";
        usuario.setTokenVerificacao(token);
        usuario.setDataExpiracaoToken(LocalDateTime.now().plusHours(1));
        usuario.setEmailVerificado(false);
        usuario.setAtivo(false);

        when(usuarioDAO.findByTokenVerificacao(token)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.verificarEmail(token);

        verify(usuarioDAO).findByTokenVerificacao(token);
        verify(usuarioDAO).save(usuario);
        assertThat(usuario.getEmailVerificado()).isTrue();
        assertThat(usuario.getAtivo()).isTrue();
        assertThat(usuario.getTokenVerificacao()).isNull();
        verify(emailService).enviarEmailBoasVindas(usuario.getEmail(), usuario.getNomeCompleto());
    }

    @Test
    void deveLancarExcecaoQuandoTokenInvalido() {
        when(usuarioDAO.findByTokenVerificacao(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.verificarEmail("tokeninvalido"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Token inválido ou expirado");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoTokenExpirado() {
        String token = "token123";
        usuario.setTokenVerificacao(token);
        usuario.setDataExpiracaoToken(LocalDateTime.now().minusHours(1));

        when(usuarioDAO.findByTokenVerificacao(token)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.verificarEmail(token))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Token expirado. Solicite um novo email de verificação");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveReenviarEmailVerificacao() {
        usuario.setEmailVerificado(false);
        usuario.setTokenVerificacao("oldtoken");

        when(usuarioDAO.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.reenviarEmailVerificacao("user@test.com");

        assertThat(usuario.getTokenVerificacao()).isNotEqualTo("oldtoken");
        assertThat(usuario.getDataExpiracaoToken()).isAfter(LocalDateTime.now());
        verify(emailService).enviarEmailVerificacao(eq("user@test.com"), eq("Usuario Teste"), anyString());
    }

    @Test
    void deveLancarExcecaoAoReenviarQuandoEmailJaVerificado() {
        usuario.setEmailVerificado(true);

        when(usuarioDAO.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.reenviarEmailVerificacao("user@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já verificado");

        verify(emailService, never()).enviarEmailVerificacao(anyString(), anyString(), anyString());
    }

    @Test
    void deveBuscarUsuarioPorUsername() {
        when(usuarioDAO.findByUsername("user")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.findByUsername("user");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("user");
        verify(usuarioDAO).findByUsername("user");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorUsername() {
        when(usuarioDAO.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findByUsername("inexistente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado: inexistente");
    }

    @Test
    void deveBuscarUsuarioPorId() {
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(usuarioDAO).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorId() {
        when(usuarioDAO.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com ID: 999");
    }

    @Test
    void deveBuscarTodosUsuarios() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));
        
        when(usuarioDAO.findAll(pageable)).thenReturn(page);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        Page<UsuarioDTO> resultado = usuarioService.findAll(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(usuarioDAO).findAll(pageable);
    }

    @Test
    void deveBuscarUsuariosPorStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));

        when(usuarioDAO.findByAtivo(true, pageable)).thenReturn(page);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        Page<UsuarioDTO> resultado = usuarioService.findByAtivo(true, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(usuarioDAO).findByAtivo(true, pageable);
    }

    @Test
    void deveBuscarUsuariosPorNome() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));

        when(usuarioDAO.findByNomeCompletoContaining("Usuario", pageable)).thenReturn(page);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        Page<UsuarioDTO> resultado = usuarioService.findByNome("Usuario", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(usuarioDAO).findByNomeCompletoContaining("Usuario", pageable);
    }

    @Test
    void deveAtualizarUsuario() {
        UsuarioDTO dadosAtualizados = new UsuarioDTO();
        dadosAtualizados.setNomeCompleto("Nome Atualizado");
        dadosAtualizados.setEmail("novo@test.com");

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(dadosAtualizados);

        UsuarioDTO resultado = usuarioService.atualizarUsuario(1L, dadosAtualizados, "admin");

        assertThat(usuario.getNomeCompleto()).isEqualTo("Nome Atualizado");
        assertThat(usuario.getEmail()).isEqualTo("novo@test.com");
        assertThat(usuario.getUsuarioAlteracao()).isEqualTo("admin");
        verify(usuarioDAO).save(usuario);
    }

    @Test
    void deveAdicionarPermissao() {
        usuario.setPermissoes(new java.util.HashSet<>(Set.of("ROLE_USER")));
        
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.adicionarPermissao(1L, "ROLE_ADMIN", "admin");

        assertThat(usuario.getPermissoes()).contains("ROLE_ADMIN");
        verify(emailService).enviarEmailAlteracaoPermissao(usuario.getEmail(), usuario.getNomeCompleto(), "ROLE_ADMIN", true);
    }

    @Test
    void deveLancarExcecaoAoAdicionarPermissaoJaExistente() {
        usuario.setPermissoes(new java.util.HashSet<>(Set.of("ROLE_USER", "ROLE_ADMIN")));

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.adicionarPermissao(1L, "ROLE_ADMIN", "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário já possui esta permissão");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveRemoverPermissao() {
        usuario.setPermissoes(new java.util.HashSet<>(Set.of("ROLE_USER", "ROLE_ADMIN")));

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.removerPermissao(1L, "ROLE_ADMIN", "admin");

        assertThat(usuario.getPermissoes()).doesNotContain("ROLE_ADMIN");
        assertThat(usuario.getPermissoes()).contains("ROLE_USER");
        verify(emailService).enviarEmailAlteracaoPermissao(usuario.getEmail(), usuario.getNomeCompleto(), "ROLE_ADMIN", false);
    }

    @Test
    void deveLancarExcecaoAoRemoverPermissaoInexistente() {
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.removerPermissao(1L, "ROLE_ADMIN", "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário não possui esta permissão");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoRemoverUltimaPermissao() {
        usuario.setPermissoes(new java.util.HashSet<>(Set.of("ROLE_USER")));

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.removerPermissao(1L, "ROLE_USER", "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível remover a última permissão do usuário");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveAtivarUsuario() {
        usuario.setAtivo(false);

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.ativarDesativarUsuario(1L, true, "admin");

        assertThat(usuario.getAtivo()).isTrue();
        assertThat(usuario.getUsuarioAlteracao()).isEqualTo("admin");
        verify(usuarioDAO).save(usuario);
    }

    @Test
    void deveDesativarUsuario() {
        usuario.setUsername("otheruser");
        usuario.setAtivo(true);

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.ativarDesativarUsuario(1L, false, "admin");

        assertThat(usuario.getAtivo()).isFalse();
        verify(usuarioDAO).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoDesativarAdmin() {
        usuario.setUsername("admin");

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.ativarDesativarUsuario(1L, false, "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível desativar o usuário administrador");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveAlterarSenha() {
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaatual", usuario.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("novasenha")).thenReturn("$2a$10$newencoded");
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.alterarSenha(1L, "senhaatual", "novasenha");

        verify(passwordEncoder).matches("senhaatual", "$2a$10$hashedPassword");
        verify(passwordEncoder).encode("novasenha");
        assertThat(usuario.getPassword()).isEqualTo("$2a$10$newencoded");
        verify(usuarioDAO).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaerrada", usuario.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.alterarSenha(1L, "senhaerrada", "novasenha"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Senha atual incorreta");

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void deveResetarSenha() {
        when(usuarioDAO.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$resetencoded");
        when(usuarioDAO.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.resetarSenha("user@test.com");

        verify(passwordEncoder).encode(anyString());
        verify(usuarioDAO).save(usuario);
        verify(emailService).enviarEmailResetSenha(eq("user@test.com"), eq("Usuario Teste"), anyString());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoParaResetSenha() {
        when(usuarioDAO.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.resetarSenha("inexistente@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com email: inexistente@test.com");

        verify(usuarioDAO, never()).save(any());
    }
}

