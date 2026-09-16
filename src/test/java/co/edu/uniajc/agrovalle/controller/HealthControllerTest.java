package co.edu.uniajc.agrovalle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/health responde 200 OK con estado UP")
    void deberiaResponderEstadoUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("agrovalle-connect"));
    }

    @Test
    @DisplayName("Una ruta inexistente responde 404 Not Found")
    void deberiaResponderNotFoundEnRutaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/ruta-inexistente"))
                .andExpect(status().isNotFound());
    }
}
