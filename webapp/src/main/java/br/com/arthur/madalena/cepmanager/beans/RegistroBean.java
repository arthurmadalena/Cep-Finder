package br.com.arthur.madalena.cepmanager.beans;

import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.service.UsuarioService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("registroBean")
@ViewScoped
@Data
@RequiredArgsConstructor
public class RegistroBean implements Serializable {

    private final UsuarioService usuarioService;

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String nomeCompleto;
    
    private boolean registroRealizado = false;

    public String registrar() {
        try {
            if (!password.equals(confirmPassword)) {
                addMessage(FacesMessage.SEVERITY_ERROR, "As senhas não coincidem");
                return null;
            }

            RegistroUsuarioDTO registroDTO = new RegistroUsuarioDTO();
            registroDTO.setUsername(username);
            registroDTO.setEmail(email);
            registroDTO.setPassword(password);
            registroDTO.setConfirmPassword(confirmPassword);
            registroDTO.setNomeCompleto(nomeCompleto);

            usuarioService.registrarUsuario(registroDTO);
            
            registroRealizado = true;
            
            addMessage(FacesMessage.SEVERITY_INFO, 
                    "Cadastro realizado com sucesso! Verifique seu email para ativar a conta");
            
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao registrar: " + e.getMessage());
            return null;
        }
    }

    public String reenviarEmail() {
        try {
            usuarioService.reenviarEmailVerificacao(email);
            addMessage(FacesMessage.SEVERITY_INFO, "Email de verificação reenviado com sucesso!");
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao reenviar email: " + e.getMessage());
            return null;
        }
    }

    public String voltarLogin() {
        return "/login?faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(severity, null, message));
    }
}
