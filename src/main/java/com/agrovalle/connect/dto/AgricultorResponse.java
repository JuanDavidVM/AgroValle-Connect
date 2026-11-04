package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.Agricultor;
import java.time.LocalDateTime;

public record AgricultorResponse(
        Long id,
        String nombreCompleto,
        String documento,
        String correo,
        String telefono,
        String municipio,
        LocalDateTime fechaRegistro) {

    public static AgricultorResponse desde(Agricultor a) {
        return new AgricultorResponse(a.getId(), a.getNombreCompleto(), a.getDocumento(),
                a.getUsuario().getCorreo(), a.getTelefono(), a.getMunicipio(), a.getFechaRegistro());
    }
}
