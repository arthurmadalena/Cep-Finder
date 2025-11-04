package br.com.arthur.madalena.cepmanager.beans;

import br.com.arthur.madalena.cepmanager.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Named
@RequestScoped
@RequiredArgsConstructor
public class VerificacaoBean {

    private final UsuarioService usuarioService;

    @PostConstruct
    public void init() {
        // Este método será chamado automaticamente quando a página carregar
    }

    public void verificarEmail() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String token = params.get("token");

            if (token == null || token.trim().isEmpty()) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Token inválido",
                                "O token de verificação não foi fornecido"));
                return;
            }

            usuarioService.verificarEmail(token);

            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Email verificado com sucesso!",
                            "Você já pode fazer login no sistema"));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro ao verificar email",
                            e.getMessage()));
        }
    }
}

