package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.FincaRequest;
import com.agrovalle.connect.dto.FincaResponse;

/**
 * Casos de uso sobre fincas.
 */
public interface FincaService {

    /**
     * Registra una finca asociada al agricultor autenticado.
     *
     * @param correoAgricultor correo del usuario autenticado (subject del JWT)
     * @param request          datos de la finca
     * @return la finca registrada
     */
    FincaResponse registrar(String correoAgricultor, FincaRequest request);
}
