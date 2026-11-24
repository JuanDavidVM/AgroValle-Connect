package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.ItemCarrito;
import com.agrovalle.connect.model.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record ItemCarritoResponse(
        Long id,
        Long productoId,
        String producto,
        Long agricultorId,
        String agricultor,
        BigDecimal cantidadKg,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {

    /**
     * Construye el renglón a partir del ítem persistido, con el subtotal en pesos.
     *
     * @param i ítem del carrito
     * @return el renglón listo para serializar
     */
    public static ItemCarritoResponse desde(ItemCarrito i) {
        Producto producto = i.getProducto();
        Agricultor agricultor = producto.getFinca().getAgricultor();
        BigDecimal subtotal = i.getCantidad().multiply(producto.getPrecioUnitario())
                .setScale(2, RoundingMode.HALF_UP);
        return new ItemCarritoResponse(i.getId(), producto.getId(), producto.getNombre(),
                agricultor.getId(), agricultor.getNombreVisible(), i.getCantidad(),
                producto.getPrecioUnitario(), subtotal);
    }
}
