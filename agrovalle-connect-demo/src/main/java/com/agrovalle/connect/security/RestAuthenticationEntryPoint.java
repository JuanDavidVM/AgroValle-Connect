package com.agrovalle.connect.security;

import com.agrovalle.connect.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** Responde 401 en JSON cuando falta el token, es inválido o expiró. */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        Object motivo = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR);
        String mensaje;
        if (JwtAuthenticationFilter.TOKEN_EXPIRADO.equals(motivo)) {
            mensaje = "El token ha expirado";
        } else if (JwtAuthenticationFilter.TOKEN_INVALIDO.equals(motivo)) {
            mensaje = "El token es inválido";
        } else {
            mensaje = "Se requiere autenticación: envíe el encabezado Authorization: Bearer <token>";
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
                ApiError.of(401, "Unauthorized", mensaje, request.getRequestURI()));
    }
}
