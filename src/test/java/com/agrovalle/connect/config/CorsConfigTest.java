package com.agrovalle.connect.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * AV-020: la API no declaraba CORS, así que cualquier frontend servido desde
 * otro origen fallaba en el preflight.
 *
 * <p>La prueba interroga directamente a la fuente de configuración, sin levantar
 * el contexto de Spring: lo que se verifica es el contrato de qué orígenes,
 * métodos y encabezados se aceptan, que es donde vive la decisión de seguridad.
 */
class CorsConfigTest {

    private static final String ORIGEN_AUTORIZADO = "http://localhost:5173";
    private static final String ORIGEN_DESCONOCIDO = "https://sitio-no-autorizado.example";
    private static final String RUTA_API = "/api/v1/productos";

    private CorsConfigurationSource fuente;

    @BeforeEach
    void prepararFuente() {
        fuente = new SecurityConfig().corsConfigurationSource(List.of(ORIGEN_AUTORIZADO));
    }

    private CorsConfiguration configuracionPara(String ruta) {
        return fuente.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", ruta));
    }

    @Test
    void lasRutasDeLaApiDeclaranConfiguracionCors() {
        assertNotNull(configuracionPara(RUTA_API),
                "La API debe declarar CORS o ningún navegador externo podrá consumirla.");
    }

    @Test
    void elOrigenDeclaradoEsAceptado() {
        assertEquals(ORIGEN_AUTORIZADO, configuracionPara(RUTA_API).checkOrigin(ORIGEN_AUTORIZADO));
    }

    @Test
    void unOrigenNoDeclaradoEsRechazado() {
        assertNull(configuracionPara(RUTA_API).checkOrigin(ORIGEN_DESCONOCIDO),
                "Solo los orígenes configurados pueden consumir la API.");
    }

    @Test
    void seAceptaElEncabezadoAuthorization() {
        List<String> aceptados = configuracionPara(RUTA_API).checkHeaders(List.of("Authorization"));
        assertNotNull(aceptados, "Sin este encabezado el frontend no podría enviar el token JWT.");
        assertTrue(aceptados.contains("Authorization"));
    }

    @Test
    void seAceptanLosMetodosDeEscritura() {
        List<HttpMethod> aceptados = configuracionPara(RUTA_API).checkHttpMethod(HttpMethod.POST);
        assertNotNull(aceptados);
        assertTrue(aceptados.contains(HttpMethod.POST));
    }

    @Test
    void lasRutasFueraDeLaApiNoDeclaranCors() {
        assertNull(configuracionPara("/swagger-ui.html"),
                "El alcance de CORS se limita a /api/** de forma deliberada.");
    }
}
