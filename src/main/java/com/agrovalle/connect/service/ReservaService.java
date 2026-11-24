package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.ReservaRequest;
import com.agrovalle.connect.dto.ReservaResponse;

/**
 * Casos de uso sobre reservas de inventario.
 */
public interface ReservaService {

    /**
     * Aparta una cantidad de una oferta activa y la descuenta del stock disponible.
     *
     * <p>La operación es atómica: o se descuenta el stock y se crea la reserva, o no ocurre ninguna
     * de las dos cosas.</p>
     *
     * @param correoComprador correo del usuario autenticado (subject del JWT)
     * @param request         oferta y cantidad a reservar
     * @return la reserva creada, con el stock que queda en la oferta
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si la oferta no existe o el
     *         usuario autenticado no tiene perfil de comprador
     * @throws com.agrovalle.connect.exception.ConflictoException si la oferta no está activa o la
     *         cantidad solicitada supera el stock disponible
     */
    ReservaResponse reservar(String correoComprador, ReservaRequest request);
}
