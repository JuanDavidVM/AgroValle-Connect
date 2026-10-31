package com.agrovalle.connect.model;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Catálogo cerrado de los 42 municipios del Valle del Cauca.
 *
 * <p>La búsqueda por nombre ignora mayúsculas, tildes y espacios sobrantes, de modo que
 * "Tuluá", "tulua" y " TULUA " resuelven al mismo municipio.</p>
 */
public enum Municipio {

    ALCALA("Alcalá"),
    ANDALUCIA("Andalucía"),
    ANSERMANUEVO("Ansermanuevo"),
    ARGELIA("Argelia"),
    BOLIVAR("Bolívar"),
    BUENAVENTURA("Buenaventura"),
    BUGA("Buga"),
    BUGALAGRANDE("Bugalagrande"),
    CAICEDONIA("Caicedonia"),
    CALI("Cali"),
    CALIMA("Calima"),
    CANDELARIA("Candelaria"),
    CARTAGO("Cartago"),
    DAGUA("Dagua"),
    EL_AGUILA("El Águila"),
    EL_CAIRO("El Cairo"),
    EL_CERRITO("El Cerrito"),
    EL_DOVIO("El Dovio"),
    FLORIDA("Florida"),
    GINEBRA("Ginebra"),
    GUACARI("Guacarí"),
    JAMUNDI("Jamundí"),
    LA_CUMBRE("La Cumbre"),
    LA_UNION("La Unión"),
    LA_VICTORIA("La Victoria"),
    OBANDO("Obando"),
    PALMIRA("Palmira"),
    PRADERA("Pradera"),
    RESTREPO("Restrepo"),
    RIOFRIO("Riofrío"),
    ROLDANILLO("Roldanillo"),
    SAN_PEDRO("San Pedro"),
    SEVILLA("Sevilla"),
    TORO("Toro"),
    TRUJILLO("Trujillo"),
    TULUA("Tuluá"),
    ULLOA("Ulloa"),
    VERSALLES("Versalles"),
    VIJES("Vijes"),
    YOTOCO("Yotoco"),
    YUMBO("Yumbo"),
    ZARZAL("Zarzal");

    private static final Map<String, Municipio> POR_NOMBRE = Arrays.stream(values())
            .collect(Collectors.toMap(m -> normalizar(m.nombre), Function.identity()));

    private final String nombre;

    Municipio(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Nombre oficial del municipio, con tildes.
     *
     * @return el nombre para mostrar
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Busca un municipio por su nombre, sin distinguir mayúsculas ni tildes.
     *
     * @param texto nombre escrito por el usuario (puede ser nulo)
     * @return el municipio, o vacío si no pertenece al catálogo
     */
    public static Optional<Municipio> buscar(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(POR_NOMBRE.get(normalizar(texto)));
    }

    /**
     * Igual que {@link #buscar(String)}, pero falla si el municipio no está en el catálogo.
     *
     * @param texto nombre del municipio
     * @return el municipio del catálogo
     * @throws IllegalArgumentException si el nombre no pertenece al Valle del Cauca
     */
    public static Municipio desdeNombre(String texto) {
        return buscar(texto).orElseThrow(
                () -> new IllegalArgumentException("El municipio no pertenece al Valle del Cauca: " + texto));
    }

    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
