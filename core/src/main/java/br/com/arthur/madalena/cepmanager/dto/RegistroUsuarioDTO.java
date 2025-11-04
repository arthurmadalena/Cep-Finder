package br.com.arthur.madalena.cepmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioDTO {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String nomeCompleto;
}
