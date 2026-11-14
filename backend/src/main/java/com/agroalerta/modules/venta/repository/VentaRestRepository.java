package com.agroalerta.modules.venta.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.venta.dto.VentaRequest;
import com.agroalerta.modules.venta.entity.Venta;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

/** Acceso a la tabla `venta` vía API REST de Supabase. */
@Repository
public class VentaRestRepository {

    private static final String PATH = "/venta";
    private static final String COLUMNS = "id_venta,id_cosecha,fecha_venta,cantidad_kg,precio_total,comprador";

    private final SupabaseRestClient client;

    public VentaRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Venta> search(String q, Long idCosecha, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .eq("id_cosecha", idCosecha)
                .order("fecha_venta", true)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("comprador.ilike.*" + safe + "*");
        }
        SupabasePage<Venta> result = client.getPage(PATH, query.build(), Venta.class);
        rellenarCosechaCampana(result.content());
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Venta> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_venta", id).limit(1);
        Venta[] rows = client.getArray(PATH, query.build(), Venta.class);
        if (rows.length == 0) {
            return Optional.empty();
        }
        rellenarCosechaCampana(List.of(rows));
        return Optional.of(rows[0]);
    }

    public Venta insert(VentaRequest request) {
        Venta venta = client.post(PATH, Map.of(), toBody(request), Venta.class);
        rellenarCosechaCampana(List.of(venta));
        return venta;
    }

    public Venta update(Long id, VentaRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_venta", id);
        Venta venta = client.patch(PATH, query.build(), toBody(request), Venta.class);
        rellenarCosechaCampana(List.of(venta));
        return venta;
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_venta", id).build());
    }

    /** Kilogramos vendidos acumulados de una cosecha (suma en Java de las filas). */
    public BigDecimal sumCantidadKgByCosecha(Long idCosecha) {
        Map<String, String> params = SupabaseQuery.of()
                .select("cantidad_kg")
                .eq("id_cosecha", idCosecha)
                .build();
        VentaCantidad[] rows = client.getArray(PATH, params, VentaCantidad.class);
        BigDecimal total = BigDecimal.ZERO;
        for (VentaCantidad row : rows) {
            if (row.cantidadKg != null) {
                total = total.add(row.cantidadKg);
            }
        }
        return total;
    }

    /** Número de ventas registradas de una cosecha. */
    public long countByCosecha(Long idCosecha) {
        return client.count(PATH, SupabaseQuery.of().eq("id_cosecha", idCosecha).build());
    }

    /**
     * Rellena los campos transitorios de `venta` (fecha de cosecha y nombre de
     * campaña) con una petición extra a `cosecha` y otra a `campana`.
     */
    private void rellenarCosechaCampana(List<Venta> rows) {
        List<Long> idsCosecha = rows.stream().map(Venta::getIdCosecha)
                .filter(Objects::nonNull).distinct().toList();
        if (idsCosecha.isEmpty()) {
            return;
        }
        Map<String, String> params = SupabaseQuery.of()
                .select("id_cosecha,fecha_cosecha,id_campana")
                .in("id_cosecha", idsCosecha.stream().map(String::valueOf)
                        .collect(Collectors.joining(",")))
                .build();
        CosechaRef[] refs = client.getArray("/cosecha", params, CosechaRef.class);
        Map<Long, CosechaRef> porId = new HashMap<>();
        for (CosechaRef ref : refs) {
            porId.put(ref.id, ref);
        }

        Set<Long> idsCampana = new LinkedHashSet<>();
        for (Venta venta : rows) {
            CosechaRef ref = porId.get(venta.getIdCosecha());
            if (ref != null) {
                venta.setFechaCosecha(ref.fechaCosecha);
                if (ref.idCampana != null) {
                    idsCampana.add(ref.idCampana);
                }
            }
        }
        if (idsCampana.isEmpty()) {
            return;
        }
        Map<String, String> campanaParams = SupabaseQuery.of()
                .select("id_campana,nombre")
                .in("id_campana", idsCampana.stream().map(String::valueOf)
                        .collect(Collectors.joining(",")))
                .build();
        CampanaRef[] campanas = client.getArray("/campana", campanaParams, CampanaRef.class);
        Map<Long, String> nombres = new HashMap<>();
        for (CampanaRef ref : campanas) {
            nombres.put(ref.id, ref.nombre);
        }
        for (Venta venta : rows) {
            CosechaRef ref = porId.get(venta.getIdCosecha());
            if (ref != null) {
                venta.setNombreCampana(nombres.get(ref.idCampana));
            }
        }
    }

    private Map<String, Object> toBody(VentaRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id_cosecha", request.idCosecha());
        body.put("fecha_venta", request.fechaVenta());
        body.put("cantidad_kg", request.cantidadKg());
        body.put("precio_total", request.precioTotal());
        body.put("comprador", request.comprador().trim());
        return body;
    }

    /** Fila mínima de `cosecha` para resolver campos transitorios (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class CosechaRef {
        @JsonProperty("id_cosecha") private Long id;
        @JsonProperty("fecha_cosecha") private LocalDate fechaCosecha;
        @JsonProperty("id_campana") private Long idCampana;
    }

    /** Fila mínima para resolver el nombre mostrado (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class CampanaRef {
        @JsonProperty("id_campana") private Long id;
        @JsonProperty("nombre") private String nombre;
    }

    /** Fila mínima con la cantidad vendida para sumar en Java (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class VentaCantidad {
        @JsonProperty("cantidad_kg") private BigDecimal cantidadKg;
    }
}
