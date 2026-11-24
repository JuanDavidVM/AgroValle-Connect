package com.agrovalle.connect.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.connect.model.EstadoReserva;
import com.agrovalle.connect.model.Orden;
import com.agrovalle.connect.model.Producto;
import com.agrovalle.connect.model.Reserva;
import com.agrovalle.connect.repository.OrdenRepository;
import com.agrovalle.connect.repository.ProductoRepository;
import com.agrovalle.connect.repository.ReservaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
 * HU-12: emisión de órdenes de compra, una por agricultor, desde el carrito.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrdenApiTest {

    private static final String CLAVE = "Clave12345";
    private static final String CORREO_COMPRADOR = "compras@valle.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private OrdenRepository ordenRepository;

    private String tokenComprador;
    private String tokenAgricultor;
    private long platano;
    private long mango;
    private long compradorId;

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
        String creado = mockMvc.perform(post("/api/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comprador)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        compradorId = objectMapper.readTree(creado).get("id").asLong();

        tokenAgricultor = tokenDe("maria@correo.com");
        tokenComprador = tokenDe(CORREO_COMPRADOR);
        platano = publicar(tokenAgricultor, "Finca Dagua", "Plátano", 100, 2500);
        mango = publicar(tokenDe("pedro@correo.com"), "Finca Buga", "Mango", 50, 4000);
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

    private long publicar(String token, String nombreFinca, String tipo, int kg, int precio) throws Exception {
        Map<String, Object> finca = new HashMap<>();
        finca.put("nombreFinca", nombreFinca);
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
        m.put("tipo", tipo);
        m.put("categoria", "Frutas");
        m.put("cantidadKg", kg);
        m.put("precioUnitario", precio);
        m.put("fechaCosecha", LocalDate.now().plusDays(5).toString());
        String publicado = mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(publicado).get("id").asLong();
    }

    private void reservar(long productoId, int cantidad) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("productoId", productoId);
        m.put("cantidadKg", cantidad);
        mockMvc.perform(post("/api/v1/reservas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenComprador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private void alCarrito(long productoId, int cantidad) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("productoId", productoId);
        m.put("cantidadKg", cantidad);
        mockMvc.perform(post("/api/v1/carrito/items")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenComprador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated());
    }

    private ResultActions emitir(String token) throws Exception {
        var peticion = post("/api/v1/ordenes");
        if (token != null) {
            peticion = peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    @Test
    void emitir_carritoConDosAgricultores_generaUnaOrdenPorAgricultor() throws Exception {
        reservar(platano, 10);
        reservar(mango, 5);
        alCarrito(platano, 10);
        alCarrito(mango, 5);

        emitir(tokenComprador)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroOrden", startsWith("AV-")))
                .andExpect(jsonPath("$[0].estado").value("CREADA"))
                .andExpect(jsonPath("$[0].agricultor").value("María Gómez"))
                .andExpect(jsonPath("$[0].total").value(25000.00))
                .andExpect(jsonPath("$[1].agricultor").value("Pedro Ruiz"))
                .andExpect(jsonPath("$[1].total").value(20000.00));
    }

    @Test
    void emitir_vaciaElCarrito() throws Exception {
        reservar(platano, 10);
        alCarrito(platano, 10);
        emitir(tokenComprador).andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/carrito")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenComprador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void emitir_dejaLasReservasComoConsumidasYLigadasALaOrden() throws Exception {
        reservar(platano, 10);
        alCarrito(platano, 10);
        emitir(tokenComprador).andExpect(status().isCreated());

        List<Reserva> consumidas = reservaRepository.findByCompradorIdAndProductoIdAndEstado(
                compradorId, platano, EstadoReserva.CONSUMIDA);
        assertEquals(1, consumidas.size());
        assertNotNull(consumidas.get(0).getOrden());
    }

    @Test
    void emitir_congelaElPrecioAunqueLaOfertaCambieDespues() throws Exception {
        reservar(platano, 10);
        alCarrito(platano, 10);
        String cuerpo = emitir(tokenComprador).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String numero = objectMapper.readTree(cuerpo).get(0).get("numeroOrden").asText();

        Producto producto = productoRepository.findById(platano).orElseThrow();
        producto.setPrecioUnitario(new BigDecimal("9999.00"));
        productoRepository.saveAndFlush(producto);

        Orden orden = ordenRepository.findByNumeroOrden(numero).orElseThrow();
        assertEquals(0, orden.getDetalles().get(0).getPrecioUnitario().compareTo(new BigDecimal("2500")));
        assertEquals(0, orden.getTotal().compareTo(new BigDecimal("25000.00")));
    }

    @Test
    void emitir_conReservaMayorQueLoPedido_devuelveElSobranteAlStock() throws Exception {
        reservar(platano, 20);
        alCarrito(platano, 10);
        emitir(tokenComprador).andExpect(status().isCreated());

        Producto producto = productoRepository.findById(platano).orElseThrow();
        assertEquals(0, producto.getCantidadDisponible().compareTo(new BigDecimal("90.00")));
    }

    @Test
    void emitir_sinStockReservado_devuelve409YNoEmiteNingunaOrden() throws Exception {
        alCarrito(platano, 10);

        emitir(tokenComprador)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La oferta Plátano no tiene stock reservado suficiente: hay 0 reservados "
                                + "y la orden pide 10"));

        mockMvc.perform(get("/api/v1/carrito")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenComprador))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void emitir_carritoVacio_devuelve409() throws Exception {
        emitir(tokenComprador)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El carrito está vacío: no hay nada que ordenar"));
    }

    @Test
    void emitir_sinToken_devuelve401() throws Exception {
        emitir(null).andExpect(status().isUnauthorized());
    }

    @Test
    void emitir_conTokenDeAgricultor_devuelve403() throws Exception {
        emitir(tokenAgricultor).andExpect(status().isForbidden());
    }
}
