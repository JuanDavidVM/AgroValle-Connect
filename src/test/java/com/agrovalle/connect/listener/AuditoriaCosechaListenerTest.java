package com.agrovalle.connect.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.model.RegistroAuditoria;
import com.agrovalle.connect.repository.RegistroAuditoriaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditoriaCosechaListenerTest {

    @Mock
    private RegistroAuditoriaRepository registroRepository;
    @InjectMocks
    private AuditoriaCosechaListener listener;

    @Test
    void registrar_cosechaPublicada_guardaElRegistroDeAuditoria() {
        LocalDateTime momento = LocalDateTime.of(2026, 11, 1, 10, 30);
        CosechaPublicadaEvent evento = new CosechaPublicadaEvent(99L, 7L, "maria@correo.com",
                "Plátano hartón", new BigDecimal("150.50"), "Dagua", momento);

        listener.registrar(evento);

        ArgumentCaptor<RegistroAuditoria> captura = ArgumentCaptor.forClass(RegistroAuditoria.class);
        verify(registroRepository).save(captura.capture());
        RegistroAuditoria registro = captura.getValue();
        assertEquals("COSECHA_PUBLICADA", registro.getAccion());
        assertEquals("Producto", registro.getEntidad());
        assertEquals(Long.valueOf(99L), registro.getEntidadId());
        assertEquals("maria@correo.com", registro.getUsuario());
        assertEquals(momento, registro.getFecha());
        assertTrue(registro.getDetalle().contains("Plátano hartón"));
        assertTrue(registro.getDetalle().contains("150.50"));
        assertTrue(registro.getDetalle().contains("Dagua"));
    }
}
