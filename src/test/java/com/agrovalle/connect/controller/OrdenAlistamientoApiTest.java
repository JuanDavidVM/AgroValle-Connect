package com.agrovalle.connect.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * HU-13: confirmación de alistamiento del lote por parte del agricultor dueño de la orden.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrdenAlistamientoApiTest {

    private static final String CLAVE = "Clave12345";
    private static final String CORREO_COMPRADOR = "compras@valle.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String tokenMaria;
    private String tokenPedro;
    private String tokenComprador;
    private String numeroOrden;

    @BeforeEach
    void datosDePrueba() throws Exception {
        registrarAgricultor("María Gómez", "1144123456", "maria@correo.com");
        registrarAgricultor("Pedro Ruiz", "1144987654", "pedro@correo.com");

        Map<String, Object> comprador = new HashMap<>();
        comprador.put("razonSocial", "Supermercados del Valle S.A.S.");
        comprador.put("nit", "900123456-7");
        comprador.put("correo", CORREO_COMPRADOR);
        comprador.put("telefono", "3109876543");
        comprador.put("direccionComercial", "Calle 10 # 5-20");
        comprador.put("municipio", "Cali");
        comprador.put("contrasena", CLAVE);
        mockMvc.perform(post("/api/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comprador)))
                .andExpect(status().isCreated());

        tokenMaria = tokenDe("maria@correo.com");
        tokenPedro = tokenDe("pedro@correo.com");
        tokenComprador = tokenDe(CORREO_COMPRADOR);

        long producto = publicar(tokenMaria);
        pedir("/api/v1/reservas", producto, 10);
        pedir("/api/v1/carrito/items", producto, 10);

        String emitidas = mockMvc.perform(post("/api/v1/ordenes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenComprador))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        numeroOrden = objectMapper.readTree(emitidas).get(0).get("numeroOrden").asText();
    }

    private void registrarAgricultor(String nombre, String documento, String correo) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreCompleto", nombre);
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

    private long publicar(String token) throws Exception {
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

        Map<String, Object> m = new HashMap<>();
        m.put("fincaId", objectMapper.readTree(fincaCreada).get("id").asLong());
        m.put("tipo", "Plátano");
        m.put("categoria", "Frutas");
        m.put("cantidadKg", 100);
        m.put("precioUnitario", 2500);
        m.put("fechaCosecha", LocalDate.now().plusDays(5).toString());
        String publicado = mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(publicado).get("id").asLong();
    }

    private void pedir(String ruta, long productoId, int cantidad) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("productoId", productoId);
        m.put("cantidadKg", cantidad);
        mockMvc.perform(post(ruta)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenComprador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private ResultActions alistar(String token, String numero) throws Exception {
        var peticion = patch("/api/v1/ordenes/" + numero + "/alistamiento");
        if (token != null) {
            peticion = peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    @Test
    void alistar_ordenPropiaEnEstadoCreada_devuelve200YRegistraLaMarcaDeTiempo() throws Exception {
        alistar(tokenMaria, numeroOrden)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroOrden").value(numeroOrden))
                .andExpect(jsonPath("$.estado").value("ALISTADA"))
                .andExpect(jsonPath("$.fechaAlistamiento").exists());
    }

    @Test
    void alistar_ordenDeOtroAgricultor_devuelve403() throws Exception {
        alistar(tokenPedro, numeroOrden)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("No tiene permisos para acceder a este recurso"));
    }

    @Test
    void alistar_ordenYaAlistada_devuelve409() throws Exception {
        alistar(tokenMaria, numeroOrden).andExpect(status().isOk());
        alistar(tokenMaria, numeroOrden)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La orden " + numeroOrden + " ya fue alistada"));
    }

    @Test
    void alistar_ordenInexistente_devuelve404() throws Exception {
        alistar(tokenMaria, "AV-00000000-XXXXXXXX")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe una orden con el numero AV-00000000-XXXXXXXX"));
    }

    @Test
    void alistar_sinToken_devuelve401() throws Exception {
        alistar(null, numeroOrden).andExpect(status().isUnauthorized());
    }

    @Test
    void alistar_conTokenDeComprador_devuelve403() throws Exception {
        alistar(tokenComprador, numeroOrden).andExpect(status().isForbidden());
    }
}
