package com.agrovalle.connect.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextoNormalizadoTest {

    @Test
    void de_quitaTildesYPasaAMinusculas() {
        assertEquals("tulua", TextoNormalizado.de("Tuluá"));
    }

    @Test
    void de_colapsaEspaciosYRecortaLosExtremos() {
        assertEquals("el aguila", TextoNormalizado.de("  EL    Águila "));
    }

    @Test
    void de_textoYaNormalizado_noCambia() {
        assertEquals("dagua", TextoNormalizado.de("dagua"));
    }
}
