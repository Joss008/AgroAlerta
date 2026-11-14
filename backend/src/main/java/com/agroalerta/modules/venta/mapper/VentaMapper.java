package com.agroalerta.modules.venta.mapper;

import com.agroalerta.modules.venta.dto.VentaResponse;
import com.agroalerta.modules.venta.entity.Venta;

public final class VentaMapper {

    private VentaMapper() {
    }

    public static VentaResponse toResponse(Venta venta) {
        return new VentaResponse(
                venta.getId(),
                venta.getIdCosecha(),
                venta.getFechaVenta(),
                venta.getCantidadKg(),
                venta.getPrecioTotal(),
                venta.getComprador(),
                venta.getFechaCosecha(),
                venta.getNombreCampana());
    }
}
