package com.agrovalle.connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.FincaRequest;
import com.agrovalle.connect.dto.FincaResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.repository.AgricultorRepository;
import com.agrovalle.connect.repository.FincaRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FincaServiceTest {

    private static final String CORREO = "maria@correo.com";

    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private AgricultorRepository agricultorRepository;
    @InjectMocks
    private FincaService fincaService;

    private Agricultor agricultor() {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        return agricultor;
    }

    private FincaRequest request(String nombre, String municipio) {
        return new FincaRequest(nombre, municipio, new BigDecimal("12.5"));
    }

    @Test
    void registrar_datosValidos_persisteYAsociaAlAgricultor() {
        Agricultor agricultor = agricultor();
        when(agricultorRepository.findByUsuarioCorreo(CORREO)).thenReturn(Optional.of(agricultor));
        when(fincaRepository.existsByAgricultorIdAndNombreIgnoreCase(7L, "La Esperanza")).thenReturn(false);
        when(fincaRepository.save(any(Finca.class))).thenAnswer(inv -> inv.getArgument(0));

        FincaResponse respuesta = fincaService.registrar(CORREO, request("  La Esperanza ", "dagua"));

        assertEquals("La Esperanza", respuesta.nombreFinca());
        assertEquals("Dagua", respuesta.municipio());
        assertEquals(Long.valueOf(7L), respuesta.agricultorId());
        ArgumentCaptor<Finca> captura = ArgumentCaptor.forClass(Finca.class);
        verify(fincaRepository).save(captura.capture());
        assertEquals(Municipio.DAGUA, captura.getValue().getMunicipio());
        assertSame(agricultor, captura.getValue().getAgricultor());
    }

    @Test
    void registrar_nombreDuplicadoDelMismoAgricultor_lanzaConflicto() {
        when(agricultorRepository.findByUsuarioCorreo(CORREO)).thenReturn(Optional.of(agricultor()));
        when(fincaRepository.existsByAgricultorIdAndNombreIgnoreCase(7L, "La Esperanza")).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> fincaService.registrar(CORREO, request("La Esperanza", "Dagua")));

        verify(fincaRepository, never()).save(any());
    }

    @Test
    void registrar_usuarioSinPerfilDeAgricultor_lanzaNoEncontrado() {
        when(agricultorRepository.findByUsuarioCorreo(CORREO)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> fincaService.registrar(CORREO, request("La Esperanza", "Dagua")));

        verifyNoInteractions(fincaRepository);
    }
}
