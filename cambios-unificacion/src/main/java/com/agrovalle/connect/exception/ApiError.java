package com.agrovalle.connect.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Cuerpo JSON uniforme para todas las respuestas de error de la API.
 *
 * @param timestamp momento en que se produjo el error
 * @param status    código de estado HTTP
 * @param error     descripción estándar del código de estado
 * @param message   mensaje legible para el cliente
 * @param path      ruta solicitada
 * @param errores   detalle de validación por campo (puede ser nulo)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errores) {

    /**
     * Crea un error sin detalle por campo.
     *
     * @param status  estado HTTP de la respuesta
     * @param message mensaje para el cliente
     * @param path    ruta solicitada
     * @return el error listo para serializar
     */
    public static ApiError of(HttpStatus status, String message, String path) {
        return of(status, message, path, null);
    }

    /**
     * Crea un error con el detalle de validación por campo.
     *
     * @param status  estado HTTP de la respuesta
     * @param message mensaje para el cliente
     * @param path    ruta solicitada
     * @param errores errores de validación indexados por campo
     * @return el error listo para serializar
     */
    public static ApiError of(HttpStatus status, String message, String path, Map<String, String> errores) {
        return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, errores);
    }
}
