package com.agrovalle.connect.model;

import com.agrovalle.connect.util.TextoNormalizado;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Catálogo cerrado de categorías de productos agrícolas.
 *
 * <p>La búsqueda por nombre ignora mayúsculas, tildes y espacios sobrantes.</p>
 */
public enum Categoria {

    FRUTAS("Frutas"),
    VERDURAS("Verduras"),
    TUBERCULOS("Tubérculos"),
    GRANOS("Granos"),
    CAFE("Café"),
    HIERBAS("Hierbas");

    private static final Map<String, Categoria> POR_NOMBRE = Arrays.stream(values())
            .collect(Collectors.toMap(c -> TextoNormalizado.de(c.nombre), Function.identity()));

    private final String nombre;

    Categoria(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Nombre oficial de la categoría, con tildes.
     *
     * @return el nombre para mostrar
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Busca una categoría por su nombre, sin distinguir mayúsculas ni tildes.
     *
     * @param texto nombre escrito por el usuario (puede ser nulo)
     * @return la categoría, o vacío si no pertenece al catálogo
     */
    public static Optional<Categoria> buscar(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(POR_NOMBRE.get(TextoNormalizado.de(texto)));
    }

    /**
     * Igual que {@link #buscar(String)}, pero falla si la categoría no está en el catálogo.
     *
     * @param texto nombre de la categoría
     * @return la categoría del catálogo
     * @throws IllegalArgumentException si el nombre no pertenece al catálogo
     */
    public static Categoria desdeNombre(String texto) {
        return buscar(texto).orElseThrow(
                () -> new IllegalArgumentException("La categoría no pertenece al catálogo: " + texto));
    }
}
