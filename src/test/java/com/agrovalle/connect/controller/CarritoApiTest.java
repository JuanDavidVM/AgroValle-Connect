package com.agrovalle.connect.controller;

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
 * HU-11: consolidación del carrito con subtotal por agricultor y total general.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CarritoApiTest {

    private static final String CLAVE = "Clave12345";
    private static final String CORREO_COMPRADOR = "compras@valle.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoRepository productoRepository;

    private String tokenComprador;
    private String tokenAgricultor;
    private long platano;
    private long mango;

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
        crear("/api/v1/compradores", comprador);

        tokenAgricultor = tokenDe("maria@correo.com");
        tokenComprador = tokenDe(CORREO_COMPRADOR);

        String tokenPedro = tokenDe("pedro@correo.com");
        platano = publicar(tokenAgricultor, "Finca Dagua", "Plátano", 100, 2500);
        mango = publicar(tokenPedro, "Finca Buga", "Mango", 50, 4000);
    }

    private void registrarAgricultor(String nombre, String documento, String correo) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("nombreCompleto", nombre);
        m.put("documento", documento);
        m.put("correo", correo);
        m.put("telefono", "3001234567");
        m.put("municipio", "Palmira");
        m.put("contrasena", CLAVE);
        crear("/api/v1/agricultores", m);
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

    private ResultActions agregar(String token, Object productoRef, Object cantidad) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("productoId", productoRef);
        m.put("cantidadKg", cantidad);
        var peticion = post("/api/v1/carrito/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(m));
        if (token != null) {
            peticion = peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    private ResultActions consultar(String token) throws Exception {
        var peticion = get("/api/v1/carrito");
        if (token != null) {
            peticion = peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    @Test
    void agregar_ofertaActiva_devuelve201ConElSubtotalDelRenglon() throws Exception {
        agregar(tokenComprador, platano, 10)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(platano))
                .andExpect(jsonPath("$.producto").value("Plátano"))
                .andExpect(jsonPath("$.agricultor").value("María Gómez"))
                .andExpect(jsonPath("$.cantidadKg").value(10))
                .andExpect(jsonPath("$.precioUnitario").value(2500))
                .andExpect(jsonPath("$.subtotal").value(25000.00));
    }

    @Test
    void consultar_dosOfertasDeAgricultoresDistintos_agrupaSubtotalesYSumaElTotal() throws Exception {
        agregar(tokenComprador, platano, 10).andExpect(status().isCreated());
        agregar(tokenComprador, mango, 5).andExpect(status().isCreated());

        consultar(tokenComprador)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.subtotalesPorAgricultor", hasSize(2)))
                .andExpect(jsonPath("$.subtotalesPorAgricultor[0].agricultor").value("María Gómez"))
                .andExpect(jsonPath("$.subtotalesPorAgricultor[0].subtotal").value(25000.00))
                .andExpect(jsonPath("$.subtotalesPorAgricultor[1].agricultor").value("Pedro Ruiz"))
                .andExpect(jsonPath("$.subtotalesPorAgricultor[1].subtotal").value(20000.00))
                .andExpect(jsonPath("$.total").value(45000.00));
    }

    @Test
    void agregar_dosVecesLaMismaOferta_acumulaEnUnSoloRenglon() throws Exception {
        agregar(tokenComprador, platano, 10).andExpect(status().isCreated());
        agregar(tokenComprador, platano, 15)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidadKg").value(25))
                .andExpect(jsonPath("$.subtotal").value(62500.00));

        consultar(tokenComprador)
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.total").value(62500.00));
    }

    @Test
    void consultar_carritoVacio_devuelve200ConTotalEnCero() throws Exception {
        consultar(tokenComprador)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.subtotalesPorAgricultor", hasSize(0)))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void agregar_cantidadAcumuladaSuperiorAlStock_devuelve409() throws Exception {
        agregar(tokenComprador, mango, 40).andExpect(status().isCreated());
        agregar(tokenComprador, mango, 20)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La cantidad acumulada en el carrito supera el stock disponible: quedan 50 kg"));
    }

    @Test
    void agregar_ofertaInactiva_devuelve409() throws Exception {
        Producto producto = productoRepository.findById(platano).orElseThrow();
        producto.setEstado(EstadoOferta.INACTIVA);
        productoRepository.saveAndFlush(producto);

        agregar(tokenComprador, platano, 10)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La oferta no está activa y no puede agregarse al carrito"));
    }

    @Test
    void agregar_ofertaInexistente_devuelve404() throws Exception {
        long ausente = platano + 9999;
        agregar(tokenComprador, ausente, 10)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe una oferta con el id " + ausente));
    }

    @Test
    void agregar_sinToken_devuelve401() throws Exception {
        agregar(null, platano, 10).andExpect(status().isUnauthorized());
    }

    @Test
    void agregar_conTokenDeAgricultor_devuelve403() throws Exception {
        agregar(tokenAgricultor, platano, 10).andExpect(status().isForbidden());
    }

    @Test
    void consultar_sinToken_devuelve401() throws Exception {
        consultar(null).andExpect(status().isUnauthorized());
    }

    @Test
    void agregar_cantidadEnCero_devuelve400PorCampo() throws Exception {
        agregar(tokenComprador, platano, 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.cantidadKg").value("La cantidad debe ser mayor a cero"));
    }
}
