package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.CompradorComercial;
import java.time.LocalDateTime;

public record CompradorComercialResponse(
        Long id,
        String razonSocial,
        String nit,
        String correo,
        String telefono,
        LocalDateTime fechaRegistro) {

    public static CompradorComercialResponse desde(CompradorComercial c) {
        return new CompradorComercialResponse(c.getId(), c.getRazonSocial(), c.getNit(),
                c.getUsuario().getCorreo(), c.getTelefono(), c.getFechaRegistro());
    }
}
