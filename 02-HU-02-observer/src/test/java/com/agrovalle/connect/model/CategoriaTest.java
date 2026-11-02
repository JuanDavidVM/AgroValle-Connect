package com.agrovalle.connect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void buscar_nombreExacto_devuelveLaCategoria() {
        assertEquals(Optional.of(Categoria.FRUTAS), Categoria.buscar("Frutas"));
    }

    @Test
    void buscar_ignoraTildesMayusculasYEspacios() {
        assertEquals(Optional.of(Categoria.TUBERCULOS), Categoria.buscar("  TUBERCULOS "));
        assertEquals(Optional.of(Categoria.CAFE), Categoria.buscar("cafe"));
    }

    @Test
    void buscar_categoriaFueraDelCatalogo_devuelveVacio() {
        assertTrue(Categoria.buscar("Electrodomésticos").isEmpty());
    }

    @Test
    void buscar_nulo_devuelveVacio() {
        assertTrue(Categoria.buscar(null).isEmpty());
    }

    @Test
    void desdeNombre_categoriaValida_devuelveLaCategoria() {
        assertEquals(Categoria.VERDURAS, Categoria.desdeNombre("verduras"));
    }

    @Test
    void desdeNombre_categoriaInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Categoria.desdeNombre("Ropa"));
    }

    @Test
    void getNombre_devuelveElNombreOficialConTildes() {
        assertEquals("Tubérculos", Categoria.TUBERCULOS.getNombre());
    }
}
