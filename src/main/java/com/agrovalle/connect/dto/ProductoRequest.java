package com.agrovalle.connect.dto;

import com.agrovalle.connect.validation.CategoriaValida;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoRequest(
        @NotNull(message = "La finca de origen es obligatoria")
        Long fincaId,

        @NotBlank(message = "El tipo de producto es obligatorio")
        @Size(max = 80, message = "El tipo de producto no puede superar 80 caracteres")
        String tipo,

        @NotBlank(message = "La categoría es obligatoria")
        @CategoriaValida
        String categoria,

        @NotNull(message = "La cantidad en kg es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        @Digits(integer = 8, fraction = 2, message = "La cantidad admite hasta 8 enteros y 2 decimales")
        BigDecimal cantidadKg,

        @NotNull(message = "El precio unitario es obligatorio")
        @Positive(message = "El precio unitario debe ser mayor a cero")
        @Digits(integer = 10, fraction = 2, message = "El precio admite hasta 10 enteros y 2 decimales")
        BigDecimal precioUnitario,

        @NotNull(message = "La fecha de cosecha es obligatoria")
        @FutureOrPresent(message = "La fecha de cosecha no puede ser anterior a hoy")
        LocalDate fechaCosecha,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion) {
}
