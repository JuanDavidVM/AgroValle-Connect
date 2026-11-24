package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.OrdenResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.factory.OrdenFactory;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.EstadoReserva;
import com.agrovalle.connect.model.ItemCarrito;
import com.agrovalle.connect.model.Orden;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.model.Reserva;
import com.agrovalle.connect.repository.ItemCarritoRepository;
import com.agrovalle.connect.repository.OrdenRepository;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.repository.ReservaRepository;
import com.agrovalle.connect.service.CompradorComercialService;
import com.agrovalle.connect.service.OrdenService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la emisión de órdenes de compra.
 */
@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;
    private final CompradorComercialService compradorComercialService;
    private final OrdenFactory ordenFactory;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param ordenRepository           repositorio de órdenes
     * @param itemCarritoRepository     repositorio de renglones del carrito
     * @param reservaRepository         repositorio de reservas
     * @param productoRepository        repositorio de productos
     * @param compradorComercialService caso de uso para resolver al comprador autenticado
     * @param ordenFactory              fábrica que construye cada orden
     */
    public OrdenServiceImpl(OrdenRepository ordenRepository,
                            ItemCarritoRepository itemCarritoRepository,
                            ReservaRepository reservaRepository,
                            ProductoRepository productoRepository,
                            CompradorComercialService compradorComercialService,
                            OrdenFactory ordenFactory) {
        this.ordenRepository = ordenRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.reservaRepository = reservaRepository;
        this.productoRepository = productoRepository;
        this.compradorComercialService = compradorComercialService;
        this.ordenFactory = ordenFactory;
    }

    @Override
    @Transactional
    public List<OrdenResponse> emitir(String correoComprador) {
        CompradorComercial comprador = compradorComercialService.obtenerPorCorreo(correoComprador);
        List<ItemCarrito> items = itemCarritoRepository.findByCompradorIdOrderByIdAsc(comprador.getId());
        if (items.isEmpty()) {
            throw new ConflictoException("El carrito está vacío: no hay nada que ordenar");
        }

        Map<Long, List<ItemCarrito>> porAgricultor = new LinkedHashMap<>();
        for (ItemCarrito item : items) {
            Long agricultorId = item.getProducto().getFinca().getAgricultor().getId();
            porAgricultor.computeIfAbsent(agricultorId, id -> new ArrayList<>()).add(item);
        }

        List<OrdenResponse> emitidas = new ArrayList<>();
        for (List<ItemCarrito> delAgricultor : porAgricultor.values()) {
            Agricultor agricultor = delAgricultor.get(0).getProducto().getFinca().getAgricultor();
            Orden orden = ordenRepository.save(ordenFactory.crear(comprador, agricultor, delAgricultor));
            for (ItemCarrito item : delAgricultor) {
                consumirReservas(comprador, item, orden);
            }
            emitidas.add(OrdenResponse.desde(orden));
        }

        itemCarritoRepository.deleteAll(items);
        return emitidas;
    }

    private void consumirReservas(CompradorComercial comprador, ItemCarrito item, Orden orden) {
        List<Reserva> activas = reservaRepository.findByCompradorIdAndProductoIdAndEstado(
                comprador.getId(), item.getProducto().getId(), EstadoReserva.ACTIVA);
        BigDecimal reservado = activas.stream()
                .map(Reserva::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (reservado.compareTo(item.getCantidad()) < 0) {
            throw new ConflictoException("La oferta " + item.getProducto().getNombre()
                    + " no tiene stock reservado suficiente: hay " + sinCerosSobrantes(reservado)
                    + " reservados y la orden pide " + sinCerosSobrantes(item.getCantidad()));
        }

        for (Reserva reserva : activas) {
            reserva.setEstado(EstadoReserva.CONSUMIDA);
            reserva.setOrden(orden);
        }

        BigDecimal sobrante = reservado.subtract(item.getCantidad());
        if (sobrante.compareTo(BigDecimal.ZERO) > 0) {
            Producto producto = item.getProducto();
            producto.setCantidadDisponible(producto.getCantidadDisponible().add(sobrante));
            productoRepository.save(producto);
        }
    }

    private static String sinCerosSobrantes(BigDecimal cantidad) {
        return cantidad.stripTrailingZeros().toPlainString();
    }
}
