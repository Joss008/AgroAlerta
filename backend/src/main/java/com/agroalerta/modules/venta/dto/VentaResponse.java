package com.agroalerta.modules.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaResponse(
        Long id,
        Long idCosecha,
        LocalDate fechaVenta,
        BigDecimal cantidadKg,
        BigDecimal precioTotal,
        String comprador,
        LocalDate fechaCosecha,
        String nombreCampana) {
}
