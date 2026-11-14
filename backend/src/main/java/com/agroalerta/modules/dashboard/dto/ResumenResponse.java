package com.agroalerta.modules.dashboard.dto;

import java.math.BigDecimal;

public record ResumenResponse(
        long agricultores,
        long parcelas,
        long cultivos,
        long campanas,
        long campanasActivas,
        BigDecimal areaTotalHectareas,
        BigDecimal produccionTotalKg,
        BigDecimal ventasTotalSoles,
        BigDecimal gastosTotalSoles,
        BigDecimal rentabilidadTotalSoles,
        BigDecimal aguaTotalM3) {
}
