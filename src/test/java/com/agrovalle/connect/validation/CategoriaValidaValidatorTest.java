package com.agrovalle.connect.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CategoriaValidaValidatorTest {

    private final CategoriaValidaValidator validador = new CategoriaValidaValidator();

    @Test
    void isValid_categoriaDelCatalogo_esValida() {
        assertTrue(validador.isValid("Frutas", null));
    }

    @Test
    void isValid_categoriaFueraDelCatalogo_noEsValida() {
        assertFalse(validador.isValid("Juguetes", null));
    }

    @Test
    void isValid_nuloOVacio_seDejaAlNotBlank() {
        assertTrue(validador.isValid(null, null));
        assertTrue(validador.isValid("  ", null));
    }
}
