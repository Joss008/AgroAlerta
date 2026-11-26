package com.agroalerta.modules.parcela.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ParcelaRequest(
        @NotNull(message = "El agricultor es obligatorio")
        Long idAgricultor,

        @NotBlank(message = "El nombre de la parcela es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @NotNull(message = "El área es obligatoria")
        @Positive(message = "El área debe ser mayor a 0 hectáreas")
        BigDecimal areaHectareas,

        @Size(max = 200, message = "La ubicación no puede superar 200 caracteres")
        String ubicacion,

        @Size(max = 200000, message = "La imagen supera el tamaño máximo permitido")
        String fotoUrl) {
}
