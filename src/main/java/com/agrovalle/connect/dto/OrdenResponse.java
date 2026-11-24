package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.Orden;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponse(
        Long id,
        String numeroOrden,
        String estado,
        Long agricultorId,
        String agricultor,
        List<DetalleOrdenResponse> detalles,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaAlistamiento) {

    /**
     * Construye la respuesta a partir de la orden emitida.
     *
     * @param o orden persistida con sus renglones
     * @return la respuesta con el número de orden y el total en pesos
     */
    public static OrdenResponse desde(Orden o) {
        return new OrdenResponse(o.getId(), o.getNumeroOrden(), o.getEstado().name(),
                o.getAgricultor().getId(), o.getAgricultor().getNombreVisible(),
                o.getDetalles().stream().map(DetalleOrdenResponse::desde).toList(),
                o.getTotal(), o.getFechaCreacion(), o.getFechaAlistamiento());
    }
}
