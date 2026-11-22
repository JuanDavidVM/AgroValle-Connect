package com.agrovalle.connect.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AV-021: la documentación OpenAPI estaba en permitAll sin condición de entorno.
 * Esta prueba verifica que el interruptor realmente la cierra, levantando el
 * contexto con app.swagger.publico en falso.
 */
@SpringBootTest(properties = "app.swagger.publico=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerRestringidoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void conSwaggerCerrado_laEspecificacionExigeAutenticacion() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conSwaggerCerrado_losEndpointsPublicosSiguenAbiertos() throws Exception {
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk());
    }
}
