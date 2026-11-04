package com.agrovalle.connect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.UsuarioRepository;
import com.agrovalle.connect.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class AuthApiTest {

    private static final String CORREO = "maria@correo.com";
    private static final String CLAVE = "Clave12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Value("${app.jwt.secret}")
    private String secret;

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

    private ResultActions login(String correo, String contrasena) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("correo", correo);
        m.put("contrasena", contrasena);
        return mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(m)));
    }

    private String obtenerToken() throws Exception {
        registrarAgricultor();
        String cuerpo = login(CORREO, CLAVE)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(cuerpo);
        return json.get("token").asText();
    }

    @Test
    void contrasena_seGuardaConHash_noEnTextoPlano() throws Exception {
        registrarAgricultor();

        Usuario usuario = usuarioRepository.findByCorreo(CORREO).orElseThrow();
        assertNotEquals(CLAVE, usuario.getPasswordHash());
        assertTrue(usuario.getPasswordHash().startsWith("$2"));
    }

    @Test
    void login_credencialesValidas_devuelveJwt() throws Exception {
        registrarAgricultor();

        login(CORREO, CLAVE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("AGRICULTOR"))
                .andExpect(jsonPath("$.expiraEnSegundos").value(3600));
    }

    @Test
    void login_contrasenaIncorrecta_devuelve401() throws Exception {
        registrarAgricultor();

        login(CORREO, "ClaveIncorrecta")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }

    @Test
    void login_usuarioInexistente_devuelve401() throws Exception {
        login("nadie@correo.com", CLAVE)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_datosInvalidos_devuelve400() throws Exception {
        login("no-es-correo", "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.contrasena").exists());
    }

    @Test
    void endpointProtegido_conTokenValido_permiteAcceso() throws Exception {
        String token = obtenerToken();

        mockMvc.perform(get("/api/v1/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(CORREO))
                .andExpect(jsonPath("$.rol").value("AGRICULTOR"));
    }

    @Test
    void endpointProtegido_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegido_tokenInvalido_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer esto.no.es-un-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("El token es inválido"));
    }

    @Test
    void endpointProtegido_tokenExpirado_devuelve401() throws Exception {
        registrarAgricultor();
        String expirado = new JwtService(secret, -1_000L).generarToken(CORREO, "AGRICULTOR");

        mockMvc.perform(get("/api/v1/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + expirado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("El token ha expirado"));
    }

    @Test
    void endpointProtegido_tokenFirmadoConOtraClave_devuelve401() throws Exception {
        registrarAgricultor();
        String falso = new JwtService("otra-clave-distinta-para-pruebas-987654321012", 3_600_000L)
                .generarToken(CORREO, "AGRICULTOR");

        mockMvc.perform(get("/api/v1/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + falso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegido_esquemaDistintoDeBearer_devuelve401() throws Exception {
        String token = obtenerToken();
        assertEquals(3, token.split("\\.").length);

        mockMvc.perform(get("/api/v1/auth/me").header(HttpHeaders.AUTHORIZATION, "Basic " + token))
                .andExpect(status().isUnauthorized());
    }
}
