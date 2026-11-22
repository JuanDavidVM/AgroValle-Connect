package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.AgricultorRequest;
import com.agrovalle.connect.dto.AgricultorResponse;
import com.agrovalle.connect.model.Agricultor;

/**
 * Casos de uso sobre agricultores.
 */
public interface AgricultorService {

    /**
     * Registra un agricultor junto con su usuario de acceso.
     *
     * @param request datos del agricultor
     * @return el agricultor registrado
     */
    AgricultorResponse registrar(AgricultorRequest request);

    /**
     * Obtiene el agricultor asociado a una cuenta de acceso.
     *
     * @param correo correo del usuario autenticado
     * @return el agricultor
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si el usuario no tiene perfil
     *         de agricultor
     */
    Agricultor obtenerPorCorreo(String correo);
}
