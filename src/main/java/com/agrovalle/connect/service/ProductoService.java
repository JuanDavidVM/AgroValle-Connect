package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.dto.ProductoResponse;

/**
 * Casos de uso sobre productos (lotes de cosecha).
 */
public interface ProductoService {

    /**
     * Publica un lote de cosecha del agricultor autenticado y emite el evento de cosecha publicada.
     *
     * @param correoAgricultor correo del usuario autenticado (subject del JWT)
     * @param request          datos del lote
     * @return el producto publicado
     * @throws com.agrovalle.connect.exception.RecursoNoEncontradoException si la finca no existe o no
     *         pertenece al agricultor
     */
    ProductoResponse publicar(String correoAgricultor, ProductoRequest request);
}
