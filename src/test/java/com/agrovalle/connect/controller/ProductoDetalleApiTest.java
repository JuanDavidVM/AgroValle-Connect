package com.agrovalle.connect.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * HU-09: detalle de una oferta publicada. La consulta es pública y no requiere token.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductoDetalleApiTest {

    private static final String CORREO = "maria@correo.com";
    private static final String CLAVE = "Clave12345";
    private static final LocalDate COSECHA = LocalDate.now().plusDays(5);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private long productoId;

    @BeforeEach
    void datosDePrueba() throws Exception {
        Map<String, Object> agricultor = new HashMap<>();
        agricultor.put("nombreCompleto", "María Gómez");
        agricultor.put("documento", "1144123456");
        agricultor.put("correo", CORREO);
        agricultor.put("telefono", "3001234567");
        agricultor.put("municipio", "Palmira");
        agricultor.put("contrasena", CLAVE);
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agricultor)))
                .andExpect(status().isCreated());

        Map<String, Object> login = new HashMap<>();
        login.put("correo", CORREO);
        login.put("contrasena", CLAVE);
        String sesion = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(sesion).get("token").asText();

        Map<String, Object> finca = new HashMap<>();
        finca.put("nombreFinca", "Finca Dagua");
        finca.put("municipio", "Dagua");
        finca.put("hectareas", 5);
        String fincaCreada = mockMvc.perform(post("/api/v1/fincas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finca)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> producto = new HashMap<>();
        producto.put("fincaId", objectMapper.readTree(fincaCreada).get("id").asLong());
        producto.put("tipo", "Plátano");
        producto.put("categoria", "Frutas");
        producto.put("cantidadKg", 100);
        producto.put("precioUnitario", 2500);
        producto.put("fechaCosecha", COSECHA.toString());
        String publicado = mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        productoId = objectMapper.readTree(publicado).get("id").asLong();
    }

    @Test
    void detalle_ofertaExistente_devuelve200ConFincaMunicipioCantidadYPrecio() throws Exception {
        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId))
                .andExpect(jsonPath("$.tipo").value("Plátano"))
                .andExpect(jsonPath("$.categoria").value("Frutas"))
                .andExpect(jsonPath("$.municipio").value("Dagua"))
                .andExpect(jsonPath("$.cantidadKg").value(100))
                .andExpect(jsonPath("$.precioUnitario").value(2500))
                .andExpect(jsonPath("$.fechaCosecha").value(COSECHA.toString()))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.fincaId").exists());
    }

    @Test
    void detalle_noRequiereToken() throws Exception {
        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(status().isOk());
    }

    @Test
    void detalle_ofertaInexistente_devuelve404ConMensajeDescriptivo() throws Exception {
        long ausente = productoId + 9999;
        mockMvc.perform(get("/api/v1/productos/" + ausente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe una oferta con el id " + ausente))
                .andExpect(jsonPath("$.path").value("/api/v1/productos/" + ausente));
    }
}
