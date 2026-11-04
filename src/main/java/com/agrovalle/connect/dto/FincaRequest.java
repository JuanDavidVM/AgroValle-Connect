package com.agrovalle.connect.dto;

import com.agrovalle.connect.validation.MunicipioDelValle;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record FincaRequest(
        @NotBlank(message = "El nombre de la finca es obligatorio")
        @Size(max = 100, message = "El nombre de la finca no puede superar 100 caracteres")
        String nombreFinca,

        @NotBlank(message = "El municipio es obligatorio")
        @MunicipioDelValle
        String municipio,

        @NotNull(message = "Las hectáreas son obligatorias")
        @Positive(message = "Las hectáreas deben ser mayores a cero")
        @Digits(integer = 6, fraction = 2, message = "Las hectáreas admiten hasta 6 enteros y 2 decimales")
        BigDecimal hectareas) {
}
