package com.agrovalle.connect.notificacion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.CompradorComercial;
import org.junit.jupiter.api.Test;

class NotificadorLogTest {

    private final Notificador notificador = new NotificadorLog();

    @Test
    void notificar_aUnAgricultor_noFalla() {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("María Gómez");

        assertDoesNotThrow(() -> notificador.notificar(agricultor, "Cosecha publicada"));
    }

    @Test
    void notificar_aUnComprador_noFalla() {
        CompradorComercial comprador = new CompradorComercial();
        comprador.setRazonSocial("Supermercados del Valle S.A.S.");

        assertDoesNotThrow(() -> notificador.notificar(comprador, "Nueva oferta disponible"));
    }
}
