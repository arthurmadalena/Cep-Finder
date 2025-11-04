package br.com.arthur.madalena.cepmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CepDTO implements Serializable {

    private Long id;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve conter 8 dígitos no formato 00000-000 ou 00000000")
    private String codigo;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
    private String logradouro;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    private String uf;

    @Size(max = 7, message = "Código IBGE deve ter no máximo 7 caracteres")
    @Pattern(regexp = "^$|^[0-9]{7}$", message = "Código IBGE deve ter 7 dígitos numéricos ou estar vazio")
    private String ibge;
    
    private LocalDateTime datHoraCadastro;
    private LocalDateTime datHoraAlteracao;
}

