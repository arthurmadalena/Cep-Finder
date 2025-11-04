package br.com.arthur.madalena.cepmanager.rest;

import br.com.arthur.madalena.cepmanager.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e geração de token JWT")
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica usuário e retorna token JWT")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        final String token = authenticationService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Data
    @AllArgsConstructor
    static class AuthRequest {
        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    static class AuthResponse {
        private String token;
    }
}

