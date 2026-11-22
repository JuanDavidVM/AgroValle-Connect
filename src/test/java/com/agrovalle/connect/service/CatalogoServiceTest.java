package com.agrovalle.connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.exception.SolicitudInvalidaException;
import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.repository.CategoriaRepository;
import com.agrovalle.connect.repository.MunicipioRepository;
import com.agrovalle.connect.service.impl.CatalogoServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private MunicipioRepository municipioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private CatalogoServiceImpl catalogoService;

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

    @Test
    void buscarMunicipio_nombreExacto_devuelveElMunicipio() {
        Municipio tulua = municipio("Tuluá");
        when(municipioRepository.findAll()).thenReturn(List.of(municipio("Dagua"), tulua));

        assertEquals(tulua, catalogoService.buscarMunicipio("Tuluá").orElseThrow());
    }

    @Test
    void buscarMunicipio_ignoraTildesMayusculasYEspacios() {
        Municipio aguila = municipio("El Águila");
        when(municipioRepository.findAll()).thenReturn(List.of(municipio("Dagua"), aguila));

        assertEquals(aguila, catalogoService.buscarMunicipio("  EL   aguila ").orElseThrow());
    }

    @Test
    void buscarMunicipio_fueraDelValle_devuelveVacio() {
        when(municipioRepository.findAll()).thenReturn(List.of(municipio("Dagua")));

        assertTrue(catalogoService.buscarMunicipio("Bogotá").isEmpty());
    }

    @Test
    void buscarMunicipio_nulo_devuelveVacioSinConsultarLaBaseDeDatos() {
        assertTrue(catalogoService.buscarMunicipio(null).isEmpty());

        verifyNoInteractions(municipioRepository);
    }

    @Test
    void obtenerMunicipio_valido_devuelveElMunicipio() {
        Municipio dagua = municipio("Dagua");
        when(municipioRepository.findAll()).thenReturn(List.of(dagua));

        assertEquals(dagua, catalogoService.obtenerMunicipio("dagua"));
    }

    @Test
    void obtenerMunicipio_invalido_lanzaSolicitudInvalida() {
        when(municipioRepository.findAll()).thenReturn(List.of(municipio("Dagua")));

        assertThrows(SolicitudInvalidaException.class, () -> catalogoService.obtenerMunicipio("Medellín"));
    }

    @Test
    void buscarCategoria_ignoraTildesMayusculasYEspacios() {
        Categoria tuberculos = categoria("Tubérculos");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria("Frutas"), tuberculos));

        assertEquals(tuberculos, catalogoService.buscarCategoria("  TUBERCULOS ").orElseThrow());
    }

    @Test
    void buscarCategoria_fueraDelCatalogo_devuelveVacio() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria("Frutas")));

        assertTrue(catalogoService.buscarCategoria("Electrodomésticos").isEmpty());
    }

    @Test
    void buscarCategoria_nula_devuelveVacioSinConsultarLaBaseDeDatos() {
        assertTrue(catalogoService.buscarCategoria(null).isEmpty());

        verifyNoInteractions(categoriaRepository);
    }

    @Test
    void obtenerCategoria_valida_devuelveLaCategoria() {
        Categoria frutas = categoria("Frutas");
        when(categoriaRepository.findAll()).thenReturn(List.of(frutas));

        assertEquals(frutas, catalogoService.obtenerCategoria("frutas"));
    }

    @Test
    void obtenerCategoria_invalida_lanzaSolicitudInvalida() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria("Frutas")));

        assertThrows(SolicitudInvalidaException.class, () -> catalogoService.obtenerCategoria("Ropa"));
    }
}
