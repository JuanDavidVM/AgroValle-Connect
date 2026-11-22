package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.dto.ProductoResponse;
import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.repository.FincaRepository;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.service.AgricultorService;
import com.agrovalle.connect.service.CatalogoService;
import com.agrovalle.connect.service.ProductoService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final String UNIDAD_KILOGRAMOS = "kg";

    private final ProductoRepository productoRepository;
    private final FincaRepository fincaRepository;
    private final AgricultorService agricultorService;
    private final CatalogoService catalogoService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crea el servicio con sus dependencias, todas inyectadas como abstracciones.
     *
     * @param productoRepository repositorio de productos
     * @param fincaRepository    repositorio de fincas
     * @param agricultorService  caso de uso para resolver al agricultor autenticado
     * @param catalogoService    consulta de categorías de producto
     * @param eventPublisher     publicador de eventos de dominio (Observer)
     */
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               FincaRepository fincaRepository,
                               AgricultorService agricultorService,
                               CatalogoService catalogoService,
                               ApplicationEventPublisher eventPublisher) {
        this.productoRepository = productoRepository;
        this.fincaRepository = fincaRepository;
        this.agricultorService = agricultorService;
        this.catalogoService = catalogoService;
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
        producto.setNombre(request.tipo().trim());
        producto.setDescripcion(descripcionDe(request));
        producto.setUnidadMedida(UNIDAD_KILOGRAMOS);
        producto.setCategoria(catalogoService.obtenerCategoria(request.categoria()));
        producto.setCantidadDisponible(request.cantidadKg());
        producto.setPrecioUnitario(request.precioUnitario());
        producto.setFechaCosecha(request.fechaCosecha());
        producto.setEstado(EstadoOferta.ACTIVA);
        producto.setFinca(finca);
        Producto guardado = productoRepository.save(producto);

        eventPublisher.publishEvent(new CosechaPublicadaEvent(
                guardado.getId(), agricultor.getId(), correoAgricultor, guardado.getNombre(),
                guardado.getCantidadDisponible(), finca.getMunicipio().getNombre(), LocalDateTime.now()));

        return ProductoResponse.desde(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarOfertas(String municipio, String categoria) {
        Municipio filtroMunicipio = estaVacio(municipio) ? null : catalogoService.obtenerMunicipio(municipio);
        Categoria filtroCategoria = estaVacio(categoria) ? null : catalogoService.obtenerCategoria(categoria);
        return consultar(filtroMunicipio, filtroCategoria).stream().map(ProductoResponse::desde).toList();
    }

    private List<Producto> consultar(Municipio municipio, Categoria categoria) {
        if (municipio != null && categoria != null) {
            return productoRepository.findByEstadoAndFincaMunicipioAndCategoriaOrderByFechaPublicacionDesc(
                    EstadoOferta.ACTIVA, municipio, categoria);
        }
        if (municipio != null) {
            return productoRepository.findByEstadoAndFincaMunicipioOrderByFechaPublicacionDesc(
                    EstadoOferta.ACTIVA, municipio);
        }
        if (categoria != null) {
            return productoRepository.findByEstadoAndCategoriaOrderByFechaPublicacionDesc(
                    EstadoOferta.ACTIVA, categoria);
        }
        return productoRepository.findByEstadoOrderByFechaPublicacionDesc(EstadoOferta.ACTIVA);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String descripcionDe(ProductoRequest request) {
        if (request.descripcion() == null || request.descripcion().isBlank()) {
            return null;
        }
        return request.descripcion().trim();
    }
}
