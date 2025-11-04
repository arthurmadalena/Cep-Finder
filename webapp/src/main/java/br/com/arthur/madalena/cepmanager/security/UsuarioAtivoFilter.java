package br.com.arthur.madalena.cepmanager.security;

import br.com.arthur.madalena.cepmanager.dao.UsuarioDAO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioAtivoFilter extends OncePerRequestFilter {

    private final UsuarioDAO usuarioDAO;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Ignora validação para endpoints públicos
        String requestURI = request.getRequestURI();
        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verifica se há autenticação
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")) {
            
            String username = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioDAO.findByUsername(username);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                // Valida se o usuário está ativo
                if (!usuario.getAtivo()) {
                    enviarErro(response, HttpServletResponse.SC_FORBIDDEN, 
                            "Usuário inativo", 
                            "Sua conta foi desativada. Entre em contato com o administrador.");
                    return;
                }
                
                // Valida se o email foi verificado
                if (!usuario.getEmailVerificado()) {
                    enviarErro(response, HttpServletResponse.SC_FORBIDDEN, 
                            "Email não verificado", 
                            "Por favor, verifique seu email para ativar a conta.");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.contains("/api/auth/") ||
               requestURI.contains("/api/usuarios/registro") ||
               requestURI.contains("/api/usuarios/verificar-email") ||
               requestURI.contains("/api/usuarios/reenviar-verificacao") ||
               requestURI.contains("/swagger-ui") ||
               requestURI.contains("/v3/api-docs") ||
               requestURI.contains("/jakarta.faces.resource") ||
               requestURI.contains("/javax.faces.resource") ||
               requestURI.contains("/resources/") ||
               requestURI.endsWith(".xhtml") ||
               requestURI.equals("/") ||
               requestURI.contains("/login") ||
               requestURI.contains("/registro") ||
               requestURI.contains("/verificar-email");
    }

    private void enviarErro(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

