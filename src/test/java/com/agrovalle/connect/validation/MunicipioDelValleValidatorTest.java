package com.agrovalle.connect.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.service.CatalogoService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MunicipioDelValleValidatorTest {

    @Mock
    private CatalogoService catalogoService;
    @InjectMocks
    private MunicipioDelValleValidator validador;

    @Test
    void isValid_valorDelCatalogo_esValido() {
        when(catalogoService.buscarMunicipio("Palmira")).thenReturn(Optional.of(new Municipio()));

        assertTrue(validador.isValid("Palmira", null));
    }

    @Test
    void isValid_valorFueraDelCatalogo_noEsValido() {
        when(catalogoService.buscarMunicipio("Narnia")).thenReturn(Optional.empty());

        assertFalse(validador.isValid("Narnia", null));
    }

    @Test
    void isValid_nuloOVacio_seDejaAlNotBlankSinConsultarElCatalogo() {
        assertTrue(validador.isValid(null, null));
        assertTrue(validador.isValid("   ", null));

        verifyNoInteractions(catalogoService);
    }
}
