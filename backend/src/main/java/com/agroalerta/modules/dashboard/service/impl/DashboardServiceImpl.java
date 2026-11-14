package com.agroalerta.modules.dashboard.service.impl;

import com.agroalerta.modules.dashboard.dto.GastosResumen;
import com.agroalerta.modules.dashboard.dto.ProduccionRow;
import com.agroalerta.modules.dashboard.dto.ResumenResponse;
import com.agroalerta.modules.dashboard.dto.RentabilidadRow;
import com.agroalerta.modules.dashboard.dto.VentasRow;
import com.agroalerta.modules.dashboard.repository.DashboardQueryRepository;
import com.agroalerta.modules.dashboard.service.DashboardService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final int SCALE = 2;

    private final DashboardQueryRepository query;

    public DashboardServiceImpl(DashboardQueryRepository query) {
        this.query = query;
    }

    @Override
    public ResumenResponse resumen() {
        BigDecimal ventas = decimal(query.sumVentasSoles());
        BigDecimal gastos = decimal(query.sumGastosSoles());
        return new ResumenResponse(
                query.countAgricultores(),
                query.countParcelas(),
                query.countCultivos(),
                query.countCampanas(),
                query.countCampanasActivas(),
                decimal(query.sumAreaHectareas()),
                decimal(query.sumProduccionKg()),
                ventas,
                gastos,
                ventas.subtract(gastos),
                decimal(query.sumAguaM3()));
    }

    @Override
    public List<ProduccionRow> produccion() {
        Map<Long, BigDecimal> produccion = query.produccionPorCampana();
        List<ProduccionRow> rows = new ArrayList<>();
        for (DashboardQueryRepository.CampanaBase campana : query.campanasBase()) {
            BigDecimal total = produccion.getOrDefault(campana.idCampana(), BigDecimal.ZERO);
            rows.add(new ProduccionRow(
                    campana.idCampana(),
                    campana.nombreCampana(),
                    campana.nombreCultivo(),
                    campana.nombreParcela(),
                    decimal(campana.areaHectareas()),
                    scale(total),
                    division(total, campana.areaHectareas())));
        }
        return rows;
    }

    @Override
    public List<VentasRow> ventas() {
        Map<Long, DashboardQueryRepository.VentaAcum> porCampana = new java.util.LinkedHashMap<>();
        for (DashboardQueryRepository.VentaAcum venta : query.ventasPorCampana()) {
            porCampana.put(venta.idCampana(), venta);
        }
        List<VentasRow> rows = new ArrayList<>();
        for (DashboardQueryRepository.CampanaBase campana : query.campanasBase()) {
            DashboardQueryRepository.VentaAcum venta = porCampana.get(campana.idCampana());
            BigDecimal cantidad = venta == null ? BigDecimal.ZERO : decimal(venta.cantidadKg());
            BigDecimal monto = venta == null ? BigDecimal.ZERO : decimal(venta.montoSoles());
            BigDecimal promedio = cantidad.compareTo(BigDecimal.ZERO) > 0
                    ? monto.divide(cantidad, SCALE, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
            rows.add(new VentasRow(campana.idCampana(), campana.nombreCampana(),
                    scale(cantidad), scale(monto), promedio));
        }
        return rows;
    }

    @Override
    public GastosResumen gastos() {
        List<GastosResumen.GastoCampanaRow> porCampana = query.gastosPorCampana().stream()
                .map(row -> new GastosResumen.GastoCampanaRow(
                        row.idCampana(), row.nombreCampana(), scale(decimal(row.montoSoles()))))
                .toList();
        List<GastosResumen.GastoConceptoRow> porConcepto = query.gastosPorConcepto().stream()
                .map(row -> new GastosResumen.GastoConceptoRow(
                        row.concepto(), scale(decimal(row.montoSoles()))))
                .toList();
        return new GastosResumen(porCampana, porConcepto);
    }

    @Override
    public List<RentabilidadRow> rentabilidad() {
        Map<Long, BigDecimal> produccion = query.produccionPorCampana();
        Map<Long, BigDecimal> agua = query.aguaPorCampana();

        Map<Long, BigDecimal> ventas = new java.util.LinkedHashMap<>();
        for (DashboardQueryRepository.VentaAcum row : query.ventasPorCampana()) {
            ventas.put(row.idCampana(), decimal(row.montoSoles()));
        }
        Map<Long, BigDecimal> gastos = new java.util.LinkedHashMap<>();
        for (DashboardQueryRepository.GastoAcum row : query.gastosPorCampana()) {
            gastos.put(row.idCampana(), decimal(row.montoSoles()));
        }

        List<RentabilidadRow> rows = new ArrayList<>();
        for (DashboardQueryRepository.CampanaBase campana : query.campanasBase()) {
            BigDecimal totalProduccion = produccion.getOrDefault(campana.idCampana(), BigDecimal.ZERO);
            BigDecimal totalVentas = ventas.getOrDefault(campana.idCampana(), BigDecimal.ZERO);
            BigDecimal totalGastos = gastos.getOrDefault(campana.idCampana(), BigDecimal.ZERO);
            rows.add(new RentabilidadRow(
                    campana.idCampana(),
                    campana.nombreCampana(),
                    scale(totalProduccion),
                    scale(totalVentas),
                    scale(totalGastos),
                    scale(totalVentas.subtract(totalGastos)),
                    division(totalProduccion, campana.areaHectareas()),
                    scale(agua.getOrDefault(campana.idCampana(), BigDecimal.ZERO))));
        }
        return rows;
    }

    private BigDecimal division(BigDecimal numerador, BigDecimal area) {
        if (area == null || area.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        return numerador.divide(area, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal scale(BigDecimal value) {
        return decimal(value).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal decimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
