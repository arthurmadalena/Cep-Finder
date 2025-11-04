package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setEmail("admin@test.com");
        usuario.setPermissoes(Set.of("ROLE_ADMIN", "ROLE_USER"));

        when(jwtProperties.getIssuer()).thenReturn("CepFinder");
        when(jwtProperties.getExpireDuration()).thenReturn(Duration.ofHours(24));
    }

    @Test
    void deveGerarTokenComDadosDoUsuario() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("token.jwt.gerado");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String token = jwtService.encode(usuario);

        assertThat(token).isNotNull();
        assertThat(token).isEqualTo("token.jwt.gerado");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void deveChamarJwtEncoderComParametrosCorretos() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        jwtService.encode(usuario);

        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
        verify(jwtProperties).getIssuer();
        verify(jwtProperties).getExpireDuration();
    }

    @Test
    void deveUsarIssuerDasPropriedades() {
        when(jwtProperties.getIssuer()).thenReturn("TestIssuer");
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        jwtService.encode(usuario);

        verify(jwtProperties).getIssuer();
    }

    @Test
    void deveUsarDuracaoDasPropriedades() {
        when(jwtProperties.getExpireDuration()).thenReturn(Duration.ofHours(12));
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        jwtService.encode(usuario);

        verify(jwtProperties).getExpireDuration();
    }

    @Test
    void deveGerarTokenDiferenteParaUsuariosDiferentes() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("user");
        usuario2.setEmail("user@test.com");
        usuario2.setPermissoes(Set.of("ROLE_USER"));

        Jwt mockJwt1 = mock(Jwt.class);
        Jwt mockJwt2 = mock(Jwt.class);
        when(mockJwt1.getTokenValue()).thenReturn("token1");
        when(mockJwt2.getTokenValue()).thenReturn("token2");
        
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockJwt1)
                .thenReturn(mockJwt2);

        String token1 = jwtService.encode(usuario);
        String token2 = jwtService.encode(usuario2);

        assertThat(token1).isNotEqualTo(token2);
    }
}

