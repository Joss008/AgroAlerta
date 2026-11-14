package com.agroalerta.modules.campana.dto;

import java.time.LocalDate;

public record CampanaResponse(
        Long id,
        Long idParcela,
        Long idCultivo,
        String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String nombreParcela,
        String nombreCultivo) {
}
