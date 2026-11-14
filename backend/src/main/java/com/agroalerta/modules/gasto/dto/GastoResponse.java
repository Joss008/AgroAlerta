package com.agroalerta.modules.gasto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoResponse(
        Long id,
        Long idCampana,
        String concepto,
        LocalDate fechaGasto,
        BigDecimal monto,
        String nombreCampana) {
}
