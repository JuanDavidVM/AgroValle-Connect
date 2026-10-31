package com.agrovalle.connect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class MunicipioTest {

    @Test
    void catalogo_tiene42MunicipiosDelValle() {
        assertEquals(42, Municipio.values().length);
    }

    @Test
    void buscar_nombreExacto_devuelveElMunicipio() {
        assertEquals(Optional.of(Municipio.TULUA), Municipio.buscar("Tuluá"));
    }

    @Test
    void buscar_ignoraTildesMayusculasYEspacios() {
        assertEquals(Optional.of(Municipio.TULUA), Municipio.buscar("  tulua "));
        assertEquals(Optional.of(Municipio.EL_AGUILA), Municipio.buscar("EL   aguila"));
    }

    @Test
    void buscar_municipioFueraDelValle_devuelveVacio() {
        assertTrue(Municipio.buscar("Bogotá").isEmpty());
    }

    @Test
    void buscar_nulo_devuelveVacio() {
        assertTrue(Municipio.buscar(null).isEmpty());
    }

    @Test
    void desdeNombre_municipioValido_devuelveElMunicipio() {
        assertEquals(Municipio.DAGUA, Municipio.desdeNombre("Dagua"));
    }

    @Test
    void desdeNombre_municipioInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Municipio.desdeNombre("Medellín"));
    }

    @Test
    void getNombre_devuelveElNombreOficialConTildes() {
        assertEquals("Jamundí", Municipio.JAMUNDI.getNombre());
    }
}
