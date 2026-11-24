package com.agrovalle.connect.dto;

import java.math.BigDecimal;

public record SubtotalAgricultorResponse(
        Long agricultorId,
        String agricultor,
        BigDecimal subtotal) {
}
