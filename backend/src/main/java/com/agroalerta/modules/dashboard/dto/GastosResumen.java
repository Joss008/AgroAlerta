package com.agroalerta.modules.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record GastosResumen(
        List<GastoCampanaRow> porCampana,
        List<GastoConceptoRow> porConcepto) {

    public record GastoCampanaRow(Long idCampana, String nombreCampana, BigDecimal montoSoles) {
    }

    public record GastoConceptoRow(String concepto, BigDecimal montoSoles) {
    }
}
