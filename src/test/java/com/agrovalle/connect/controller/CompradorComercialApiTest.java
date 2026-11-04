package com.agrovalle.connect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.repository.CompradorComercialRepository;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CompradorComercialApiTest {

    private static final String URL = "/api/v1/compradores";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CompradorComercialRepository compradorRepository;

    private Map<String, Object> cuerpoValido(String nit, String correo) {
        Map<String, Object> m = new HashMap<>();
        m.put("razonSocial", "Supermercados del Valle S.A.S.");
        m.put("nit", nit);
        m.put("correo", correo);
        m.put("telefono", "3109876543");
        m.put("direccionComercial", "Calle 10 # 5-20");
        m.put("municipio", "Cali");
        m.put("contrasena", "Clave12345");
        return m;
    }

    private ResultActions enviar(Map<String, Object> cuerpo) throws Exception {
        return mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuerpo)));
    }

    @Test
    void registrar_datosValidos_devuelve201YPersiste() throws Exception {
        enviar(cuerpoValido("900123456-7", "compras@supervalle.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nit").value("900123456-7"))
                .andExpect(jsonPath("$.correo").value("compras@supervalle.com"))
                .andExpect(jsonPath("$.direccionComercial").value("Calle 10 # 5-20"))
                .andExpect(jsonPath("$.municipio").value("Cali"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        assertTrue(compradorRepository.existsByNit("900123456-7"));
    }

    @Test
    void registrar_datosInvalidos_devuelve400ConDetalle() throws Exception {
        Map<String, Object> cuerpo = cuerpoValido("123", "correo-malo");
        cuerpo.put("razonSocial", " ");

        enviar(cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nit").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.razonSocial").exists());
    }

    @Test
    void registrar_nitDuplicado_devuelve409() throws Exception {
        enviar(cuerpoValido("900123456-7", "compras@supervalle.com")).andExpect(status().isCreated());

        enviar(cuerpoValido("900123456-7", "otro@supervalle.com"))
                .andExpect(status().isConflict());
    }

    @Test
    void registrar_correoDuplicado_devuelve409() throws Exception {
        enviar(cuerpoValido("900123456-7", "compras@supervalle.com")).andExpect(status().isCreated());

        enviar(cuerpoValido("800654321-0", "compras@supervalle.com"))
                .andExpect(status().isConflict());
    }

    @Test
    void registrar_municipioFueraDelCatalogo_devuelve400() throws Exception {
        Map<String, Object> cuerpo = cuerpoValido("900123456-7", "compras@supervalle.com");
        cuerpo.put("municipio", "Bogotá");

        enviar(cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.municipio").value("El municipio no pertenece al Valle del Cauca"));

        assertEquals(0, compradorRepository.count());
    }

    @Test
    void registrar_sinDireccionComercial_devuelve400() throws Exception {
        Map<String, Object> cuerpo = cuerpoValido("900123456-7", "compras@supervalle.com");
        cuerpo.remove("direccionComercial");

        enviar(cuerpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.direccionComercial").exists());
    }
}
