package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.Finca;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FincaResponse(
        Long id,
        String nombreFinca,
        String municipio,
        BigDecimal hectareas,
        Long agricultorId,
        LocalDateTime fechaRegistro) {

    public static FincaResponse desde(Finca f) {
        return new FincaResponse(f.getId(), f.getNombre(), f.getMunicipio().getNombre(),
                f.getHectareas(), f.getAgricultor().getId(), f.getFechaRegistro());
    }
}
