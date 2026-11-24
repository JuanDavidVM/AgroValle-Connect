package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.CompradorComercialRequest;
import com.agrovalle.connect.dto.CompradorComercialResponse;
import com.agrovalle.connect.model.CompradorComercial;

/**
 * Casos de uso sobre compradores comerciales.
 */
public interface CompradorComercialService {

    /**
     * Registra un comprador comercial junto con su usuario de acceso.
     *
     * @param request datos del comprador
     * @return el comprador registrado
     */
    CompradorComercialResponse registrar(CompradorComercialRequest request);

    /**
     * Resuelve el comprador comercial asociado a la cuenta autenticada.
     *
     * @param correo correo del usuario autenticado (subject del JWT)
     * @return el comprador comercial dueño de esa cuenta
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si la cuenta no tiene
     *         perfil de comprador comercial
     */
    CompradorComercial obtenerPorCorreo(String correo);
}
