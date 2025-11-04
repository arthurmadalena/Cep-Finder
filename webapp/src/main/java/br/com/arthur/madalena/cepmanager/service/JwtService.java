package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import br.com.arthur.madalena.cepmanager.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public String encode(Usuario usuario) {
        final Instant now = Instant.now();

        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.getExpireDuration()))
                .claim("authorities", usuario.getPermissoes())
                .claim("userId", usuario.getId())
                .claim("email", usuario.getEmail())
                .build();

        final JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}

