package com.agrovalle.connect.listener;

import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.model.RegistroAuditoria;
import com.agrovalle.connect.repository.RegistroAuditoriaRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Suscriptor que deja constancia en la base de datos de cada cosecha publicada.
 *
 * <p>Se ejecuta dentro de la misma transacción de la publicación: si la auditoría falla, la cosecha
 * tampoco se publica, de modo que nunca queda una publicación sin rastro.</p>
 */
@Component
public class AuditoriaCosechaListener {

    static final String ACCION = "COSECHA_PUBLICADA";
    static final String ENTIDAD = "Producto";

    private final RegistroAuditoriaRepository registroRepository;

    public AuditoriaCosechaListener(RegistroAuditoriaRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    /**
     * Registra la publicación de la cosecha.
     *
     * @param evento evento de cosecha publicada
     */
    @EventListener
    public void registrar(CosechaPublicadaEvent evento) {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setAccion(ACCION);
        registro.setEntidad(ENTIDAD);
        registro.setEntidadId(evento.productoId());
        registro.setUsuario(evento.correoAgricultor());
        registro.setDetalle("Cosecha de " + evento.tipo() + ": " + evento.cantidadKg().toPlainString()
                + " kg en " + evento.municipio());
        registro.setFecha(evento.ocurridoEn());
        registroRepository.save(registro);
    }
}
