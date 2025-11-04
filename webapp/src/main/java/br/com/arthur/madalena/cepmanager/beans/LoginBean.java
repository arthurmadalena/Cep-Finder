package br.com.arthur.madalena.cepmanager.beans;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.security.UserDetailsServiceImpl;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("loginBean")
@Named
@SessionScoped
@Data
@RequiredArgsConstructor
public class LoginBean implements Serializable {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private String username;
    private String password;
    private String usuarioLogado;
    private boolean authenticated = false;
    private Boolean isAdmin = false;

    public String login() {
        try {
            if (username == null || username.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Campo obrigatório",
                                "Por favor, informe o usuário"));
                return null;
            }
            
            if (password == null || password.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Campo obrigatório",
                                "Por favor, informe a senha"));
                return null;
            }

            // Busca o usuário diretamente
            Usuario usuario = userDetailsService.findByUsername(username)
                    .orElse(null);

            if (usuario == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Credenciais inválidas",
                                "Usuário ou senha incorretos"));
                return null;
            }

            // Verifica a senha
            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Credenciais inválidas",
                                "Usuário ou senha incorretos"));
                return null;
            }

            // Verifica se o usuário está ativo
            if (!usuario.getAtivo()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Usuário inativo",
                                "Sua conta foi desativada. Entre em contato com o administrador"));
                return null;
            }

            // Verifica se o email foi verificado
            if (!usuario.getEmailVerificado()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Email não verificado",
                                "Por favor, verifique seu email para ativar a conta"));
                return null;
            }

            // Login bem-sucedido
            authenticated = true;
            usuarioLogado = username;
            isAdmin = Boolean.TRUE.equals(usuario.getPermissoes() != null && 
                                         usuario.getPermissoes().contains("ROLE_ADMIN"));

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Login realizado com sucesso!",
                            "Bem-vindo ao CepFinder, " + username + "!"));
            
            context.getExternalContext().getFlash().setKeepMessages(true);
            
            return "/pages/dashboard?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro de conexão",
                            "Não foi possível se conectar com o servidor: " + e.getMessage()));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        authenticated = false;
        usuarioLogado = null;
        return "/login?faces-redirect=true";
    }

    // Getter explícito para JSF EL
    public boolean getIsAdmin() {
        return Boolean.TRUE.equals(isAdmin);
    }
}

