package com.agroalerta.modules.riego.mapper;

import com.agroalerta.modules.riego.dto.RiegoResponse;
import com.agroalerta.modules.riego.entity.Riego;

public final class RiegoMapper {

    private RiegoMapper() {
    }

    public static RiegoResponse toResponse(Riego riego) {
        return new RiegoResponse(
                riego.getId(),
                riego.getIdCampana(),
                riego.getFechaRiego(),
                riego.getCantidadM3(),
                riego.getObservacion(),
                riego.getNombreCampana());
    }
}
