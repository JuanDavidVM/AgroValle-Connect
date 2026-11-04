package com.agrovalle.connect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.repository.FincaRepository;
import com.agrovalle.connect.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FincaApiTest {

    private static final String CORREO = "maria@correo.com";
    private static final String CLAVE = "Clave12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FincaRepository fincaRepository;
    @Autowired
    private UsuarioService usuarioService;

    private void registrarAgricultor() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreCompleto", "María Gómez");
        m.put("documento", "1144123456");
        m.put("correo", CORREO);
        m.put("telefono", "3001234567");
        m.put("municipio", "Palmira");
        m.put("contrasena", CLAVE);
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private void registrarComprador(String correo) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("razonSocial", "Supermercados del Valle S.A.S.");
        m.put("nit", "900123456-7");
        m.put("correo", correo);
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

    private String tokenDeAgricultor() throws Exception {
        registrarAgricultor();
        return tokenDe(CORREO);
    }

    private Map<String, Object> fincaValida() {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreFinca", "La Esperanza");
        m.put("municipio", "Dagua");
        m.put("hectareas", 12.5);
        return m;
    }

    private ResultActions crearFinca(String token, Map<String, Object> cuerpo) throws Exception {
        var peticion = post("/api/v1/fincas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuerpo));
        if (token != null) {
            peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    @Test
    void registrar_conTokenDeAgricultor_devuelve201YAsociaLaFinca() throws Exception {
        String token = tokenDeAgricultor();

        crearFinca(token, fincaValida())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nombreFinca").value("La Esperanza"))
                .andExpect(jsonPath("$.municipio").value("Dagua"))
                .andExpect(jsonPath("$.hectareas").value(12.5))
                .andExpect(jsonPath("$.agricultorId").isNotEmpty());

        List<Finca> fincas = fincaRepository.findAll();
        assertEquals(1, fincas.size());
        assertEquals(Municipio.DAGUA, fincas.get(0).getMunicipio());
        assertEquals(CORREO, fincas.get(0).getAgricultor().getUsuario().getCorreo());
    }

    @Test
    void registrar_municipioSinTildesNiMayusculas_seNormaliza() throws Exception {
        String token = tokenDeAgricultor();
        Map<String, Object> cuerpo = fincaValida();
        cuerpo.put("municipio", " tulua ");

        crearFinca(token, cuerpo)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.municipio").value("Tuluá"));
    }

    @Test
    void registrar_municipioFueraDelCatalogo_devuelve400ConDetalle() throws Exception {
        String token = tokenDeAgricultor();
        Map<String, Object> cuerpo = fincaValida();
        cuerpo.put("municipio", "Bogotá");

        crearFinca(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.municipio").value("El municipio no pertenece al Valle del Cauca"));

        assertEquals(0, fincaRepository.count());
    }

    @Test
    void registrar_nombreVacioYHectareasEnCero_devuelve400PorCampo() throws Exception {
        String token = tokenDeAgricultor();
        Map<String, Object> cuerpo = fincaValida();
        cuerpo.put("nombreFinca", " ");
        cuerpo.put("hectareas", 0);

        crearFinca(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombreFinca").exists())
                .andExpect(jsonPath("$.errores.hectareas").exists());
    }

    @Test
    void registrar_sinHectareas_devuelve400() throws Exception {
        String token = tokenDeAgricultor();
        Map<String, Object> cuerpo = fincaValida();
        cuerpo.remove("hectareas");

        crearFinca(token, cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.hectareas").exists());
    }

    @Test
    void registrar_sinToken_devuelve401() throws Exception {
        crearFinca(null, fincaValida())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrar_conTokenDeComprador_devuelve403() throws Exception {
        registrarComprador("compras@valle.com");
        String token = tokenDe("compras@valle.com");

        crearFinca(token, fincaValida())
                .andExpect(status().isForbidden());
    }

    @Test
    void registrar_nombreDuplicadoDelMismoAgricultor_devuelve409() throws Exception {
        String token = tokenDeAgricultor();
        crearFinca(token, fincaValida()).andExpect(status().isCreated());
        Map<String, Object> repetida = fincaValida();
        repetida.put("nombreFinca", "la esperanza");

        crearFinca(token, repetida)
                .andExpect(status().isConflict());

        assertEquals(1, fincaRepository.count());
    }

    @Test
    void registrar_usuarioAgricultorSinPerfil_devuelve404() throws Exception {
        usuarioService.crear("huerfano@correo.com", CLAVE, Rol.AGRICULTOR);
        String token = tokenDe("huerfano@correo.com");

        crearFinca(token, fincaValida())
                .andExpect(status().isNotFound());
    }
}
