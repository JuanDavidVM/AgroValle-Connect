package com.agrovalle.connect.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP con el formato {@link ApiError}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Responde 400 cuando falla la validación de un DTO ({@code @Valid}).
     *
     * @param ex      excepción con los errores de campo
     * @param request solicitud original
     * @return 400 con el detalle por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of(
                HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos", request.getRequestURI(), errores));
    }

    /**
     * Responde 400 cuando el cuerpo falta o no es un JSON legible.
     *
     * @param request solicitud original
     * @return 400 con un mensaje descriptivo
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> cuerpoIlegible(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud falta o no es un JSON válido",
                request.getRequestURI()));
    }

    /**
     * Responde 409 ante un conflicto de negocio (por ejemplo, un registro duplicado).
     *
     * @param ex      excepción de conflicto
     * @param request solicitud original
     * @return 409 con el mensaje de la excepción
     */
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ApiError> conflicto(ConflictoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(
                HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Responde 404 cuando el recurso solicitado no existe.
     *
     * @param ex      excepción de recurso no encontrado
     * @param request solicitud original
     * @return 404 con el mensaje de la excepción
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Responde 409 cuando la base de datos rechaza el registro por una restricción de integridad.
     *
     * @param request solicitud original
     * @return 409 con un mensaje descriptivo
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integridad(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(
                HttpStatus.CONFLICT,
                "El registro ya existe o viola una restricción de integridad",
                request.getRequestURI()));
    }

    /**
     * Responde 401 cuando las credenciales no son válidas.
     *
     * @param request solicitud original
     * @return 401 sin revelar qué campo falló
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> credencialesIncorrectas(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError.of(
                HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", request.getRequestURI()));
    }
}
