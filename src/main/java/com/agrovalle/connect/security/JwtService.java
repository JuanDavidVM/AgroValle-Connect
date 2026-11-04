package com.agrovalle.connect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final int LONGITUD_MINIMA_SECRETO = 32;

    private final SecretKey key;
    private final long expirationMs;

    /**
     * Crea el servicio validando que el secreto tenga la longitud mínima exigida.
     *
     * @param secret       clave de firma (mínimo 32 caracteres)
     * @param expirationMs vigencia del token en milisegundos
     */
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < LONGITUD_MINIMA_SECRETO) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Genera un token JWT firmado para el usuario indicado.
     *
     * @param correo correo del usuario (subject del token)
     * @param rol    rol que se incluye como claim
     * @return el token compacto
     */
    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Valida firma y expiración. Lanza {@link io.jsonwebtoken.ExpiredJwtException} si expiró
     * y {@link io.jsonwebtoken.JwtException} si el token es inválido.
     */
    public Claims validar(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String extraerCorreo(String token) {
        return validar(token).getSubject();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
