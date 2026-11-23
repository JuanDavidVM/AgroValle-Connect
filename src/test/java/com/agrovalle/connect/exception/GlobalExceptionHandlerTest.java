package com.agrovalle.connect.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private static final String RUTA = "/api/v1/agricultores";
    private static final String OBJETO = "agricultorRequest";

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest solicitud() {
        return new MockHttpServletRequest("POST", RUTA);
    }

    private ApiError cuerpoDe(ResponseEntity<ApiError> respuesta, HttpStatus esperado) {
        assertEquals(esperado, respuesta.getStatusCode());
        ApiError cuerpo = respuesta.getBody();
        assertNotNull(cuerpo);
        assertEquals(esperado.value(), cuerpo.status());
        assertEquals(esperado.getReasonPhrase(), cuerpo.error());
        assertEquals(RUTA, cuerpo.path());
        assertNotNull(cuerpo.timestamp());
        return cuerpo;
    }

    private MethodArgumentNotValidException errorDeValidacion(FieldError... errores) throws Exception {
        Method metodo = String.class.getDeclaredMethod("concat", String.class);
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), OBJETO);
        for (FieldError error : errores) {
            binding.addError(error);
        }
        return new MethodArgumentNotValidException(new MethodParameter(metodo, 0), binding);
    }

    @Test
    void validacion_conErroresDeCampo_devuelve400ConElDetallePorCampo() throws Exception {
        MethodArgumentNotValidException ex = errorDeValidacion(
                new FieldError(OBJETO, "documento", "El documento es obligatorio"),
                new FieldError(OBJETO, "correo", "El correo no tiene un formato válido"));

        ApiError cuerpo = cuerpoDe(handler.validacion(ex, solicitud()), HttpStatus.BAD_REQUEST);

        assertEquals("Los datos enviados no son válidos", cuerpo.message());
        assertNotNull(cuerpo.errores());
        assertEquals(2, cuerpo.errores().size());
        assertEquals("El documento es obligatorio", cuerpo.errores().get("documento"));
        assertEquals("El correo no tiene un formato válido", cuerpo.errores().get("correo"));
    }

    @Test
    void validacion_conVariosErroresDelMismoCampo_conservaElPrimerMensaje() throws Exception {
        MethodArgumentNotValidException ex = errorDeValidacion(
                new FieldError(OBJETO, "documento", "El documento es obligatorio"),
                new FieldError(OBJETO, "documento", "El documento debe ser numérico"));

        ApiError cuerpo = cuerpoDe(handler.validacion(ex, solicitud()), HttpStatus.BAD_REQUEST);

        assertEquals(1, cuerpo.errores().size());
        assertEquals("El documento es obligatorio", cuerpo.errores().get("documento"));
    }

    @Test
    void cuerpoIlegible_devuelve400SinDetallePorCampo() {
        ApiError cuerpo = cuerpoDe(handler.cuerpoIlegible(solicitud()), HttpStatus.BAD_REQUEST);

        assertEquals("El cuerpo de la solicitud falta o no es un JSON válido", cuerpo.message());
        assertNull(cuerpo.errores());
    }

    @Test
    void conflicto_devuelve409ConElMensajeDeLaExcepcion() {
        ConflictoException ex = new ConflictoException("El documento ya está registrado");

        ApiError cuerpo = cuerpoDe(handler.conflicto(ex, solicitud()), HttpStatus.CONFLICT);

        assertEquals("El documento ya está registrado", cuerpo.message());
        assertNull(cuerpo.errores());
    }

    @Test
    void solicitudInvalida_devuelve400ConElMensajeDeLaExcepcion() {
        SolicitudInvalidaException ex = new SolicitudInvalidaException("El municipio no existe en el catálogo");

        ApiError cuerpo = cuerpoDe(handler.solicitudInvalida(ex, solicitud()), HttpStatus.BAD_REQUEST);

        assertEquals("El municipio no existe en el catálogo", cuerpo.message());
    }

    @Test
    void noEncontrado_devuelve404ConElMensajeDeLaExcepcion() {
        RecursoNoEncontradoException ex = new RecursoNoEncontradoException("La finca no existe");

        ApiError cuerpo = cuerpoDe(handler.noEncontrado(ex, solicitud()), HttpStatus.NOT_FOUND);

        assertEquals("La finca no existe", cuerpo.message());
    }

    @Test
    void integridad_devuelve409ConMensajeGenerico() {
        ApiError cuerpo = cuerpoDe(handler.integridad(solicitud()), HttpStatus.CONFLICT);

        assertEquals("El registro ya existe o viola una restricción de integridad", cuerpo.message());
        assertNull(cuerpo.errores());
    }

    @Test
    void credencialesIncorrectas_devuelve401SinRevelarElCampoQueFallo() {
        ApiError cuerpo = cuerpoDe(handler.credencialesIncorrectas(solicitud()), HttpStatus.UNAUTHORIZED);

        assertEquals("Credenciales incorrectas", cuerpo.message());
        assertNull(cuerpo.errores());
    }

    @Test
    void lasExcepcionesDeDominioConservanSuMensaje() {
        assertEquals("duplicado", new ConflictoException("duplicado").getMessage());
        assertEquals("invalida", new SolicitudInvalidaException("invalida").getMessage());
        assertEquals("ausente", new RecursoNoEncontradoException("ausente").getMessage());
    }
}
