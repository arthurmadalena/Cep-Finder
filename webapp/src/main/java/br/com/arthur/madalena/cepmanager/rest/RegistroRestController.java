package br.com.arthur.madalena.cepmanager.rest;

import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro")
@RequiredArgsConstructor
@Tag(name = "Registro", description = "Registro e verificação de novos usuários")
public class RegistroRestController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Registrar novo usuário", description = "Cria nova conta e envia email de verificação")
    public ResponseEntity<Map<String, String>> registrar(@RequestBody RegistroUsuarioDTO registroDTO) {
        UsuarioDTO usuario = usuarioService.registrarUsuario(registroDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "mensagem", "Usuário registrado com sucesso!",
                "detalhes", "Um email de verificação foi enviado para " + usuario.getEmail(),
                "username", usuario.getUsername()
            )
        );
    }

    @GetMapping("/verificar")
    @Operation(summary = "Verificar email do usuário", description = "Ativa a conta do usuário através do token enviado por email")
    public ResponseEntity<Map<String, String>> verificarEmail(@RequestParam String token) {
        try {
            usuarioService.verificarEmail(token);
            return ResponseEntity.ok(
                Map.of(
                    "mensagem", "Email verificado com sucesso!",
                    "detalhes", "Sua conta foi ativada. Você já pode fazer login no sistema."
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "mensagem", "Erro ao verificar email",
                    "detalhes", "Token inválido ou expirado"
                )
            );
        }
    }

    @PostMapping("/reenviar-verificacao")
    @Operation(summary = "Reenviar email de verificação")
    public ResponseEntity<Map<String, String>> reenviarVerificacao(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        usuarioService.reenviarEmailVerificacao(email);
        
        return ResponseEntity.ok(
            Map.of(
                "mensagem", "Email reenviado com sucesso!",
                "detalhes", "Verifique sua caixa de entrada em " + email
            )
        );
    }
}

