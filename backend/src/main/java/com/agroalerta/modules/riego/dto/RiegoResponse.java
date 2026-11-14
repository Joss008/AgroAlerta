package com.agroalerta.modules.riego.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RiegoResponse(
        Long id,
        Long idCampana,
        LocalDate fechaRiego,
        BigDecimal cantidadM3,
        String observacion,
        String nombreCampana) {
}
