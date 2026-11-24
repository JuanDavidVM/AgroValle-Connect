package com.agrovalle.connect.factory;

import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.DetalleOrden;
import com.agrovalle.connect.model.EstadoOrden;
import com.agrovalle.connect.model.ItemCarrito;
import com.agrovalle.connect.model.Orden;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Fábrica de órdenes de compra (patrón Factory).
 *
 * <p>Concentra en un solo punto cómo nace una orden: su estado inicial, la copia del precio vigente a
 * cada renglón y el formato del número de orden. El servicio decide <em>cuándo</em> crear órdenes y
 * cuántas; esta clase decide <em>cómo</em> queda construida cada una.</p>
 */
@Component
public class OrdenFactory {

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int LONGITUD_SUFIJO = 8;

    /**
     * Construye una orden para un agricultor a partir de los renglones de carrito que le corresponden.
     *
     * <p>La orden queda en estado {@link EstadoOrden#CREADA} con su número ya asignado.</p>
     *
     * @param comprador  comprador que emite la orden
     * @param agricultor agricultor dueño de las ofertas
     * @param items      renglones del carrito de ese agricultor
     * @return la orden construida, con sus renglones y el precio congelado
     */
    public Orden crear(CompradorComercial comprador, Agricultor agricultor, List<ItemCarrito> items) {
        Orden orden = new Orden();
        orden.setComprador(comprador);
        orden.setAgricultor(agricultor);
        orden.setEstado(EstadoOrden.CREADA);
        orden.setNumeroOrden(numeroNuevo());
        for (ItemCarrito item : items) {
            DetalleOrden detalle = new DetalleOrden();
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getProducto().getPrecioUnitario());
            orden.agregarDetalle(detalle);
        }
        return orden;
    }

    private static String numeroNuevo() {
        String sufijo = UUID.randomUUID().toString().substring(0, LONGITUD_SUFIJO).toUpperCase(Locale.ROOT);
        return "AV-" + LocalDate.now().format(DIA) + "-" + sufijo;
    }
}
