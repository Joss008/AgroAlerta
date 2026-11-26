package com.agroalerta.modules.agricultor.mapper;

import com.agroalerta.modules.agricultor.dto.AgricultorResponse;
import com.agroalerta.modules.agricultor.entity.Agricultor;

public final class AgricultorMapper {

    private AgricultorMapper() {
    }

    public static AgricultorResponse toResponse(Agricultor agricultor) {
        return new AgricultorResponse(
                agricultor.getId(),
                agricultor.getNombres(),
                agricultor.getApellidos(),
                agricultor.getTelefono(),
                agricultor.getDni(),
                agricultor.getFotoUrl());
    }
}
