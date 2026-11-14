package com.agroalerta.modules.dashboard.controller;

import com.agroalerta.modules.dashboard.dto.GastosResumen;
import com.agroalerta.modules.dashboard.dto.ProduccionRow;
import com.agroalerta.modules.dashboard.dto.ResumenResponse;
import com.agroalerta.modules.dashboard.dto.RentabilidadRow;
import com.agroalerta.modules.dashboard.dto.VentasRow;
import com.agroalerta.modules.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Indicadores y métricas del negocio")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "Resumen general de indicadores")
    @GetMapping("/resumen")
    public ResumenResponse resumen() {
        return service.resumen();
    }

    @Operation(summary = "Producción y rendimiento por campaña")
    @GetMapping("/produccion")
    public List<ProduccionRow> produccion() {
        return service.produccion();
    }

    @Operation(summary = "Ventas por campaña")
    @GetMapping("/ventas")
    public List<VentasRow> ventas() {
        return service.ventas();
    }

    @Operation(summary = "Gastos por campaña y por concepto")
    @GetMapping("/gastos")
    public GastosResumen gastos() {
        return service.gastos();
    }

    @Operation(summary = "Rentabilidad por campaña (ventas - gastos)")
    @GetMapping("/rentabilidad")
    public List<RentabilidadRow> rentabilidad() {
        return service.rentabilidad();
    }
}
