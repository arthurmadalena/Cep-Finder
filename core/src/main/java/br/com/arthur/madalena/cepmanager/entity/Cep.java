package br.com.arthur.madalena.cepmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "cep", indexes = {
    @Index(name = "idx_cep_codigo", columnList = "codigo"),
    @Index(name = "idx_cep_logradouro", columnList = "logradouro"),
    @Index(name = "idx_cep_cidade", columnList = "cidade")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cep implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Column(name = "codigo", unique = true, nullable = false, length = 8)
    private String codigo;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "complemento")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Size(max = 7, message = "Código IBGE deve ter no máximo 7 caracteres")
    @Pattern(regexp = "^$|^[0-9]{7}$", message = "Código IBGE deve ter 7 dígitos numéricos ou estar vazio")
    @Column(name = "ibge", length = 7)
    private String ibge;

    @Column(name = "dat_hora_cadastro")
    private LocalDateTime datHoraCadastro;

    @Column(name = "dat_hora_alteracao")
    private LocalDateTime datHoraAlteracao;

    @PrePersist
    protected void onCreate() {
        datHoraCadastro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        datHoraAlteracao = LocalDateTime.now();
    }
}

