package com.agroalerta.modules.cosecha.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CosechaRequest(
        @NotNull(message = "La campaña es obligatoria")
        Long idCampana,

        @NotNull(message = "La fecha de cosecha es obligatoria")
        LocalDate fechaCosecha,

        @NotNull(message = "La cantidad es obligatoria")
        @PositiveOrZero(message = "La cantidad cosechada no puede ser negativa")
        BigDecimal cantidadKg,

        @Size(max = 300, message = "La observación no puede superar 300 caracteres")
        String observacion) {
}
