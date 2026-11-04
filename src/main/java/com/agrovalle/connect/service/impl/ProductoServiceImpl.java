package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.dto.ProductoResponse;
import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.repository.FincaRepository;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.service.AgricultorService;
import com.agrovalle.connect.service.ProductoService;
import java.time.LocalDateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final FincaRepository fincaRepository;
    private final AgricultorService agricultorService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crea el servicio con sus dependencias, todas inyectadas como abstracciones.
     *
     * @param productoRepository repositorio de productos
     * @param fincaRepository    repositorio de fincas
     * @param agricultorService  caso de uso para resolver al agricultor autenticado
     * @param eventPublisher     publicador de eventos de dominio (Observer)
     */
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               FincaRepository fincaRepository,
                               AgricultorService agricultorService,
                               ApplicationEventPublisher eventPublisher) {
        this.productoRepository = productoRepository;
        this.fincaRepository = fincaRepository;
        this.agricultorService = agricultorService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ProductoResponse publicar(String correoAgricultor, ProductoRequest request) {
        Agricultor agricultor = agricultorService.obtenerPorCorreo(correoAgricultor);
        Finca finca = fincaRepository.findByIdAndAgricultorId(request.fincaId(), agricultor.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La finca no existe o no pertenece al agricultor autenticado"));

        Producto producto = new Producto();
        producto.setTipo(request.tipo().trim());
        producto.setCategoria(Categoria.desdeNombre(request.categoria()));
        producto.setCantidadKg(request.cantidadKg());
        producto.setPrecioUnitario(request.precioUnitario());
        producto.setFechaCosecha(request.fechaCosecha());
        producto.setEstado(EstadoOferta.ACTIVA);
        producto.setFinca(finca);
        Producto guardado = productoRepository.save(producto);

        eventPublisher.publishEvent(new CosechaPublicadaEvent(
                guardado.getId(), agricultor.getId(), correoAgricultor, guardado.getTipo(),
                guardado.getCantidadKg(), finca.getMunicipio().getNombre(), LocalDateTime.now()));

        return ProductoResponse.desde(guardado);
    }
}
