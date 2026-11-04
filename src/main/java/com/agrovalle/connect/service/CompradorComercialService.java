package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.CompradorComercialRequest;
import com.agrovalle.connect.dto.CompradorComercialResponse;

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
}
