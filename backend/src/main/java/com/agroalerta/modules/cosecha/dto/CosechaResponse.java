package com.agroalerta.modules.cosecha.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CosechaResponse(
        Long id,
        Long idCampana,
        LocalDate fechaCosecha,
        BigDecimal cantidadKg,
        String observacion,
        String nombreCampana) {
}
