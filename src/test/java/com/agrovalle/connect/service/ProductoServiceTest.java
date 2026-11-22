package com.agrovalle.connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.dto.ProductoResponse;
import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.exception.SolicitudInvalidaException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.repository.FincaRepository;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.service.impl.ProductoServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    private static final String CORREO = "maria@correo.com";

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private AgricultorService agricultorService;
    @Mock
    private CatalogoService catalogoService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private ProductoServiceImpl productoService;

    private Agricultor agricultor() {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        return agricultor;
    }

    private Finca finca(Agricultor agricultor) {
        Finca finca = new Finca();
        finca.setId(3L);
        finca.setMunicipio(municipio("Dagua"));
        finca.setAgricultor(agricultor);
        return finca;
    }

    private Municipio municipio(String nombre) {
        Municipio municipio = new Municipio();
        municipio.setNombre(nombre);
        return municipio;
    }

    private Categoria categoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private ProductoRequest request(String descripcion) {
        return new ProductoRequest(3L, "  Plátano hartón ", "frutas", new BigDecimal("150.5"),
                new BigDecimal("2800"), LocalDate.now().plusDays(5), descripcion);
    }

    private ProductoRequest request() {
        return request(null);
    }

    private void prepararPublicacion(Agricultor agricultor) {
        when(agricultorService.obtenerPorCorreo(CORREO)).thenReturn(agricultor);
        when(fincaRepository.findByIdAndAgricultorId(3L, 7L)).thenReturn(Optional.of(finca(agricultor)));
        when(catalogoService.obtenerCategoria("frutas")).thenReturn(categoria("Frutas"));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto producto = inv.getArgument(0);
            producto.setId(99L);
            return producto;
        });
    }

    @Test
    void publicar_datosValidos_persisteActivoYPublicaElEvento() {
        prepararPublicacion(agricultor());

        ProductoResponse respuesta = productoService.publicar(CORREO, request());

        assertEquals(Long.valueOf(99L), respuesta.id());
        assertEquals("Plátano hartón", respuesta.tipo());
        assertEquals("Frutas", respuesta.categoria());
        assertEquals("ACTIVA", respuesta.estado());
        assertEquals("Dagua", respuesta.municipio());
        assertEquals(Long.valueOf(3L), respuesta.fincaId());
        ArgumentCaptor<Producto> producto = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(producto.capture());
        assertEquals(EstadoOferta.ACTIVA, producto.getValue().getEstado());
        assertEquals("Frutas", producto.getValue().getCategoria().getNombre());
        assertEquals("kg", producto.getValue().getUnidadMedida());
        assertEquals("Plátano hartón", producto.getValue().getNombre());
    }

    @Test
    void publicar_datosValidos_emiteUnEventoConLosDatosDeLaCosecha() {
        prepararPublicacion(agricultor());

        productoService.publicar(CORREO, request());

        ArgumentCaptor<CosechaPublicadaEvent> captura = ArgumentCaptor.forClass(CosechaPublicadaEvent.class);
        verify(eventPublisher).publishEvent(captura.capture());
        CosechaPublicadaEvent evento = captura.getValue();
        assertEquals(Long.valueOf(99L), evento.productoId());
        assertEquals(Long.valueOf(7L), evento.agricultorId());
        assertEquals(CORREO, evento.correoAgricultor());
        assertEquals("Plátano hartón", evento.tipo());
        assertEquals(new BigDecimal("150.5"), evento.cantidadKg());
        assertEquals("Dagua", evento.municipio());
    }

    @Test
    void publicar_conDescripcion_laGuardaSinEspaciosSobrantes() {
        prepararPublicacion(agricultor());

        ProductoResponse respuesta = productoService.publicar(CORREO, request("  Plátano de primera calidad "));

        assertEquals("Plátano de primera calidad", respuesta.descripcion());
    }

    @Test
    void publicar_descripcionEnBlanco_seGuardaSinDescripcion() {
        prepararPublicacion(agricultor());

        ProductoResponse respuesta = productoService.publicar(CORREO, request("   "));

        assertNull(respuesta.descripcion());
    }

    @Test
    void publicar_fincaAjenaOInexistente_lanzaNoEncontradoYNoPublicaNada() {
        Agricultor agricultor = agricultor();
        when(agricultorService.obtenerPorCorreo(CORREO)).thenReturn(agricultor);
        when(fincaRepository.findByIdAndAgricultorId(3L, 7L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.publicar(CORREO, request()));

        verify(productoRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void publicar_usuarioSinPerfilDeAgricultor_propagaNoEncontrado() {
        when(agricultorService.obtenerPorCorreo(CORREO))
                .thenThrow(new RecursoNoEncontradoException("No existe un agricultor asociado"));

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.publicar(CORREO, request()));

        verifyNoInteractions(fincaRepository, productoRepository, eventPublisher);
    }

    private Producto productoPublicado(String nombre) {
        Finca finca = finca(agricultor());
        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre(nombre);
        producto.setUnidadMedida("kg");
        producto.setCantidadDisponible(new BigDecimal("100"));
        producto.setPrecioUnitario(new BigDecimal("2800"));
        producto.setFechaCosecha(LocalDate.now().plusDays(3));
        producto.setEstado(EstadoOferta.ACTIVA);
        producto.setCategoria(categoria("Frutas"));
        producto.setFinca(finca);
        return producto;
    }

    @Test
    void buscarOfertas_sinFiltros_devuelveTodasLasActivasSinConsultarElCatalogo() {
        when(productoRepository.findByEstadoOrderByFechaPublicacionDesc(EstadoOferta.ACTIVA))
                .thenReturn(List.of(productoPublicado("Plátano")));

        List<ProductoResponse> ofertas = productoService.buscarOfertas(null, "  ");

        assertEquals(1, ofertas.size());
        assertEquals("Plátano", ofertas.get(0).tipo());
        verifyNoInteractions(catalogoService);
    }

    @Test
    void buscarOfertas_soloMunicipio_filtraPorMunicipio() {
        Municipio dagua = municipio("Dagua");
        when(catalogoService.obtenerMunicipio("Dagua")).thenReturn(dagua);
        when(productoRepository.findByEstadoAndFincaMunicipioOrderByFechaPublicacionDesc(
                EstadoOferta.ACTIVA, dagua)).thenReturn(List.of(productoPublicado("Plátano")));

        List<ProductoResponse> ofertas = productoService.buscarOfertas("Dagua", null);

        assertEquals(1, ofertas.size());
        assertEquals("Dagua", ofertas.get(0).municipio());
    }

    @Test
    void buscarOfertas_soloCategoria_filtraPorCategoria() {
        Categoria frutas = categoria("Frutas");
        when(catalogoService.obtenerCategoria("Frutas")).thenReturn(frutas);
        when(productoRepository.findByEstadoAndCategoriaOrderByFechaPublicacionDesc(
                EstadoOferta.ACTIVA, frutas)).thenReturn(List.of(productoPublicado("Mango")));

        List<ProductoResponse> ofertas = productoService.buscarOfertas(null, "Frutas");

        assertEquals("Mango", ofertas.get(0).tipo());
    }

    @Test
    void buscarOfertas_municipioYCategoria_filtraPorAmbos() {
        Municipio dagua = municipio("Dagua");
        Categoria frutas = categoria("Frutas");
        when(catalogoService.obtenerMunicipio("Dagua")).thenReturn(dagua);
        when(catalogoService.obtenerCategoria("Frutas")).thenReturn(frutas);
        when(productoRepository.findByEstadoAndFincaMunicipioAndCategoriaOrderByFechaPublicacionDesc(
                EstadoOferta.ACTIVA, dagua, frutas)).thenReturn(List.of(productoPublicado("Plátano")));

        List<ProductoResponse> ofertas = productoService.buscarOfertas("Dagua", "Frutas");

        assertEquals(1, ofertas.size());
    }

    @Test
    void buscarOfertas_sinCoincidencias_devuelveListaVacia() {
        Municipio buga = municipio("Buga");
        Categoria frutas = categoria("Frutas");
        when(catalogoService.obtenerMunicipio("Buga")).thenReturn(buga);
        when(catalogoService.obtenerCategoria("Frutas")).thenReturn(frutas);
        when(productoRepository.findByEstadoAndFincaMunicipioAndCategoriaOrderByFechaPublicacionDesc(
                EstadoOferta.ACTIVA, buga, frutas)).thenReturn(List.of());

        assertEquals(0, productoService.buscarOfertas("Buga", "Frutas").size());
    }

    @Test
    void buscarOfertas_municipioFueraDelCatalogo_lanzaSolicitudInvalidaSinConsultarProductos() {
        when(catalogoService.obtenerMunicipio("Bogotá"))
                .thenThrow(new SolicitudInvalidaException("El municipio no pertenece al Valle del Cauca"));

        assertThrows(SolicitudInvalidaException.class, () -> productoService.buscarOfertas("Bogotá", null));

        verifyNoInteractions(productoRepository);
    }
}
