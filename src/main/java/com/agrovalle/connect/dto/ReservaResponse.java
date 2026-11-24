package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.Reserva;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponse(
        Long id,
        Long productoId,
        String producto,
        BigDecimal cantidadKg,
        BigDecimal stockRestante,
        String estado,
        LocalDateTime fechaReserva) {

    /**
     * Construye la respuesta a partir de la reserva persistida.
     *
     * @param r reserva confirmada
     * @return la respuesta lista para serializar, con el stock que queda en la oferta
     */
    public static ReservaResponse desde(Reserva r) {
        return new ReservaResponse(r.getId(), r.getProducto().getId(), r.getProducto().getNombre(),
                r.getCantidad(), r.getProducto().getCantidadDisponible(), r.getEstado().name(),
                r.getFechaReserva());
    }
}
