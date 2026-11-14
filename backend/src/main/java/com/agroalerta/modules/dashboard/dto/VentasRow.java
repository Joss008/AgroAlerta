package com.agroalerta.modules.dashboard.dto;

import java.math.BigDecimal;

public record VentasRow(
        Long idCampana,
        String nombreCampana,
        BigDecimal cantidadKg,
        BigDecimal montoSoles,
        BigDecimal precioPromedioSoles) {
}
