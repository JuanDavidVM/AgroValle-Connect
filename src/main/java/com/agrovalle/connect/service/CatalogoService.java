package com.agrovalle.connect.service;

import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.Municipio;
import java.util.Optional;

/**
 * Consulta de los catálogos del sistema (municipios del Valle y categorías de producto).
 *
 * <p>La búsqueda por nombre ignora mayúsculas, tildes y espacios sobrantes.</p>
 */
public interface CatalogoService {

    /**
     * Busca un municipio por su nombre.
     *
     * @param nombre nombre escrito por el usuario (puede ser nulo)
     * @return el municipio, o vacío si no pertenece al catálogo
     */
    Optional<Municipio> buscarMunicipio(String nombre);

    /**
     * Obtiene un municipio por su nombre.
     *
     * @param nombre nombre del municipio
     * @return el municipio del catálogo
     * @throws com.agrovalle.connect.exception.SolicitudInvalidaException si no pertenece al Valle del Cauca
     */
    Municipio obtenerMunicipio(String nombre);

    /**
     * Busca una categoría por su nombre.
     *
     * @param nombre nombre escrito por el usuario (puede ser nulo)
     * @return la categoría, o vacío si no pertenece al catálogo
     */
    Optional<Categoria> buscarCategoria(String nombre);

    /**
     * Obtiene una categoría por su nombre.
     *
     * @param nombre nombre de la categoría
     * @return la categoría del catálogo
     * @throws com.agrovalle.connect.exception.SolicitudInvalidaException si no pertenece al catálogo
     */
    Categoria obtenerCategoria(String nombre);
}
