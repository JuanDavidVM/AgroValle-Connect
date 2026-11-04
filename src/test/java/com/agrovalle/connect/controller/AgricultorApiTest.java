package com.agrovalle.connect.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.repository.AgricultorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgricultorApiTest {

    private static final String URL = "/api/v1/agricultores";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AgricultorRepository agricultorRepository;

    private Map<String, Object> cuerpoValido(String documento, String correo) {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreCompleto", "María Gómez");
        m.put("documento", documento);
        m.put("correo", correo);
        m.put("telefono", "3001234567");
        m.put("municipio", "Palmira");
        m.put("contrasena", "Clave12345");
        return m;
    }

    private org.springframework.test.web.servlet.ResultActions enviar(Map<String, Object> cuerpo) throws Exception {
        return mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuerpo)));
    }

    @Test
    void registrar_datosValidos_devuelve201YPersiste() throws Exception {
        enviar(cuerpoValido("1144123456", "maria@correo.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.documento").value("1144123456"))
                .andExpect(jsonPath("$.correo").value("maria@correo.com"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        assertTrue(agricultorRepository.existsByDocumento("1144123456"));
    }

    @Test
    void registrar_datosInvalidos_devuelve400ConDetalle() throws Exception {
        Map<String, Object> cuerpo = cuerpoValido("abc", "no-es-un-correo");
        cuerpo.put("contrasena", "corta");
        cuerpo.put("nombreCompleto", "");

        enviar(cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.documento").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.contrasena").exists())
                .andExpect(jsonPath("$.errores.nombreCompleto").exists());
    }

    @Test
    void registrar_camposFaltantes_devuelve400() throws Exception {
        enviar(new HashMap<>())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.documento").exists());
    }

    @Test
    void registrar_documentoDuplicado_devuelve409() throws Exception {
        enviar(cuerpoValido("1144123456", "maria@correo.com")).andExpect(status().isCreated());

        enviar(cuerpoValido("1144123456", "otra@correo.com"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registrar_correoDuplicado_devuelve409() throws Exception {
        enviar(cuerpoValido("1144123456", "maria@correo.com")).andExpect(status().isCreated());

        enviar(cuerpoValido("1155987654", "MARIA@correo.com"))
                .andExpect(status().isConflict());
    }
}
