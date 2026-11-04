package com.agrovalle.connect.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.repository.CategoriaRepository;
import com.agrovalle.connect.repository.MunicipioRepository;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogoInicializadorTest {

    @Mock
    private MunicipioRepository municipioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private CatalogoInicializador inicializador;

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
    void catalogos_tienenLos42MunicipiosDelValleSinRepetidosYSeisCategorias() {
        assertEquals(42, new HashSet<>(CatalogoInicializador.MUNICIPIOS).size());
        assertEquals(6, new HashSet<>(CatalogoInicializador.CATEGORIAS).size());
    }

    @Test
    void run_baseDeDatosVacia_insertaTodosLosCatalogos() {
        when(municipioRepository.findAll()).thenReturn(List.of());
        when(categoriaRepository.findAll()).thenReturn(List.of());

        inicializador.run(null);

        verify(municipioRepository, times(42)).save(any(Municipio.class));
        verify(categoriaRepository, times(6)).save(any(Categoria.class));
    }

    @Test
    void run_catalogosCompletos_noInsertaNada() {
        when(municipioRepository.findAll()).thenReturn(
                CatalogoInicializador.MUNICIPIOS.stream().map(this::municipio).toList());
        when(categoriaRepository.findAll()).thenReturn(
                CatalogoInicializador.CATEGORIAS.stream().map(this::categoria).toList());

        inicializador.run(null);

        verify(municipioRepository, never()).save(any());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void run_catalogoParcial_soloInsertaLosFaltantes() {
        when(municipioRepository.findAll()).thenReturn(List.of(municipio("Dagua")));
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria("Frutas"), categoria("Café")));

        inicializador.run(null);

        verify(municipioRepository, times(41)).save(any(Municipio.class));
        verify(categoriaRepository, times(4)).save(any(Categoria.class));
    }
}
