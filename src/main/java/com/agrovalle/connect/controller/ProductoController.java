package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.dto.ProductoResponse;
import com.agrovalle.connect.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "HU-02 Productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Publicar un lote de cosecha del agricultor autenticado "
            + "(201 Created; 400 datos inválidos; 401 sin token; 403 si no es agricultor; "
            + "404 si la finca no existe o no es suya)")
    public ResponseEntity<ProductoResponse> publicar(Authentication authentication,
                                                     @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.publicar(authentication.getName(), request));
    }

    @GetMapping
    @Operation(summary = "Filtrar las ofertas activas por municipio y categoría "
            + "(200 OK con arreglo vacío si no hay coincidencias; 400 si el municipio o la categoría no existen)")
    public ResponseEntity<List<ProductoResponse>> buscar(
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(productoService.buscarOfertas(municipio, categoria));
    }
}
