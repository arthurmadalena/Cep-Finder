package br.com.arthur.madalena.cepmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@cepfinder.com}")
    private String emailFrom;

    @Value("${app.url.frontend:http://localhost:8080/cep-manager}")
    private String frontendUrl;

    @Override
    public void enviarEmailVerificacao(String destinatario, String nomeUsuario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(destinatario);
            message.setSubject("CepFinder - Verificação de Email");
            message.setText(
                "Olá " + nomeUsuario + ",\n\n" +
                "Bem-vindo ao CepFinder!\n\n" +
                "Para ativar sua conta, clique no link abaixo:\n" +
                frontendUrl + "/verificar-email?token=" + token + "\n\n" +
                "Este link expira em 24 horas.\n\n" +
                "Se você não solicitou este cadastro, ignore este email.\n\n" +
                "Atenciosamente,\n" +
                "Equipe CepFinder"
            );

            mailSender.send(message);
            log.info("Email de verificação enviado para: {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar email de verificação para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Erro ao enviar email de verificação", e);
        }
    }

    @Override
    public void enviarEmailBoasVindas(String destinatario, String nomeUsuario) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(destinatario);
            message.setSubject("CepFinder - Conta Ativada com Sucesso!");
            message.setText(
                "Olá " + nomeUsuario + ",\n\n" +
                "Sua conta foi ativada com sucesso!\n\n" +
                "Você já pode fazer login no sistema:\n" +
                frontendUrl + "\n\n" +
                "Atenciosamente,\n" +
                "Equipe CepFinder"
            );

            mailSender.send(message);
            log.info("Email de boas-vindas enviado para: {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar email de boas-vindas para {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    public void enviarEmailResetSenha(String destinatario, String nomeUsuario, String novaSenha) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(destinatario);
            message.setSubject("CepFinder - Redefinição de Senha");
            message.setText(
                "Olá " + nomeUsuario + ",\n\n" +
                "Sua senha foi redefinida pelo administrador.\n\n" +
                "Nova senha temporária: " + novaSenha + "\n\n" +
                "Por segurança, recomendamos que você altere esta senha após o login.\n\n" +
                "Atenciosamente,\n" +
                "Equipe CepFinder"
            );

            mailSender.send(message);
            log.info("Email de reset de senha enviado para: {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar email de reset para {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    public void enviarEmailAlteracaoPermissao(String destinatario, String nomeUsuario, String permissao, boolean adicionada) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(destinatario);
            message.setSubject("CepFinder - Alteração de Permissões");
            
            String acao = adicionada ? "adicionada" : "removida";
            message.setText(
                "Olá " + nomeUsuario + ",\n\n" +
                "Sua permissão de acesso foi alterada.\n\n" +
                "Permissão " + acao + ": " + permissao + "\n\n" +
                "Atenciosamente,\n" +
                "Equipe CepFinder"
            );

            mailSender.send(message);
            log.info("Email de alteração de permissão enviado para: {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar email de permissão para {}: {}", destinatario, e.getMessage());
        }
    }
}
