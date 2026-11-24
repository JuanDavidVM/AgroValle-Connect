package com.agrovalle.connect.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarritoResponse(
        List<ItemCarritoResponse> items,
        List<SubtotalAgricultorResponse> subtotalesPorAgricultor,
        BigDecimal total) {
}
