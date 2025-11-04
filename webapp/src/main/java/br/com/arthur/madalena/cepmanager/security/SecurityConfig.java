package br.com.arthur.madalena.cepmanager.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/auth/**",
            "/api/usuarios/registro",
            "/api/usuarios/verificar-email",
            "/api/usuarios/reenviar-verificacao",
            "/jakarta.faces.resource/**",
            "/javax.faces.resource/**",
            "/resources/**",
            "/",
            "/login.xhtml",
            "/registro.xhtml",
            "/verificar-email.xhtml",
            "/pages/registro/**",
            "/pages/verificacao/**"
    };

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UsuarioAtivoFilter usuarioAtivoFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    );
                    oauth2.authenticationEntryPoint((request, response, authException) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(
                            "{\"status\":401,\"message\":\"Autenticação necessária. Por favor, forneça um token válido.\",\"timestamp\":\"" 
                            + java.time.LocalDateTime.now() + "\"}"
                        );
                    });
                    oauth2.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write(
                            "{\"status\":403,\"message\":\"Acesso negado. Você não tem permissão para acessar este recurso.\",\"timestamp\":\"" 
                            + java.time.LocalDateTime.now() + "\"}"
                        );
                    });
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterAfter(usuarioAtivoFilter, org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                "{\"status\":401,\"message\":\"Autenticação necessária. Por favor, faça login.\",\"timestamp\":\"" 
                                + java.time.LocalDateTime.now() + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(
                                "{\"status\":403,\"message\":\"Acesso negado. Você não tem permissão para acessar este recurso.\",\"timestamp\":\"" 
                                + java.time.LocalDateTime.now() + "\"}"
                            );
                        })
                )
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        final SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        final JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        return new NimbusJwtEncoder(immutableSecret);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        final SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
    
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        
        org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

