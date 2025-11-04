package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dao.UsuarioDAO;
import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.exception.BusinessException;
import br.com.arthur.madalena.cepmanager.exception.ResourceNotFoundException;
import br.com.arthur.madalena.cepmanager.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO) {
        log.info("Iniciando registro de usuário: {}", registroDTO.getUsername());

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            throw new BusinessException("As senhas não coincidem");
        }

        if (usuarioDAO.existsByUsername(registroDTO.getUsername())) {
            throw new BusinessException("Username já está em uso");
        }

        if (usuarioDAO.existsByEmail(registroDTO.getEmail())) {
            throw new BusinessException("Email já está cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setNomeCompleto(registroDTO.getNomeCompleto());
        usuario.setAtivo(false);
        usuario.setEmailVerificado(false);
        usuario.setUsuarioCadastro("sistema");

        String token = UUID.randomUUID().toString();
        usuario.setTokenVerificacao(token);
        usuario.setDataExpiracaoToken(LocalDateTime.now().plusHours(24));

        Set<String> permissoes = new HashSet<>();
        permissoes.add("ROLE_USER");
        usuario.setPermissoes(permissoes);

        Usuario usuarioSalvo = usuarioDAO.save(usuario);

        emailService.enviarEmailVerificacao(usuario.getEmail(), usuario.getNomeCompleto(), token);

        log.info("Usuário registrado com sucesso: {}", usuarioSalvo.getUsername());
        return usuarioMapper.toDTO(usuarioSalvo);
    }

    @Override
    public void verificarEmail(String token) {
        log.info("Verificando email com token: {}", token);

        Usuario usuario = usuarioDAO.findByTokenVerificacao(token)
                .orElseThrow(() -> new BusinessException("Token inválido ou expirado"));

        if (usuario.getDataExpiracaoToken().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token expirado. Solicite um novo email de verificação");
        }

        usuario.setEmailVerificado(true);
        usuario.setAtivo(true);
        usuario.setTokenVerificacao(null);
        usuario.setDataExpiracaoToken(null);

        usuarioDAO.save(usuario);

        emailService.enviarEmailBoasVindas(usuario.getEmail(), usuario.getNomeCompleto());

        log.info("Email verificado com sucesso para usuário: {}", usuario.getUsername());
    }

    @Override
    public void reenviarEmailVerificacao(String email) {
        log.info("Reenviando email de verificação para: {}", email);

        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));

        if (usuario.getEmailVerificado()) {
            throw new BusinessException("Email já verificado");
        }

        String novoToken = UUID.randomUUID().toString();
        usuario.setTokenVerificacao(novoToken);
        usuario.setDataExpiracaoToken(LocalDateTime.now().plusHours(24));

        usuarioDAO.save(usuario);

        emailService.enviarEmailVerificacao(usuario.getEmail(), usuario.getNomeCompleto(), novoToken);

        log.info("Email de verificação reenviado para: {}", email);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findByUsername(String username) {
        Usuario usuario = usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> findAll(Pageable pageable) {
        return usuarioDAO.findAll(pageable).map(usuarioMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> findByAtivo(Boolean ativo, Pageable pageable) {
        return usuarioDAO.findByAtivo(ativo, pageable).map(usuarioMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> findByNome(String nome, Pageable pageable) {
        return usuarioDAO.findByNomeCompletoContaining(nome, pageable).map(usuarioMapper::toDTO);
    }

    @Override
    public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO usuarioDTO, String usuarioLogado) {
        log.info("Atualizando usuário ID: {} por {}", id, usuarioLogado);

        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        usuario.setNomeCompleto(usuarioDTO.getNomeCompleto());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setUsuarioAlteracao(usuarioLogado);

        Usuario usuarioAtualizado = usuarioDAO.save(usuario);

        log.info("Usuário atualizado com sucesso: {}", usuarioAtualizado.getUsername());
        return usuarioMapper.toDTO(usuarioAtualizado);
    }

    @Override
    public void adicionarPermissao(Long usuarioId, String permissao, String adminUsername) {
        log.info("Adicionando permissão {} ao usuário ID: {} por {}", permissao, usuarioId, adminUsername);

        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (usuario.getPermissoes().contains(permissao)) {
            throw new BusinessException("Usuário já possui esta permissão");
        }

        usuario.getPermissoes().add(permissao);
        usuario.setUsuarioAlteracao(adminUsername);

        usuarioDAO.save(usuario);

        emailService.enviarEmailAlteracaoPermissao(usuario.getEmail(), usuario.getNomeCompleto(), permissao, true);

        log.info("Permissão {} adicionada ao usuário {}", permissao, usuario.getUsername());
    }

    @Override
    public void removerPermissao(Long usuarioId, String permissao, String adminUsername) {
        log.info("Removendo permissão {} do usuário ID: {} por {}", permissao, usuarioId, adminUsername);

        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (!usuario.getPermissoes().contains(permissao)) {
            throw new BusinessException("Usuário não possui esta permissão");
        }

        if (permissao.equals("ROLE_USER") && usuario.getPermissoes().size() == 1) {
            throw new BusinessException("Não é possível remover a última permissão do usuário");
        }

        usuario.getPermissoes().remove(permissao);
        usuario.setUsuarioAlteracao(adminUsername);

        usuarioDAO.save(usuario);

        emailService.enviarEmailAlteracaoPermissao(usuario.getEmail(), usuario.getNomeCompleto(), permissao, false);

        log.info("Permissão {} removida do usuário {}", permissao, usuario.getUsername());
    }

    @Override
    public void ativarDesativarUsuario(Long usuarioId, Boolean ativo, String adminUsername) {
        log.info("Alterando status do usuário ID: {} para ativo={} por {}", usuarioId, ativo, adminUsername);

        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (usuario.getUsername().equals("admin")) {
            throw new BusinessException("Não é possível desativar o usuário administrador");
        }

        usuario.setAtivo(ativo);
        usuario.setUsuarioAlteracao(adminUsername);

        usuarioDAO.save(usuario);

        log.info("Status do usuário {} alterado para ativo={}", usuario.getUsername(), ativo);
    }

    @Override
    public void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha) {
        log.info("Alterando senha do usuário ID: {}", usuarioId);

        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (!passwordEncoder.matches(senhaAtual, usuario.getPassword())) {
            throw new BusinessException("Senha atual incorreta");
        }

        usuario.setPassword(passwordEncoder.encode(novaSenha));
        usuario.setUsuarioAlteracao(usuario.getUsername());

        usuarioDAO.save(usuario);

        log.info("Senha alterada com sucesso para usuário: {}", usuario.getUsername());
    }

    @Override
    public void resetarSenha(String email) {
        log.info("Resetando senha para email: {}", email);

        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));

        String novaSenha = gerarSenhaAleatoria();
        usuario.setPassword(passwordEncoder.encode(novaSenha));
        usuario.setUsuarioAlteracao("sistema");

        usuarioDAO.save(usuario);

        emailService.enviarEmailResetSenha(usuario.getEmail(), usuario.getNomeCompleto(), novaSenha);

        log.info("Senha resetada para usuário: {}", usuario.getUsername());
    }

    private String gerarSenhaAleatoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder senha = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * caracteres.length());
            senha.append(caracteres.charAt(index));
        }
        return senha.toString();
    }
}
