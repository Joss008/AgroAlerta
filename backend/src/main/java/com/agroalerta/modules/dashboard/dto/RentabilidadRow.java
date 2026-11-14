package com.agroalerta.modules.dashboard.dto;

import java.math.BigDecimal;

public record RentabilidadRow(
        Long idCampana,
        String nombreCampana,
        BigDecimal produccionKg,
        BigDecimal ventasSoles,
        BigDecimal gastosSoles,
        BigDecimal rentabilidadSoles,
        BigDecimal rendimientoKgHa,
        BigDecimal consumoAguaM3) {
}
