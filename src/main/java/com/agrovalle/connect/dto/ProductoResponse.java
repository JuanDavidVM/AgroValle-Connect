package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.Producto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        Long fincaId,
        String tipo,
        String descripcion,
        String categoria,
        BigDecimal cantidadKg,
        String unidadMedida,
        BigDecimal precioUnitario,
        LocalDate fechaCosecha,
        String estado,
        String municipio,
        LocalDateTime fechaPublicacion) {

    /**
     * Construye la respuesta a partir del producto persistido.
     *
     * @param p producto publicado
     * @return la respuesta lista para serializar
     */
    public static ProductoResponse desde(Producto p) {
        return new ProductoResponse(p.getId(), p.getFinca().getId(), p.getNombre(), p.getDescripcion(),
                p.getCategoria().getNombre(), p.getCantidadDisponible(), p.getUnidadMedida(),
                p.getPrecioUnitario(), p.getFechaCosecha(), p.getEstado().name(),
                p.getFinca().getMunicipio().getNombre(), p.getFechaPublicacion());
    }
}
