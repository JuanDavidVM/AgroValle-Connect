package com.agrovalle.connect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.event.CosechaPublicadaEvent;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.model.RegistroAuditoria;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.repository.RegistroAuditoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@RecordApplicationEvents
class ProductoApiTest {

    private static final String CORREO = "maria@correo.com";
    private static final String CLAVE = "Clave12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private RegistroAuditoriaRepository registroRepository;
    @Autowired
    private ApplicationEvents eventos;

    private void registrarAgricultor(String correo, String documento) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreCompleto", "María Gómez");
        m.put("documento", documento);
        m.put("correo", correo);
        m.put("telefono", "3001234567");
        m.put("municipio", "Palmira");
        m.put("contrasena", CLAVE);
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private void registrarComprador() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("razonSocial", "Supermercados del Valle S.A.S.");
        m.put("nit", "900123456-7");
        m.put("correo", "compras@valle.com");
        m.put("telefono", "3109876543");
        m.put("contrasena", CLAVE);
        mockMvc.perform(post("/api/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private String tokenDe(String correo) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("correo", correo);
        m.put("contrasena", CLAVE);
        String cuerpo = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(cuerpo).get("token").asText();
    }

    private long crearFinca(String token) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreFinca", "La Esperanza");
        m.put("municipio", "Dagua");
        m.put("hectareas", 12.5);
        String cuerpo = mockMvc.perform(post("/api/v1/fincas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(cuerpo).get("id").asLong();
    }

    private Map<String, Object> productoValido(long fincaId) {
        Map<String, Object> m = new HashMap<>();
        m.put("fincaId", fincaId);
        m.put("tipo", "Plátano hartón");
        m.put("categoria", "Frutas");
        m.put("cantidadKg", 150.5);
        m.put("precioUnitario", 2800);
        m.put("fechaCosecha", LocalDate.now().plusDays(7).toString());
        return m;
    }

    private ResultActions publicar(String token, Map<String, Object> cuerpo) throws Exception {
        var peticion = post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuerpo));
        if (token != null) {
            peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    private long contarEventos() {
        return eventos.stream(CosechaPublicadaEvent.class).count();
    }

    @Test
    void publicar_datosValidos_devuelve201YPersisteElProductoActivo() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);
        long fincaId = crearFinca(token);

        publicar(token, productoValido(fincaId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Plátano hartón"))
                .andExpect(jsonPath("$.categoria").value("Frutas"))
                .andExpect(jsonPath("$.cantidadKg").value(150.5))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.municipio").value("Dagua"))
                .andExpect(jsonPath("$.fincaId").value(fincaId));

        List<Producto> productos = productoRepository.findAll();
        assertEquals(1, productos.size());
        assertEquals(EstadoOferta.ACTIVA, productos.get(0).getEstado());
        assertEquals(fincaId, productos.get(0).getFinca().getId().longValue());
    }

    @Test
    void publicar_datosValidos_emiteElEventoYDejaRegistroDeAuditoria() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);
        long fincaId = crearFinca(token);

        publicar(token, productoValido(fincaId)).andExpect(status().isCreated());

        assertEquals(1L, contarEventos());
        List<RegistroAuditoria> registros = registroRepository.findAll();
        assertEquals(1, registros.size());
        assertEquals("COSECHA_PUBLICADA", registros.get(0).getAccion());
        assertEquals(CORREO, registros.get(0).getUsuario());
        assertEquals(productoRepository.findAll().get(0).getId(), registros.get(0).getEntidadId());
    }

    @Test
    void publicar_fechaDeCosechaAnteriorAHoy_devuelve400YNoEmiteEvento() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);
        Map<String, Object> cuerpo = productoValido(crearFinca(token));
        cuerpo.put("fechaCosecha", LocalDate.now().minusDays(1).toString());

        publicar(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.fechaCosecha").value("La fecha de cosecha no puede ser anterior a hoy"));

        assertEquals(0, productoRepository.count());
        assertEquals(0L, contarEventos());
    }

    @Test
    void publicar_cantidadYPrecioEnCero_devuelve400PorCampo() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);
        Map<String, Object> cuerpo = productoValido(crearFinca(token));
        cuerpo.put("cantidadKg", 0);
        cuerpo.put("precioUnitario", -5);

        publicar(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.cantidadKg").value("La cantidad debe ser mayor a cero"))
                .andExpect(jsonPath("$.errores.precioUnitario").exists());
    }

    @Test
    void publicar_categoriaFueraDelCatalogo_devuelve400() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);
        Map<String, Object> cuerpo = productoValido(crearFinca(token));
        cuerpo.put("categoria", "Electrodomésticos");

        publicar(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.categoria").value("La categoría no pertenece al catálogo de productos"));
    }

    @Test
    void publicar_sinFinca_devuelve400() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);
        Map<String, Object> cuerpo = productoValido(crearFinca(token));
        cuerpo.remove("fincaId");

        publicar(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.fincaId").exists());
    }

    @Test
    void publicar_sinToken_devuelve401() throws Exception {
        publicar(null, productoValido(1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicar_conTokenDeComprador_devuelve403() throws Exception {
        registrarComprador();
        String token = tokenDe("compras@valle.com");

        publicar(token, productoValido(1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicar_fincaInexistente_devuelve404() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        String token = tokenDe(CORREO);

        publicar(token, productoValido(999_999L))
                .andExpect(status().isNotFound());

        assertEquals(0L, contarEventos());
    }

    @Test
    void publicar_fincaDeOtroAgricultor_devuelve404() throws Exception {
        registrarAgricultor(CORREO, "1144123456");
        registrarAgricultor("otro@correo.com", "1144999999");
        long fincaAjena = crearFinca(tokenDe("otro@correo.com"));

        publicar(tokenDe(CORREO), productoValido(fincaAjena))
                .andExpect(status().isNotFound());

        assertEquals(0, productoRepository.count());
    }
}
