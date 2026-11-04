package com.agrovalle.connect.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MunicipioDelValleValidatorTest {

    private final MunicipioDelValleValidator validador = new MunicipioDelValleValidator();

    @Test
    void isValid_municipioDelCatalogo_esValido() {
        assertTrue(validador.isValid("Palmira", null));
    }

    @Test
    void isValid_municipioFueraDelCatalogo_noEsValido() {
        assertFalse(validador.isValid("Narnia", null));
    }

    @Test
    void isValid_nuloOVacio_seDejaAlNotBlank() {
        assertTrue(validador.isValid(null, null));
        assertTrue(validador.isValid("   ", null));
    }
}
