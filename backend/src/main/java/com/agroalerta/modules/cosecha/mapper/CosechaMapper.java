package com.agroalerta.modules.cosecha.mapper;

import com.agroalerta.modules.cosecha.dto.CosechaResponse;
import com.agroalerta.modules.cosecha.entity.Cosecha;

public final class CosechaMapper {

    private CosechaMapper() {
    }

    public static CosechaResponse toResponse(Cosecha cosecha) {
        return new CosechaResponse(
                cosecha.getId(),
                cosecha.getIdCampana(),
                cosecha.getFechaCosecha(),
                cosecha.getCantidadKg(),
                cosecha.getObservacion(),
                cosecha.getNombreCampana());
    }
}
