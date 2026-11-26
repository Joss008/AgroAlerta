package com.agroalerta.modules.agricultor.dto;

public record AgricultorResponse(
        Long id,
        String nombres,
        String apellidos,
        String telefono,
        String dni,
        String fotoUrl) {
}
