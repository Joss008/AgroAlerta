package com.agroalerta.modules.cultivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CultivoRequest(
        @NotBlank(message = "El nombre del cultivo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 100, message = "La variedad no puede superar 100 caracteres")
        String variedad) {
}
