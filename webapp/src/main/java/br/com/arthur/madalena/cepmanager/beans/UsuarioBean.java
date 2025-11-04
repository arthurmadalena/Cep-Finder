package br.com.arthur.madalena.cepmanager.beans;

import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("usuarioBean")
@ViewScoped
@Data
@RequiredArgsConstructor
public class UsuarioBean implements Serializable {

    private final UsuarioService usuarioService;
    private final LoginBean loginBean;

    private UsuarioDTO usuarioSelecionado;
    private List<UsuarioDTO> usuarios;
    
    private String filtroNome;
    private Boolean filtroAtivo;
    private String novaPermissao;
    
    private Long idSelecionado;
    
    private List<String> permissoesDisponiveis = Arrays.asList(
        "ROLE_USER",
        "ROLE_ADMIN",
        "ROLE_GERENTE"
    );

    @PostConstruct
    public void init() {
        usuarioSelecionado = new UsuarioDTO();
        usuarios = null;
    }

    public void buscarTodos() {
        try {
            Pageable pageable = PageRequest.of(0, 100, Sort.by("nomeCompleto"));
            Page<UsuarioDTO> page = usuarioService.findAll(pageable);
            usuarios = page.getContent();
            
            if (filtroAtivo != null) {
                usuarios = usuarios.stream()
                        .filter(u -> u.getAtivo().equals(filtroAtivo))
                        .toList();
            }
            
            if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                String nomeLower = filtroNome.toLowerCase();
                usuarios = usuarios.stream()
                        .filter(u -> u.getNomeCompleto().toLowerCase().contains(nomeLower) ||
                                   u.getUsername().toLowerCase().contains(nomeLower))
                        .toList();
            }
            
            addMessage(FacesMessage.SEVERITY_INFO, "Encontrados " + usuarios.size() + " usuário(s)");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao buscar usuários: " + e.getMessage());
            usuarios = new ArrayList<>();
        }
    }

    public void carregarUsuarioPorId() {
        if (idSelecionado != null) {
            try {
                this.usuarioSelecionado = usuarioService.findById(idSelecionado);
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Usuário não encontrado");
                this.usuarioSelecionado = new UsuarioDTO();
            }
        }
    }

    public String salvarAlteracoes() {
        try {
            usuarioService.atualizarUsuario(
                usuarioSelecionado.getId(), 
                usuarioSelecionado, 
                loginBean.getUsuarioLogado()
            );
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Usuário atualizado com sucesso!", null));
            
            return "/pages/usuario/consulta?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar: " + e.getMessage());
            return null;
        }
    }

    public void adicionarPermissao() {
        try {
            if (novaPermissao == null || novaPermissao.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Selecione uma permissão");
                return;
            }
            
            usuarioService.adicionarPermissao(
                usuarioSelecionado.getId(), 
                novaPermissao, 
                loginBean.getUsuarioLogado()
            );
            
            carregarUsuarioPorId();
            novaPermissao = null;
            
            addMessage(FacesMessage.SEVERITY_INFO, "Permissão adicionada com sucesso");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar permissão: " + e.getMessage());
        }
    }

    public void removerPermissao(String permissao) {
        try {
            usuarioService.removerPermissao(
                usuarioSelecionado.getId(), 
                permissao, 
                loginBean.getUsuarioLogado()
            );
            
            carregarUsuarioPorId();
            
            addMessage(FacesMessage.SEVERITY_INFO, "Permissão removida com sucesso");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao remover permissão: " + e.getMessage());
        }
    }

    public void ativarDesativar(UsuarioDTO usuario) {
        try {
            boolean novoStatus = !usuario.getAtivo();
            
            usuarioService.ativarDesativarUsuario(
                usuario.getId(), 
                novoStatus, 
                loginBean.getUsuarioLogado()
            );
            
            usuario.setAtivo(novoStatus);
            
            String status = novoStatus ? "ativado" : "desativado";
            addMessage(FacesMessage.SEVERITY_INFO, "Usuário " + status + " com sucesso");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao alterar status: " + e.getMessage());
        }
    }

    public String resetarSenha(UsuarioDTO usuario) {
        try {
            usuarioService.resetarSenha(usuario.getEmail());
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Senha resetada e enviada por email", null));
            
            return "/pages/usuario/consulta?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao resetar senha: " + e.getMessage());
            return null;
        }
    }

    public void limparFiltros() {
        filtroNome = null;
        filtroAtivo = null;
        usuarios = null;
    }

    public List<String> getPermissoesNaoAtribuidas() {
        if (usuarioSelecionado == null || usuarioSelecionado.getPermissoes() == null) {
            return permissoesDisponiveis;
        }
        
        return permissoesDisponiveis.stream()
                .filter(p -> !usuarioSelecionado.getPermissoes().contains(p))
                .toList();
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage("growl", 
                new FacesMessage(severity, message, null));
    }
}
