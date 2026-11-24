package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.CarritoResponse;
import com.agrovalle.connect.dto.ItemCarritoRequest;
import com.agrovalle.connect.dto.ItemCarritoResponse;
import com.agrovalle.connect.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "HU-11 Carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping("/items")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Agregar una oferta al carrito del comprador autenticado "
            + "(201 Created con el renglon y su subtotal; 400 datos inválidos; 401 sin token; "
            + "403 si no es comprador; 404 si la oferta no existe; "
            + "409 si la oferta no está activa o la cantidad acumulada supera el stock)")
    public ResponseEntity<ItemCarritoResponse> agregar(Authentication authentication,
                                                       @Valid @RequestBody ItemCarritoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carritoService.agregarItem(authentication.getName(), request));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Consultar el carrito con el subtotal por agricultor y el total general "
            + "(200 OK; 401 sin token; 403 si no es comprador)")
    public ResponseEntity<CarritoResponse> consultar(Authentication authentication) {
        return ResponseEntity.ok(carritoService.obtener(authentication.getName()));
    }
}
