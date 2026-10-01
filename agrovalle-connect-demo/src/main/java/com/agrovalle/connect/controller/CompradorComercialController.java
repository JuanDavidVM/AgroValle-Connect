package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.CompradorComercialRequest;
import com.agrovalle.connect.dto.CompradorComercialResponse;
import com.agrovalle.connect.service.CompradorComercialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compradores")
@Tag(name = "HU-07 Compradores comerciales")
public class CompradorComercialController {

    private final CompradorComercialService compradorService;

    public CompradorComercialController(CompradorComercialService compradorService) {
        this.compradorService = compradorService;
    }

    @PostMapping
    @Operation(summary = "Registrar un comprador comercial (201 Created; 400 datos inválidos; 409 duplicado)")
    public ResponseEntity<CompradorComercialResponse> registrar(
            @Valid @RequestBody CompradorComercialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compradorService.registrar(request));
    }
}
