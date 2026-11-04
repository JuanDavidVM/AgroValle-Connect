package com.agrovalle.connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        finca.setMunicipio(Municipio.DAGUA);
        finca.setAgricultor(agricultor);
        return finca;
    }

    private ProductoRequest request() {
        return new ProductoRequest(3L, "  Plátano hartón ", "frutas", new BigDecimal("150.5"),
                new BigDecimal("2800"), LocalDate.now().plusDays(5));
    }

    @Test
    void publicar_datosValidos_persisteActivoYPublicaElEvento() {
        Agricultor agricultor = agricultor();
        when(agricultorService.obtenerPorCorreo(CORREO)).thenReturn(agricultor);
        when(fincaRepository.findByIdAndAgricultorId(3L, 7L)).thenReturn(Optional.of(finca(agricultor)));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto producto = inv.getArgument(0);
            producto.setId(99L);
            return producto;
        });

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
        assertEquals(Categoria.FRUTAS, producto.getValue().getCategoria());
    }

    @Test
    void publicar_datosValidos_emiteUnEventoConLosDatosDeLaCosecha() {
        Agricultor agricultor = agricultor();
        when(agricultorService.obtenerPorCorreo(CORREO)).thenReturn(agricultor);
        when(fincaRepository.findByIdAndAgricultorId(3L, 7L)).thenReturn(Optional.of(finca(agricultor)));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto producto = inv.getArgument(0);
            producto.setId(99L);
            return producto;
        });

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
}
