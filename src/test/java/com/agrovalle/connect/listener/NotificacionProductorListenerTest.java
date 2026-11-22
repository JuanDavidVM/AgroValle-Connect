package com.agrovalle.connect.listener;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.notificacion.Notificador;
import com.agrovalle.connect.repository.AgricultorRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificacionProductorListenerTest {

    @Mock
    private AgricultorRepository agricultorRepository;
    @Mock
    private Notificador notificador;
    @InjectMocks
    private NotificacionProductorListener listener;

    private CosechaPublicadaEvent evento() {
        return new CosechaPublicadaEvent(99L, 7L, "maria@correo.com", "Plátano hartón",
                new BigDecimal("150.50"), "Dagua", LocalDateTime.of(2026, 11, 1, 10, 30));
    }

    @Test
    void notificar_agricultorExistente_enviaElMensajeDeCosechaPublicada() {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("María Gómez");
        when(agricultorRepository.findById(7L)).thenReturn(Optional.of(agricultor));

        listener.notificar(evento());

        ArgumentCaptor<String> mensaje = ArgumentCaptor.forClass(String.class);
        verify(notificador).notificar(eq(agricultor), mensaje.capture());
        assertTrue(mensaje.getValue().contains("Plátano hartón"));
        assertTrue(mensaje.getValue().contains("150.50"));
        assertTrue(mensaje.getValue().contains("Dagua"));
    }

    @Test
    void notificar_agricultorInexistente_noEnviaNada() {
        when(agricultorRepository.findById(7L)).thenReturn(Optional.empty());

        listener.notificar(evento());

        verifyNoInteractions(notificador);
    }
}
