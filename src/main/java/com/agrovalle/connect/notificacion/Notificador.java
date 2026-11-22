package com.agrovalle.connect.notificacion;

import com.agrovalle.connect.model.PerfilUsuario;

/**
 * Canal de notificación hacia los usuarios de la plataforma.
 *
 * <p>Recibe cualquier {@link PerfilUsuario} (agricultor o comprador). Agregar un canal nuevo, como
 * WhatsApp o SMS, es crear otra implementación sin modificar a quienes notifican.</p>
 */
public interface Notificador {

    /**
     * Envía un mensaje al titular del perfil.
     *
     * @param destinatario perfil que recibe el mensaje
     * @param mensaje      texto a enviar
     */
    void notificar(PerfilUsuario destinatario, String mensaje);
}
