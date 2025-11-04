package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    
    UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO);
    
    void verificarEmail(String token);
    
    void reenviarEmailVerificacao(String email);
    
    UsuarioDTO findByUsername(String username);
    
    UsuarioDTO findById(Long id);
    
    Page<UsuarioDTO> findAll(Pageable pageable);
    
    Page<UsuarioDTO> findByAtivo(Boolean ativo, Pageable pageable);
    
    Page<UsuarioDTO> findByNome(String nome, Pageable pageable);
    
    UsuarioDTO atualizarUsuario(Long id, UsuarioDTO usuarioDTO, String usuarioLogado);
    
    void adicionarPermissao(Long usuarioId, String permissao, String adminUsername);
    
    void removerPermissao(Long usuarioId, String permissao, String adminUsername);
    
    void ativarDesativarUsuario(Long usuarioId, Boolean ativo, String adminUsername);
    
    void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha);
    
    void resetarSenha(String email);
}
