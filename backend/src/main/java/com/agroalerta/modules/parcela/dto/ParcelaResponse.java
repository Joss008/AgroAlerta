package com.agroalerta.modules.parcela.dto;

import java.math.BigDecimal;

public record ParcelaResponse(
        Long id,
        Long idAgricultor,
        String nombre,
        BigDecimal areaHectareas,
        String ubicacion,
        String nombreAgricultor,
        String fotoAgricultor) {
}
