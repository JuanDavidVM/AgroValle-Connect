package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.DetalleOrden;
import java.math.BigDecimal;

public record DetalleOrdenResponse(
        Long productoId,
        String producto,
        BigDecimal cantidadKg,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {

    /**
     * Construye el renglón de la respuesta a partir del detalle persistido.
     *
     * @param d renglón de la orden
     * @return el renglón listo para serializar
     */
    public static DetalleOrdenResponse desde(DetalleOrden d) {
        return new DetalleOrdenResponse(d.getProducto().getId(), d.getProducto().getNombre(),
                d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }
}
