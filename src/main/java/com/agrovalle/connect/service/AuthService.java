package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.LoginRequest;
import com.agrovalle.connect.dto.LoginResponse;
import com.agrovalle.connect.dto.PerfilResponse;
import org.springframework.security.core.Authentication;

/**
 * Casos de uso de autenticación.
 */
public interface AuthService {

    /**
     * Autentica al usuario y emite un token JWT.
     *
     * @param request correo y contraseña
     * @return token, tipo, vigencia en segundos y rol
     */
    LoginResponse login(LoginRequest request);

    /**
     * Devuelve los datos básicos del usuario autenticado.
     *
     * @param auth autenticación vigente
     * @return correo y rol
     */
    PerfilResponse perfil(Authentication auth);
}
