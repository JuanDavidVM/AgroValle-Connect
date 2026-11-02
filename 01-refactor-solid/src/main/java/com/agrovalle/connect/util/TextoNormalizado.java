package com.agrovalle.connect.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Normaliza texto para compararlo sin distinguir mayúsculas, tildes ni espacios sobrantes.
 */
public final class TextoNormalizado {

    private TextoNormalizado() {
        // Clase de utilidades: no se instancia.
    }

    /**
     * Devuelve el texto en minúsculas, sin tildes y con los espacios colapsados.
     *
     * @param texto texto a normalizar (no nulo)
     * @return el texto normalizado, por ejemplo "  El   Águila " produce "el aguila"
     */
    public static String de(String texto) {
        String sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
