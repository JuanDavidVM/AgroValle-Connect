package com.agrovalle.connect.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "clave-solo-para-pruebas-automatizadas-0123456789";

    private final JwtService jwtService = new JwtService(SECRET, 3_600_000L);

    @Test
    void generarYValidar_tokenCorrecto_devuelveCorreoYRol() {
        String token = jwtService.generarToken("maria@correo.com", "AGRICULTOR");

        assertEquals("maria@correo.com", jwtService.extraerCorreo(token));
        assertEquals("AGRICULTOR", jwtService.validar(token).get("rol", String.class));
    }

    @Test
    void validar_tokenExpirado_lanzaExpiredJwtException() {
        JwtService expirado = new JwtService(SECRET, -1_000L);
        String token = expirado.generarToken("maria@correo.com", "AGRICULTOR");

        assertThrows(ExpiredJwtException.class, () -> jwtService.validar(token));
    }

    @Test
    void validar_tokenManipulado_lanzaJwtException() {
        String token = jwtService.generarToken("maria@correo.com", "AGRICULTOR");
        String manipulado = token.substring(0, token.length() - 3) + "abc";

        assertThrows(JwtException.class, () -> jwtService.validar(manipulado));
    }

    @Test
    void validar_firmadoConOtraClave_lanzaJwtException() {
        JwtService otro = new JwtService("otra-clave-distinta-para-pruebas-987654321012", 3_600_000L);
        String token = otro.generarToken("maria@correo.com", "AGRICULTOR");

        assertThrows(JwtException.class, () -> jwtService.validar(token));
    }

    @Test
    void constructor_secretoCorto_falla() {
        assertThrows(IllegalStateException.class, () -> new JwtService("corto", 1000L));
    }
}
