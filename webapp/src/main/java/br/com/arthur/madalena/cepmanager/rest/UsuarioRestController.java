package br.com.arthur.madalena.cepmanager.rest;

import br.com.arthur.madalena.cepmanager.dto.RegistroUsuarioDTO;
import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários e permissões")
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar novo usuário", 
               description = "Cria uma nova conta de usuário e envia email de verificação")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody RegistroUsuarioDTO registroDTO) {
        UsuarioDTO usuario = usuarioService.registrarUsuario(registroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/verificar-email")
    @Operation(summary = "Verificar email", 
               description = "Ativa a conta do usuário através do token de verificação")
    public ResponseEntity<Map<String, String>> verificarEmail(@RequestParam String token) {
        usuarioService.verificarEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verificado com sucesso! Você já pode fazer login."));
    }

    @PostMapping("/reenviar-verificacao")
    @Operation(summary = "Reenviar email de verificação")
    public ResponseEntity<Map<String, String>> reenviarVerificacao(@RequestBody Map<String, String> request) {
        usuarioService.reenviarEmailVerificacao(request.get("email"));
        return ResponseEntity.ok(Map.of("message", "Email de verificação reenviado"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todos os usuários (ADMIN)", 
               description = "Retorna lista paginada de todos os usuários")
    public ResponseEntity<Page<UsuarioDTO>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nomeCompleto"));
        return ResponseEntity.ok(usuarioService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar usuário por username (ADMIN)")
    public ResponseEntity<UsuarioDTO> buscarPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.findByUsername(username));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar dados do usuário")
    public ResponseEntity<UsuarioDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioDTO usuarioDTO,
            Authentication authentication) {
        String usuarioLogado = authentication.getName();
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, usuarioDTO, usuarioLogado));
    }

    @PostMapping("/{id}/permissoes")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar permissão ao usuário (ADMIN)")
    public ResponseEntity<Map<String, String>> adicionarPermissao(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String permissao = request.get("permissao");
        usuarioService.adicionarPermissao(id, permissao, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Permissão adicionada com sucesso"));
    }

    @DeleteMapping("/{id}/permissoes/{permissao}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remover permissão do usuário (ADMIN)")
    public ResponseEntity<Map<String, String>> removerPermissao(
            @PathVariable Long id,
            @PathVariable String permissao,
            Authentication authentication) {
        usuarioService.removerPermissao(id, permissao, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Permissão removida com sucesso"));
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ativar/Desativar usuário (ADMIN)")
    public ResponseEntity<Map<String, String>> ativarDesativar(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {
        Boolean ativo = request.get("ativo");
        usuarioService.ativarDesativarUsuario(id, ativo, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Status do usuário alterado com sucesso"));
    }

    @PostMapping("/{id}/alterar-senha")
    @PreAuthorize("#id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Alterar própria senha")
    public ResponseEntity<Map<String, String>> alterarSenha(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        usuarioService.alterarSenha(id, request.get("senhaAtual"), request.get("novaSenha"));
        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
    }

    @PostMapping("/resetar-senha")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Resetar senha do usuário (ADMIN)")
    public ResponseEntity<Map<String, String>> resetarSenha(@RequestBody Map<String, String> request) {
        usuarioService.resetarSenha(request.get("email"));
        return ResponseEntity.ok(Map.of("message", "Senha resetada e enviada por email"));
    }
}
