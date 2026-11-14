package com.agroalerta.modules.venta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaRequest(
        @NotNull(message = "La cosecha es obligatoria")
        Long idCosecha,

        @NotNull(message = "La fecha de venta es obligatoria")
        LocalDate fechaVenta,

        @NotNull(message = "La cantidad es obligatoria")
        @PositiveOrZero(message = "La cantidad vendida no puede ser negativa")
        BigDecimal cantidadKg,

        @NotNull(message = "El precio total es obligatorio")
        @PositiveOrZero(message = "El precio total no puede ser negativo")
        BigDecimal precioTotal,

        @NotBlank(message = "El comprador es obligatorio")
        @Size(max = 150, message = "El comprador no puede superar 150 caracteres")
        String comprador) {
}
