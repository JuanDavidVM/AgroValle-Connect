package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.CarritoResponse;
import com.agrovalle.connect.dto.ItemCarritoRequest;
import com.agrovalle.connect.dto.ItemCarritoResponse;

/**
 * Casos de uso sobre el carrito de compra del comprador autenticado.
 */
public interface CarritoService {

    /**
     * Agrega una cantidad de una oferta al carrito del comprador.
     *
     * <p>Si la oferta ya está en el carrito, las cantidades se acumulan en el mismo renglón.</p>
     *
     * @param correoComprador correo del usuario autenticado (subject del JWT)
     * @param request         oferta y cantidad a agregar
     * @return el renglón del carrito, con su subtotal
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si la oferta no existe o el
     *         usuario autenticado no tiene perfil de comprador
     * @throws com.agrovalle.connect.exception.ConflictoException si la oferta no está activa o la
     *         cantidad acumulada supera el stock disponible
     */
    ItemCarritoResponse agregarItem(String correoComprador, ItemCarritoRequest request);

    /**
     * Devuelve el carrito del comprador con el subtotal por agricultor y el total general.
     *
     * @param correoComprador correo del usuario autenticado (subject del JWT)
     * @return el carrito; con listas vacías y total en cero si no tiene renglones
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si el usuario autenticado
     *         no tiene perfil de comprador
     */
    CarritoResponse obtener(String correoComprador);
}
