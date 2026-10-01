package com.agrovalle.connect.controller;

import com.agrovalle.connect.dto.AgricultorRequest;
import com.agrovalle.connect.dto.AgricultorResponse;
import com.agrovalle.connect.service.AgricultorService;
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
@RequestMapping("/api/v1/agricultores")
@Tag(name = "HU-01 Agricultores")
public class AgricultorController {

    private final AgricultorService agricultorService;

    public AgricultorController(AgricultorService agricultorService) {
        this.agricultorService = agricultorService;
    }

    @PostMapping
    @Operation(summary = "Registrar un agricultor (201 Created; 400 datos inválidos; 409 duplicado)")
    public ResponseEntity<AgricultorResponse> registrar(@Valid @RequestBody AgricultorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agricultorService.registrar(request));
    }
}
