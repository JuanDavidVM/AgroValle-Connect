package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.ReservaRequest;
import com.agrovalle.connect.dto.ReservaResponse;
import com.agrovalle.connect.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservas")
@Tag(name = "HU-10 Reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Apartar una cantidad de una oferta activa y descontarla del stock "
            + "(201 Created con el stock restante; 400 datos inválidos; 401 sin token; "
            + "403 si no es comprador; 404 si la oferta no existe; "
            + "409 si la oferta no está activa o la cantidad supera el stock)")
    public ResponseEntity<ReservaResponse> reservar(Authentication authentication,
                                                    @Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.reservar(authentication.getName(), request));
    }
}
