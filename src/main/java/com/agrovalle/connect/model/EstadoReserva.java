package com.agrovalle.connect.model;

/**
 * Estado de una reserva de inventario sobre una oferta de cosecha.
 */
public enum EstadoReserva {

    /** La reserva mantiene el stock apartado y todavía no se ha convertido en orden. */
    ACTIVA,

    /** La reserva quedó incorporada a una orden de compra emitida. */
    CONSUMIDA,

    /** La reserva se anuló y su cantidad volvió al stock disponible. */
    CANCELADA
}
