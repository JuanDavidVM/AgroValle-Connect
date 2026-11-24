package com.agrovalle.connect.model;

/**
 * Estado de una orden de compra directa.
 */
public enum EstadoOrden {

    /** La orden fue emitida y espera que el agricultor aliste el lote. */
    CREADA,

    /** El agricultor confirmó que el lote está alistado. */
    ALISTADA
}
