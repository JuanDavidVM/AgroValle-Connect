package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.OrdenResponse;
import com.agrovalle.connect.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ordenes")
@Tag(name = "HU-12 Órdenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Emitir las órdenes del carrito del comprador autenticado, una por agricultor "
            + "(201 Created con el numero de orden y el total de cada una; 401 sin token; "
            + "403 si no es comprador; 409 si el carrito está vacío o falta stock reservado)")
    public ResponseEntity<List<OrdenResponse>> emitir(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordenService.emitir(authentication.getName()));
    }
}
