package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.OrdenResponse;
import java.util.List;

/**
 * Casos de uso sobre órdenes de compra directa.
 */
public interface OrdenService {

    /**
     * Emite las órdenes correspondientes al carrito del comprador autenticado.
     *
     * <p>Genera una orden por agricultor, consume las reservas que respaldan cada renglón y vacía el
     * carrito. Todo ocurre en una sola transacción: si una sola oferta no tiene stock reservado
     * suficiente, no se emite ninguna orden y el carrito queda intacto.</p>
     *
     * @param correoComprador correo del usuario autenticado (subject del JWT)
     * @return una orden por cada agricultor presente en el carrito
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si el usuario autenticado
     *         no tiene perfil de comprador
     * @throws com.agrovalle.connect.exception.ConflictoException si el carrito está vacío o alguna
     *         oferta no tiene stock reservado suficiente
     */
    List<OrdenResponse> emitir(String correoComprador);
}
