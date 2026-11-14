package com.agroalerta.modules.parcela.mapper;

import com.agroalerta.modules.parcela.dto.ParcelaResponse;
import com.agroalerta.modules.parcela.entity.Parcela;

public final class ParcelaMapper {

    private ParcelaMapper() {
    }

    public static ParcelaResponse toResponse(Parcela parcela) {
        return new ParcelaResponse(
                parcela.getId(),
                parcela.getIdAgricultor(),
                parcela.getNombre(),
                parcela.getAreaHectareas(),
                parcela.getUbicacion(),
                parcela.getNombreAgricultor());
    }
}
