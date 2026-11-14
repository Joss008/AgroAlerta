package com.agroalerta.modules.gasto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoRequest(
        @NotNull(message = "La campaña es obligatoria")
        Long idCampana,

        @NotBlank(message = "El concepto es obligatorio")
        @Size(max = 150, message = "El concepto no puede superar 150 caracteres")
        String concepto,

        @NotNull(message = "La fecha del gasto es obligatoria")
        LocalDate fechaGasto,

        @NotNull(message = "El monto es obligatorio")
        @PositiveOrZero(message = "El monto no puede ser negativo")
        BigDecimal monto) {
}
