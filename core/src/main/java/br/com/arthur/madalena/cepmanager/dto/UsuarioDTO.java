package br.com.arthur.madalena.cepmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private String nomeCompleto;
    private Boolean ativo;
    private Boolean emailVerificado;
    private Set<String> permissoes = new HashSet<>();
    private LocalDateTime datHoraCadastro;
    private LocalDateTime datHoraAlteracao;
    private String usuarioCadastro;
    private String usuarioAlteracao;
}
