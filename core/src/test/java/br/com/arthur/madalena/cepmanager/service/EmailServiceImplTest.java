package br.com.arthur.madalena.cepmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "emailFrom", "noreply@cepfinder.com");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:8080/cep-manager");
    }

    @Test
    void deveEnviarEmailVerificacao() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        emailService.enviarEmailVerificacao("user@test.com", "Usuario Teste", "token123");

        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("noreply@cepfinder.com");
        assertThat(message.getTo()).containsExactly("user@test.com");
        assertThat(message.getSubject()).isEqualTo("CepFinder - Verificação de Email");
        assertThat(message.getText()).contains("Usuario Teste");
        assertThat(message.getText()).contains("token123");
        assertThat(message.getText()).contains("http://localhost:8080/cep-manager/verificar-email?token=token123");
    }

    @Test
    void deveEnviarEmailBoasVindas() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        emailService.enviarEmailBoasVindas("user@test.com", "Usuario Teste");

        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("noreply@cepfinder.com");
        assertThat(message.getTo()).containsExactly("user@test.com");
        assertThat(message.getSubject()).isEqualTo("CepFinder - Conta Ativada com Sucesso!");
        assertThat(message.getText()).contains("Usuario Teste");
        assertThat(message.getText()).contains("Sua conta foi ativada com sucesso");
    }

    @Test
    void deveEnviarEmailResetSenha() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        emailService.enviarEmailResetSenha("user@test.com", "Usuario Teste", "novaSenha123");

        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("noreply@cepfinder.com");
        assertThat(message.getTo()).containsExactly("user@test.com");
        assertThat(message.getSubject()).isEqualTo("CepFinder - Redefinição de Senha");
        assertThat(message.getText()).contains("Usuario Teste");
        assertThat(message.getText()).contains("novaSenha123");
        assertThat(message.getText()).contains("Sua senha foi redefinida");
    }

    @Test
    void deveEnviarEmailAlteracaoPermissaoAdicionada() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        emailService.enviarEmailAlteracaoPermissao("user@test.com", "Usuario Teste", "ROLE_ADMIN", true);

        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("noreply@cepfinder.com");
        assertThat(message.getTo()).containsExactly("user@test.com");
        assertThat(message.getSubject()).isEqualTo("CepFinder - Alteração de Permissões");
        assertThat(message.getText()).contains("Usuario Teste");
        assertThat(message.getText()).contains("ROLE_ADMIN");
        assertThat(message.getText()).contains("adicionada");
    }

    @Test
    void deveEnviarEmailAlteracaoPermissaoRemovida() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        emailService.enviarEmailAlteracaoPermissao("user@test.com", "Usuario Teste", "ROLE_ADMIN", false);

        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getText()).contains("removida");
        assertThat(message.getText()).doesNotContain("adicionada");
    }

    @Test
    void deveLancarExcecaoQuandoErroAoEnviarEmailVerificacao() {
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> emailService.enviarEmailVerificacao("user@test.com", "Usuario", "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao enviar email de verificação");
    }

    @Test
    void naoDeveLancarExcecaoQuandoErroAoEnviarEmailBoasVindas() {
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(SimpleMailMessage.class));

        // Não deve lançar exceção, apenas logar o erro
        emailService.enviarEmailBoasVindas("user@test.com", "Usuario");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void naoDeveLancarExcecaoQuandoErroAoEnviarEmailResetSenha() {
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(SimpleMailMessage.class));

        // Não deve lançar exceção, apenas logar o erro
        emailService.enviarEmailResetSenha("user@test.com", "Usuario", "novaSenha");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void deveUsarEmailFromConfigurado() {
        ReflectionTestUtils.setField(emailService, "emailFrom", "custom@domain.com");
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.enviarEmailVerificacao("user@test.com", "Usuario", "token");

        verify(mailSender).send(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getFrom()).isEqualTo("custom@domain.com");
    }

    @Test
    void deveUsarFrontendUrlConfigurado() {
        ReflectionTestUtils.setField(emailService, "frontendUrl", "https://producao.com");
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.enviarEmailVerificacao("user@test.com", "Usuario", "token");

        verify(mailSender).send(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getText()).contains("https://producao.com/verificar-email?token=token");
    }
}

