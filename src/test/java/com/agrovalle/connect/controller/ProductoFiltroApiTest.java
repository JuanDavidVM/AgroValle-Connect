package com.agrovalle.connect.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.repository.ProductoRepository;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * HU-04: filtro de ofertas por municipio y categoría. La consulta es pública (no requiere token).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductoFiltroApiTest {

    private static final String CORREO = "maria@correo.com";
    private static final String CLAVE = "Clave12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoRepository productoRepository;

    private String token;

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
        String cuerpo = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(cuerpo).get("token").asText();

        long fincaDagua = crearFinca("Finca Dagua", "Dagua");
        long fincaPalmira = crearFinca("Finca Palmira", "Palmira");
        publicar(fincaDagua, "Plátano", "Frutas");
        publicar(fincaPalmira, "Mango", "Frutas");
        publicar(fincaDagua, "Yuca", "Tubérculos");
        publicar(fincaDagua, "Lulo", "Frutas");
        desactivar("Lulo");
    }

    private long crearFinca(String nombre, String municipio) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreFinca", nombre);
        m.put("municipio", municipio);
        m.put("hectareas", 5);
        String cuerpo = mockMvc.perform(post("/api/v1/fincas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(cuerpo).get("id").asLong();
    }

    private void publicar(long fincaId, String tipo, String categoria) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("fincaId", fincaId);
        m.put("tipo", tipo);
        m.put("categoria", categoria);
        m.put("cantidadKg", 100);
        m.put("precioUnitario", 2500);
        m.put("fechaCosecha", LocalDate.now().plusDays(5).toString());
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private void desactivar(String nombre) {
        Producto producto = productoRepository.findAll().stream()
                .filter(p -> p.getNombre().equals(nombre))
                .findFirst().orElseThrow();
        producto.setEstado(EstadoOferta.INACTIVA);
        productoRepository.saveAndFlush(producto);
    }

    private ResultActions consultar(String consulta) throws Exception {
        return mockMvc.perform(get("/api/v1/productos" + consulta));
    }

    @Test
    void filtrar_municipioYCategoria_devuelveSoloLasOfertasActivasQueCoinciden() throws Exception {
        consultar("?municipio=Dagua&categoria=Frutas")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tipo", containsInAnyOrder("Plátano")))
                .andExpect(jsonPath("$[0].municipio").value("Dagua"))
                .andExpect(jsonPath("$[0].categoria").value("Frutas"))
                .andExpect(jsonPath("$[0].estado").value("ACTIVA"));
    }

    @Test
    void filtrar_sinCoincidencias_devuelve200ConArregloVacio() throws Exception {
        consultar("?municipio=Buga&categoria=Frutas")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void filtrar_soloMunicipio_devuelveLasActivasDeEseMunicipio() throws Exception {
        consultar("?municipio=Dagua")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tipo", containsInAnyOrder("Plátano", "Yuca")));
    }

    @Test
    void filtrar_soloCategoria_devuelveLasActivasDeEsaCategoria() throws Exception {
        consultar("?categoria=Frutas")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tipo", containsInAnyOrder("Plátano", "Mango")));
    }

    @Test
    void filtrar_sinFiltros_devuelveTodasLasActivasYNoRequiereToken() throws Exception {
        consultar("")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tipo", containsInAnyOrder("Plátano", "Mango", "Yuca")));
    }

    @Test
    void filtrar_ignoraTildesYMayusculas() throws Exception {
        consultar("?municipio=dagua&categoria=tuberculos")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tipo", containsInAnyOrder("Yuca")));
    }

    @Test
    void filtrar_municipioFueraDelCatalogo_devuelve400() throws Exception {
        consultar("?municipio=Bogotá")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El municipio no pertenece al Valle del Cauca: Bogotá"));
    }

    @Test
    void filtrar_categoriaFueraDelCatalogo_devuelve400() throws Exception {
        consultar("?categoria=Ropa")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La categoría no pertenece al catálogo: Ropa"));
    }
}
