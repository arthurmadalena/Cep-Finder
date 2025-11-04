package br.com.arthur.madalena.cepmanager.service;

public interface EmailService {
    
    void enviarEmailVerificacao(String destinatario, String nomeUsuario, String token);
    
    void enviarEmailBoasVindas(String destinatario, String nomeUsuario);
    
    void enviarEmailResetSenha(String destinatario, String nomeUsuario, String novaSenha);
    
    void enviarEmailAlteracaoPermissao(String destinatario, String nomeUsuario, String permissao, boolean adicionada);
}
