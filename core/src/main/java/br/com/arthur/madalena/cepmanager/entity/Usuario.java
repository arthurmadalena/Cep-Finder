package br.com.arthur.madalena.cepmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(name = "seq_usuario", sequenceName = "seq_usuario", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nome_completo", nullable = false, length = 200)
    private String nomeCompleto;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    @Column(name = "token_verificacao", length = 100)
    private String tokenVerificacao;

    @Column(name = "data_expiracao_token")
    private LocalDateTime dataExpiracaoToken;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "permissao")
    private Set<String> permissoes = new HashSet<>();

    @CreationTimestamp
    @Column(name = "dat_hora_cadastro", nullable = false, updatable = false)
    private LocalDateTime datHoraCadastro;

    @UpdateTimestamp
    @Column(name = "dat_hora_alteracao")
    private LocalDateTime datHoraAlteracao;

    @Column(name = "usuario_cadastro", length = 100)
    private String usuarioCadastro;

    @Column(name = "usuario_alteracao", length = 100)
    private String usuarioAlteracao;
}

