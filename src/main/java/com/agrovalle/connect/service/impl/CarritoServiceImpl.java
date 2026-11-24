package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.CarritoResponse;
import com.agrovalle.connect.dto.ItemCarritoRequest;
import com.agrovalle.connect.dto.ItemCarritoResponse;
import com.agrovalle.connect.dto.SubtotalAgricultorResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.ItemCarrito;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.repository.ItemCarritoRepository;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.service.CarritoService;
import com.agrovalle.connect.service.CompradorComercialService;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del carrito de compra.
 */
@Service
public class CarritoServiceImpl implements CarritoService {

    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;
    private final CompradorComercialService compradorComercialService;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param itemCarritoRepository     repositorio de renglones del carrito
     * @param productoRepository        repositorio de productos
     * @param compradorComercialService caso de uso para resolver al comprador autenticado
     */
    public CarritoServiceImpl(ItemCarritoRepository itemCarritoRepository,
                              ProductoRepository productoRepository,
                              CompradorComercialService compradorComercialService) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoRepository = productoRepository;
        this.compradorComercialService = compradorComercialService;
    }

    @Override
    @Transactional
    public ItemCarritoResponse agregarItem(String correoComprador, ItemCarritoRequest request) {
        CompradorComercial comprador = compradorComercialService.obtenerPorCorreo(correoComprador);
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una oferta con el id " + request.productoId()));

        if (producto.getEstado() != EstadoOferta.ACTIVA) {
            throw new ConflictoException("La oferta no está activa y no puede agregarse al carrito");
        }

        ItemCarrito item = itemCarritoRepository
                .findByCompradorIdAndProductoId(comprador.getId(), producto.getId())
                .orElseGet(() -> nuevoItem(comprador, producto));
        BigDecimal cantidadFinal = item.getCantidad() == null
                ? request.cantidadKg()
                : item.getCantidad().add(request.cantidadKg());

        if (producto.getCantidadDisponible().compareTo(cantidadFinal) < 0) {
            throw new ConflictoException("La cantidad acumulada en el carrito supera el stock disponible: quedan "
                    + sinCerosSobrantes(producto.getCantidadDisponible()) + " " + producto.getUnidadMedida());
        }

        item.setCantidad(cantidadFinal);
        return ItemCarritoResponse.desde(itemCarritoRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoResponse obtener(String correoComprador) {
        CompradorComercial comprador = compradorComercialService.obtenerPorCorreo(correoComprador);
        List<ItemCarritoResponse> items = itemCarritoRepository
                .findByCompradorIdOrderByIdAsc(comprador.getId())
                .stream().map(ItemCarritoResponse::desde).toList();

        Map<Long, SubtotalAgricultorResponse> porAgricultor = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarritoResponse item : items) {
            porAgricultor.merge(item.agricultorId(),
                    new SubtotalAgricultorResponse(item.agricultorId(), item.agricultor(), item.subtotal()),
                    (previo, nuevo) -> new SubtotalAgricultorResponse(previo.agricultorId(),
                            previo.agricultor(), previo.subtotal().add(nuevo.subtotal())));
            total = total.add(item.subtotal());
        }
        return new CarritoResponse(items, List.copyOf(porAgricultor.values()), total);
    }

    private ItemCarrito nuevoItem(CompradorComercial comprador, Producto producto) {
        ItemCarrito item = new ItemCarrito();
        item.setComprador(comprador);
        item.setProducto(producto);
        return item;
    }

    private static String sinCerosSobrantes(BigDecimal cantidad) {
        return cantidad.stripTrailingZeros().toPlainString();
    }
}
