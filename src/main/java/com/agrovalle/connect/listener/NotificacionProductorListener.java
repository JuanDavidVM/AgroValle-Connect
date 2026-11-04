package com.agrovalle.connect.listener;

import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.notificacion.Notificador;
import com.agrovalle.connect.repository.AgricultorRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Suscriptor que avisa al agricultor de que su cosecha quedó publicada.
 *
 * <p>Se ejecuta solo después de confirmar la transacción: si la publicación se revierte, no se envía
 * ninguna notificación.</p>
 */
@Component
public class NotificacionProductorListener {

    private final AgricultorRepository agricultorRepository;
    private final Notificador notificador;

    public NotificacionProductorListener(AgricultorRepository agricultorRepository, Notificador notificador) {
        this.agricultorRepository = agricultorRepository;
        this.notificador = notificador;
    }

    /**
     * Notifica al agricultor la publicación de su cosecha.
     *
     * @param evento evento de cosecha publicada
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notificar(CosechaPublicadaEvent evento) {
        agricultorRepository.findById(evento.agricultorId())
                .ifPresent(agricultor -> notificador.notificar(agricultor, mensaje(evento)));
    }

    private String mensaje(CosechaPublicadaEvent evento) {
        return "Tu cosecha de " + evento.tipo() + " (" + evento.cantidadKg().toPlainString()
                + " kg) quedó publicada para los compradores de " + evento.municipio() + ".";
    }
}
