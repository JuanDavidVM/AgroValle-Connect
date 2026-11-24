package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.ReservaRequest;
import com.agrovalle.connect.dto.ReservaResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.EstadoReserva;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.model.Reserva;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.repository.ReservaRepository;
import com.agrovalle.connect.service.CompradorComercialService;
import com.agrovalle.connect.service.ReservaService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de las reservas de inventario.
 */
@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;
    private final CompradorComercialService compradorComercialService;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param reservaRepository         repositorio de reservas
     * @param productoRepository        repositorio de productos
     * @param compradorComercialService caso de uso para resolver al comprador autenticado
     */
    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              ProductoRepository productoRepository,
                              CompradorComercialService compradorComercialService) {
        this.reservaRepository = reservaRepository;
        this.productoRepository = productoRepository;
        this.compradorComercialService = compradorComercialService;
    }

    @Override
    @Transactional
    public ReservaResponse reservar(String correoComprador, ReservaRequest request) {
        CompradorComercial comprador = compradorComercialService.obtenerPorCorreo(correoComprador);
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una oferta con el id " + request.productoId()));

        if (producto.getEstado() != EstadoOferta.ACTIVA) {
            throw new ConflictoException("La oferta no está activa y no admite reservas");
        }
        if (producto.getCantidadDisponible().compareTo(request.cantidadKg()) < 0) {
            throw new ConflictoException("La cantidad solicitada supera el stock disponible: quedan "
                    + sinCerosSobrantes(producto.getCantidadDisponible()) + " " + producto.getUnidadMedida());
        }

        producto.setCantidadDisponible(producto.getCantidadDisponible().subtract(request.cantidadKg()));
        productoRepository.saveAndFlush(producto);

        Reserva reserva = new Reserva();
        reserva.setComprador(comprador);
        reserva.setProducto(producto);
        reserva.setCantidad(request.cantidadKg());
        reserva.setEstado(EstadoReserva.ACTIVA);
        return ReservaResponse.desde(reservaRepository.save(reserva));
    }

    private static String sinCerosSobrantes(BigDecimal cantidad) {
        return cantidad.stripTrailingZeros().toPlainString();
    }
}
