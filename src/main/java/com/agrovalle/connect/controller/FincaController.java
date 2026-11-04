package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.FincaRequest;
import com.agrovalle.connect.dto.FincaResponse;
import com.agrovalle.connect.service.FincaService;
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
@RequestMapping("/api/v1/fincas")
@Tag(name = "HU-08 Fincas")
public class FincaController {

    private final FincaService fincaService;

    public FincaController(FincaService fincaService) {
        this.fincaService = fincaService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registrar una finca del agricultor autenticado "
            + "(201 Created; 400 datos o municipio inválidos; 401 sin token; "
            + "403 si no es agricultor; 409 nombre duplicado)")
    public ResponseEntity<FincaResponse> registrar(Authentication authentication,
                                                   @Valid @RequestBody FincaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fincaService.registrar(authentication.getName(), request));
    }
}
