package com.agrovalle.connect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
 * HU-10: reserva de inventario con descuento transaccional del stock.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReservaApiTest {

    private static final String CORREO_AGRICULTOR = "maria@correo.com";
    private static final String CORREO_COMPRADOR = "compras@valle.com";
    private static final String CLAVE = "Clave12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoRepository productoRepository;

    private String tokenAgricultor;
    private String tokenComprador;
    private long productoId;

    @BeforeEach
    void datosDePrueba() throws Exception {
        Map<String, Object> agricultor = new HashMap<>();
        agricultor.put("nombreCompleto", "María Gómez");
        agricultor.put("documento", "1144123456");
        agricultor.put("correo", CORREO_AGRICULTOR);
        agricultor.put("telefono", "3001234567");
        agricultor.put("municipio", "Palmira");
        agricultor.put("contrasena", CLAVE);
        crear("/api/v1/agricultores", agricultor);

        Map<String, Object> comprador = new HashMap<>();
        comprador.put("razonSocial", "Supermercados del Valle S.A.S.");
        comprador.put("nit", "900123456-7");
        comprador.put("correo", CORREO_COMPRADOR);
        comprador.put("telefono", "3109876543");
        comprador.put("direccionComercial", "Calle 10 # 5-20");
        comprador.put("municipio", "Cali");
        comprador.put("contrasena", CLAVE);
        crear("/api/v1/compradores", comprador);

        tokenAgricultor = tokenDe(CORREO_AGRICULTOR);
        tokenComprador = tokenDe(CORREO_COMPRADOR);

        Map<String, Object> finca = new HashMap<>();
        finca.put("nombreFinca", "Finca Dagua");
        finca.put("municipio", "Dagua");
        finca.put("hectareas", 5);
        String fincaCreada = mockMvc.perform(post("/api/v1/fincas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAgricultor)
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
        producto.put("fechaCosecha", LocalDate.now().plusDays(5).toString());
        String publicado = mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAgricultor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        productoId = objectMapper.readTree(publicado).get("id").asLong();
    }

    private void crear(String ruta, Map<String, Object> cuerpo) throws Exception {
        mockMvc.perform(post(ruta)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuerpo)))
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

    private ResultActions reservar(String token, Object productoRef, Object cantidad) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("productoId", productoRef);
        m.put("cantidadKg", cantidad);
        var peticion = post("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(m));
        if (token != null) {
            peticion = peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    private Producto productoActual() {
        return productoRepository.findById(productoId).orElseThrow();
    }

    @Test
    void reservar_cantidadDisponible_devuelve201YDescuentaElStock() throws Exception {
        reservar(tokenComprador, productoId, 30)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(productoId))
                .andExpect(jsonPath("$.cantidadKg").value(30))
                .andExpect(jsonPath("$.stockRestante").value(70))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));

        assertEquals(0, productoActual().getCantidadDisponible().compareTo(new BigDecimal("70.00")));
    }

    @Test
    void reservar_dosVecesSobreLaMismaOferta_acumulaLosDescuentos() throws Exception {
        reservar(tokenComprador, productoId, 30).andExpect(status().isCreated());
        reservar(tokenComprador, productoId, 25)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockRestante").value(45));
    }

    @Test
    void reservar_descuentaElStockYAvanzaLaVersionDelProducto() throws Exception {
        Long versionInicial = productoActual().getVersion();
        reservar(tokenComprador, productoId, 10).andExpect(status().isCreated());
        assertEquals(versionInicial + 1, productoActual().getVersion());
    }

    @Test
    void reservar_cantidadSuperiorAlStock_devuelve409YNoModificaElInventario() throws Exception {
        reservar(tokenComprador, productoId, 150)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La cantidad solicitada supera el stock disponible: quedan 100 kg"));

        assertEquals(0, productoActual().getCantidadDisponible().compareTo(new BigDecimal("100.00")));
    }

    @Test
    void reservar_ofertaInactiva_devuelve409() throws Exception {
        Producto producto = productoActual();
        producto.setEstado(EstadoOferta.INACTIVA);
        productoRepository.saveAndFlush(producto);

        reservar(tokenComprador, productoId, 10)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La oferta no está activa y no admite reservas"));
    }

    @Test
    void reservar_ofertaInexistente_devuelve404() throws Exception {
        long ausente = productoId + 9999;
        reservar(tokenComprador, ausente, 10)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe una oferta con el id " + ausente));
    }

    @Test
    void reservar_sinToken_devuelve401() throws Exception {
        reservar(null, productoId, 10).andExpect(status().isUnauthorized());
    }

    @Test
    void reservar_conTokenDeAgricultor_devuelve403() throws Exception {
        reservar(tokenAgricultor, productoId, 10).andExpect(status().isForbidden());
    }

    @Test
    void reservar_cantidadEnCero_devuelve400PorCampo() throws Exception {
        reservar(tokenComprador, productoId, 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.cantidadKg").value("La cantidad debe ser mayor a cero"));
    }

    @Test
    void reservar_sinProducto_devuelve400PorCampo() throws Exception {
        reservar(tokenComprador, null, 10)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.productoId").value("El producto es obligatorio"));
    }
}
