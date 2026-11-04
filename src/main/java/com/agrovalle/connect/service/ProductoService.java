package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.dto.ProductoResponse;
import java.util.List;

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

    /**
     * Lista las ofertas activas, opcionalmente filtradas por municipio de origen y por categoría.
     *
     * @param municipio nombre del municipio (nulo o vacío para no filtrar)
     * @param categoria nombre de la categoría (nulo o vacío para no filtrar)
     * @return las ofertas activas que coinciden, de la más reciente a la más antigua; vacío si ninguna coincide
     * @throws com.agrovalle.connect.exception.SolicitudInvalidaException si el municipio o la categoría no
     *         pertenecen al catálogo
     */
    List<ProductoResponse> buscarOfertas(String municipio, String categoria);
}
