package com.agroalerta.modules.campana.mapper;

import com.agroalerta.modules.campana.dto.CampanaResponse;
import com.agroalerta.modules.campana.entity.Campana;

public final class CampanaMapper {

    private CampanaMapper() {
    }

    public static CampanaResponse toResponse(Campana campana) {
        return new CampanaResponse(
                campana.getId(),
                campana.getIdParcela(),
                campana.getIdCultivo(),
                campana.getNombre(),
                campana.getFechaInicio(),
                campana.getFechaFin(),
                campana.getNombreParcela(),
                campana.getNombreCultivo());
    }
}
