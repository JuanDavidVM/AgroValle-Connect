package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.LoginRequest;
import com.agrovalle.connect.dto.LoginResponse;
import com.agrovalle.connect.dto.PerfilResponse;
import com.agrovalle.connect.security.JwtService;
import com.agrovalle.connect.service.AuthService;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String PREFIJO_ROL = "ROLE_";
    private static final long MILISEGUNDOS_POR_SEGUNDO = 1000L;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        // Lanza BadCredentialsException si el usuario no existe o la contraseña no coincide
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, request.contrasena()));
        String rol = rolDe(auth);
        String token = jwtService.generarToken(correo, rol);
        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs() / MILISEGUNDOS_POR_SEGUNDO, rol);
    }

    @Override
    public PerfilResponse perfil(Authentication auth) {
        return new PerfilResponse(auth.getName(), rolDe(auth));
    }

    private String rolDe(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replaceFirst("^" + PREFIJO_ROL, ""))
                .orElseThrow(() -> new IllegalStateException("El usuario no tiene rol asignado"));
    }
}
