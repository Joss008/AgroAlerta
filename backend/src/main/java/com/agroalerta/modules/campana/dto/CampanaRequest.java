package com.agroalerta.modules.campana.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CampanaRequest(
        @NotNull(message = "La parcela es obligatoria")
        Long idParcela,

        @NotNull(message = "El cultivo es obligatorio")
        Long idCultivo,

        @NotBlank(message = "El nombre de la campaña es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin) {
}
