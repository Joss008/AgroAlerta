package com.agroalerta.modules.dashboard.dto;

import java.math.BigDecimal;

public record ProduccionRow(
        Long idCampana,
        String nombreCampana,
        String cultivo,
        String parcela,
        BigDecimal areaHectareas,
        BigDecimal produccionKg,
        BigDecimal rendimientoKgHa) {
}
