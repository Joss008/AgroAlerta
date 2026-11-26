package com.agroalerta.modules.agricultor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgricultorRequest(
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String apellidos,

        @Pattern(regexp = "^$|^\\+?[0-9\\s-]{5,20}$",
                message = "El teléfono debe tener entre 5 y 20 dígitos (se permiten +, espacios y guiones)")
        String telefono,

        @Pattern(regexp = "^$|^[0-9]{8}$",
                message = "El DNI debe tener exactamente 8 dígitos")
        String dni,

        @Size(max = 200000, message = "La imagen supera el tamaño máximo permitido")
        String fotoUrl) {
}
