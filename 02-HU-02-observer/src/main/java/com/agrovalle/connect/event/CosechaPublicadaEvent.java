package com.agrovalle.connect.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de dominio que se emite cuando un agricultor publica una cosecha.
 *
 * <p>Es el "sujeto" del patrón Observer: quien lo publica no conoce a los suscriptores. Transporta datos
 * simples (no entidades) para que los suscriptores no dependan de la sesión de persistencia.</p>
 *
 * @param productoId       identificador del producto publicado
 * @param agricultorId     identificador del agricultor que publica
 * @param correoAgricultor correo del agricultor (usuario autenticado)
 * @param tipo             tipo de producto
 * @param cantidadKg       cantidad publicada, en kilogramos
 * @param municipio        municipio de la finca de origen
 * @param ocurridoEn       momento de la publicación
 */
public record CosechaPublicadaEvent(
        Long productoId,
        Long agricultorId,
        String correoAgricultor,
        String tipo,
        BigDecimal cantidadKg,
        String municipio,
        LocalDateTime ocurridoEn) {
}
