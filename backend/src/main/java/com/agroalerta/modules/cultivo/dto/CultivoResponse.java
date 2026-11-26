package com.agroalerta.modules.cultivo.dto;

public record CultivoResponse(
        Long id,
        String nombre,
        String variedad,
        String fotoUrl) {
}
