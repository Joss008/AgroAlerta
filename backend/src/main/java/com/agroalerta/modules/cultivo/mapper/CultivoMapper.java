package com.agroalerta.modules.cultivo.mapper;

import com.agroalerta.modules.cultivo.dto.CultivoResponse;
import com.agroalerta.modules.cultivo.entity.Cultivo;

public final class CultivoMapper {

    private CultivoMapper() {
    }

    public static CultivoResponse toResponse(Cultivo cultivo) {
        return new CultivoResponse(
                cultivo.getId(),
                cultivo.getNombre(),
                cultivo.getVariedad(),
                cultivo.getFotoUrl());
    }
}
