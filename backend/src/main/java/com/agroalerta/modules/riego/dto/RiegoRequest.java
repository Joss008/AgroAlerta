package com.agroalerta.modules.riego.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RiegoRequest(
        @NotNull(message = "La campaña es obligatoria")
        Long idCampana,

        @NotNull(message = "La fecha de riego es obligatoria")
        LocalDate fechaRiego,

        @NotNull(message = "La cantidad es obligatoria")
        @PositiveOrZero(message = "La cantidad de agua no puede ser negativa")
        BigDecimal cantidadM3,

        @Size(max = 300, message = "La observación no puede superar 300 caracteres")
        String observacion) {
}
