package com.agroalerta.modules.gasto.mapper;

import com.agroalerta.modules.gasto.dto.GastoResponse;
import com.agroalerta.modules.gasto.entity.Gasto;

public final class GastoMapper {

    private GastoMapper() {
    }

    public static GastoResponse toResponse(Gasto gasto) {
        return new GastoResponse(
                gasto.getId(),
                gasto.getIdCampana(),
                gasto.getConcepto(),
                gasto.getFechaGasto(),
                gasto.getMonto(),
                gasto.getNombreCampana());
    }
}
