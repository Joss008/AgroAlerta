package com.agroalerta.modules.dashboard.repository;

import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import org.springframework.stereotype.Repository;

/**
 * Consultas agregadas del dashboard sobre las ocho tablas del modelo.
 * Solo lectura: no modifica datos. Agrega en Java sobre la API REST de Supabase.
 */
@Repository
public class DashboardQueryRepository {

    private static final int PAGE_SIZE = 1000;

    private final SupabaseRestClient client;

    public DashboardQueryRepository(SupabaseRestClient client) {
        this.client = client;
    }

    // ------------------------------------------------------------- conteos

    public long countAgricultores() {
        return client.count("/agricultor", Map.of());
    }

    public long countParcelas() {
        return client.count("/parcela", Map.of());
    }

    public long countCultivos() {
        return client.count("/cultivo", Map.of());
    }

    public long countCampanas() {
        return client.count("/campana", Map.of());
    }

    public long countCampanasActivas() {
        SupabaseQuery query = SupabaseQuery.of()
                .select("id_campana")
                .or("fecha_fin.is.null,fecha_fin.gte." + LocalDate.now());
        return client.count("/campana", query.build());
    }

    // --------------------------------------------------------------- sumas

    public BigDecimal sumAreaHectareas() {
        return sum("/parcela", SupabaseQuery.of().select("area_hectareas"),
                ParcelaRow.class, r -> r.areaHectareas);
    }

    public BigDecimal sumProduccionKg() {
        return sum("/cosecha", SupabaseQuery.of().select("cantidad_kg"),
                CosechaRow.class, r -> r.cantidadKg);
    }

    public BigDecimal sumVentasSoles() {
        return sum("/venta", SupabaseQuery.of().select("precio_total"),
                VentaRow.class, r -> r.precioTotal);
    }

    public BigDecimal sumGastosSoles() {
        return sum("/gasto", SupabaseQuery.of().select("monto"),
                GastoRow.class, r -> r.monto);
    }

    public BigDecimal sumAguaM3() {
        return sum("/riego", SupabaseQuery.of().select("cantidad_m3"),
                RiegoRow.class, r -> r.cantidadM3);
    }

    // ---------------------------------------------------------- agregados

    /** Base de todas las campañas con datos de parcela y cultivo resueltos. */
    public List<CampanaBase> campanasBase() {
        List<CampanaRow> campanas = fetchAll("/campana",
                SupabaseQuery.of().select("id_campana,nombre,id_parcela,id_cultivo")
                        .order("fecha_inicio", true),
                CampanaRow.class);
        if (campanas.isEmpty()) {
            return List.of();
        }

        Map<Long, ParcelaRow> parcelas = parcelasPorIds(campanas);
        Map<Long, CultivoRow> cultivos = cultivosPorIds(campanas);

        List<CampanaBase> result = new ArrayList<>();
        for (CampanaRow campana : campanas) {
            ParcelaRow parcela = parcelas.get(campana.idParcela);
            CultivoRow cultivo = cultivos.get(campana.idCultivo);
            result.add(new CampanaBase(
                    campana.id,
                    campana.nombre,
                    cultivo == null ? null : cultivo.nombre,
                    parcela == null ? null : parcela.nombre,
                    parcela == null || parcela.areaHectareas == null
                            ? BigDecimal.ZERO : parcela.areaHectareas));
        }
        return result;
    }

    /** Suma de cosechas por campaña. */
    public Map<Long, BigDecimal> produccionPorCampana() {
        Map<Long, BigDecimal> result = new java.util.LinkedHashMap<>();
        for (CosechaRow row : fetchAll("/cosecha",
                SupabaseQuery.of().select("id_campana,cantidad_kg"), CosechaRow.class)) {
            if (row.idCampana == null) {
                continue;
            }
            result.merge(row.idCampana, decimal(row.cantidadKg), BigDecimal::add);
        }
        return result;
    }

    /** Ventas acumuladas por campaña (kg y soles). */
    public List<VentaAcum> ventasPorCampana() {
        List<VentaRow> ventas = fetchAll("/venta",
                SupabaseQuery.of().select("id_cosecha,cantidad_kg,precio_total"), VentaRow.class);
        Map<Long, Long> campanaDeCosecha = campanaPorCosecha();

        Map<Long, BigDecimal> cantidad = new java.util.LinkedHashMap<>();
        Map<Long, BigDecimal> monto = new java.util.LinkedHashMap<>();
        for (VentaRow venta : ventas) {
            Long idCampana = campanaDeCosecha.get(venta.idCosecha);
            if (idCampana == null) {
                continue;
            }
            cantidad.merge(idCampana, decimal(venta.cantidadKg), BigDecimal::add);
            monto.merge(idCampana, decimal(venta.precioTotal), BigDecimal::add);
        }

        List<VentaAcum> result = new ArrayList<>();
        for (Long idCampana : cantidad.keySet()) {
            result.add(new VentaAcum(idCampana, cantidad.get(idCampana), monto.get(idCampana)));
        }
        return result;
    }

    /** Gasto total por campaña. */
    public List<GastoAcum> gastosPorCampana() {
        Map<Long, String> nombres = campanaNombres();
        Map<Long, BigDecimal> montos = new java.util.LinkedHashMap<>();
        for (GastoRow row : fetchAll("/gasto",
                SupabaseQuery.of().select("id_campana,monto"), GastoRow.class)) {
            if (row.idCampana != null) {
                montos.merge(row.idCampana, decimal(row.monto), BigDecimal::add);
            }
        }
        List<GastoAcum> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : montos.entrySet()) {
            result.add(new GastoAcum(entry.getKey(), nombres.get(entry.getKey()), entry.getValue()));
        }
        return result;
    }

    /** Gasto total por concepto, de mayor a menor. */
    public List<GastoConcepto> gastosPorConcepto() {
        Map<String, BigDecimal> montos = new java.util.LinkedHashMap<>();
        for (GastoRow row : fetchAll("/gasto",
                SupabaseQuery.of().select("concepto,monto"), GastoRow.class)) {
            if (row.concepto != null) {
                montos.merge(row.concepto, decimal(row.monto), BigDecimal::add);
            }
        }
        return montos.entrySet().stream()
                .map(entry -> new GastoConcepto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.montoSoles().compareTo(a.montoSoles()))
                .toList();
    }

    /** Agua regada por campaña. */
    public Map<Long, BigDecimal> aguaPorCampana() {
        Map<Long, BigDecimal> result = new java.util.LinkedHashMap<>();
        for (RiegoRow row : fetchAll("/riego",
                SupabaseQuery.of().select("id_campana,cantidad_m3"), RiegoRow.class)) {
            if (row.idCampana != null) {
                result.merge(row.idCampana, decimal(row.cantidadM3), BigDecimal::add);
            }
        }
        return result;
    }

    // ---------------------------------------------------------- resultados

    public record CampanaBase(
            Long idCampana,
            String nombreCampana,
            String nombreCultivo,
            String nombreParcela,
            BigDecimal areaHectareas) {
    }

    public record VentaAcum(Long idCampana, BigDecimal cantidadKg, BigDecimal montoSoles) {
    }

    public record GastoAcum(Long idCampana, String nombreCampana, BigDecimal montoSoles) {
    }

    public record GastoConcepto(String concepto, BigDecimal montoSoles) {
    }

    // ------------------------------------------------------------- apoyo

    private Map<Long, ParcelaRow> parcelasPorIds(List<CampanaRow> campanas) {
        String ids = ids(campanas.stream().map(c -> c.idParcela));
        if (ids.isEmpty()) {
            return Map.of();
        }
        Map<Long, ParcelaRow> result = new java.util.LinkedHashMap<>();
        for (ParcelaRow row : client.getArray("/parcela",
                SupabaseQuery.of().select("id_parcela,nombre,area_hectareas")
                        .in("id_parcela", ids).build(), ParcelaRow.class)) {
            result.put(row.id, row);
        }
        return result;
    }

    private Map<Long, CultivoRow> cultivosPorIds(List<CampanaRow> campanas) {
        String ids = ids(campanas.stream().map(c -> c.idCultivo));
        if (ids.isEmpty()) {
            return Map.of();
        }
        Map<Long, CultivoRow> result = new java.util.LinkedHashMap<>();
        for (CultivoRow row : client.getArray("/cultivo",
                SupabaseQuery.of().select("id_cultivo,nombre").in("id_cultivo", ids).build(),
                CultivoRow.class)) {
            result.put(row.id, row);
        }
        return result;
    }

    private Map<Long, Long> campanaPorCosecha() {
        Map<Long, Long> result = new java.util.LinkedHashMap<>();
        for (CosechaRow row : fetchAll("/cosecha",
                SupabaseQuery.of().select("id_cosecha,id_campana"), CosechaRow.class)) {
            if (row.id != null && row.idCampana != null) {
                result.put(row.id, row.idCampana);
            }
        }
        return result;
    }

    private Map<Long, String> campanaNombres() {
        Map<Long, String> result = new java.util.LinkedHashMap<>();
        for (CampanaRow row : fetchAll("/campana",
                SupabaseQuery.of().select("id_campana,nombre"), CampanaRow.class)) {
            result.put(row.id, row.nombre);
        }
        return result;
    }

    private String ids(java.util.stream.Stream<Long> values) {
        return values.filter(java.util.Objects::nonNull).distinct()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
    }

    private <T> BigDecimal sum(String path, SupabaseQuery query, Class<T> type,
                               Function<T, BigDecimal> getter) {
        BigDecimal total = BigDecimal.ZERO;
        for (T row : fetchAll(path, query, type)) {
            BigDecimal value = getter.apply(row);
            if (value != null) {
                total = total.add(value);
            }
        }
        return total;
    }

    /** Recorre todas las páginas (PostgREST limita por defecto a 1000 filas). */
    private <T> List<T> fetchAll(String path, SupabaseQuery query, Class<T> type) {
        List<T> all = new ArrayList<>();
        int offset = 0;
        while (true) {
            query.range(offset, offset + PAGE_SIZE - 1);
            T[] batch = client.getArray(path, query.build(), type);
            all.addAll(List.of(batch));
            if (batch.length < PAGE_SIZE) {
                return all;
            }
            offset += PAGE_SIZE;
        }
    }

    private BigDecimal decimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    // --------------------------------------------------------- filas JSON

    @Getter
    private static final class CampanaRow {
        @JsonProperty("id_campana")
        private Long id;
        @JsonProperty("nombre")
        private String nombre;
        @JsonProperty("id_parcela")
        private Long idParcela;
        @JsonProperty("id_cultivo")
        private Long idCultivo;
    }

    @Getter
    private static final class ParcelaRow {
        @JsonProperty("id_parcela")
        private Long id;
        @JsonProperty("nombre")
        private String nombre;
        @JsonProperty("area_hectareas")
        private BigDecimal areaHectareas;
    }

    @Getter
    private static final class CultivoRow {
        @JsonProperty("id_cultivo")
        private Long id;
        @JsonProperty("nombre")
        private String nombre;
    }

    @Getter
    private static final class CosechaRow {
        @JsonProperty("id_cosecha")
        private Long id;
        @JsonProperty("id_campana")
        private Long idCampana;
        @JsonProperty("cantidad_kg")
        private BigDecimal cantidadKg;
    }

    @Getter
    private static final class VentaRow {
        @JsonProperty("id_cosecha")
        private Long idCosecha;
        @JsonProperty("cantidad_kg")
        private BigDecimal cantidadKg;
        @JsonProperty("precio_total")
        private BigDecimal precioTotal;
    }

    @Getter
    private static final class GastoRow {
        @JsonProperty("id_campana")
        private Long idCampana;
        @JsonProperty("concepto")
        private String concepto;
        @JsonProperty("monto")
        private BigDecimal monto;
    }

    @Getter
    private static final class RiegoRow {
        @JsonProperty("id_campana")
        private Long idCampana;
        @JsonProperty("cantidad_m3")
        private BigDecimal cantidadM3;
    }
}
