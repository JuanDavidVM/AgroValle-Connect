package com.agrovalle.connect.notificacion;

import com.agrovalle.connect.model.PerfilUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación inicial del canal de notificación: deja el mensaje en el registro de la aplicación.
 *
 * <p>Sustituye a un proveedor real de SMS o WhatsApp mientras no esté contratado. No escribe datos de
 * contacto en el registro.</p>
 */
@Component
public class NotificadorLog implements Notificador {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificadorLog.class);

    @Override
    public void notificar(PerfilUsuario destinatario, String mensaje) {
        LOGGER.info("Notificación para {}: {}", destinatario.getNombreVisible(), mensaje);
    }
}
