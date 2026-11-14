package com.agroalerta.modules.gasto.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.gasto.dto.GastoRequest;
import com.agroalerta.modules.gasto.entity.Gasto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

/** Acceso a la tabla `gasto` vía API REST de Supabase. */
@Repository
public class GastoRestRepository {

    private static final String PATH = "/gasto";
    private static final String COLUMNS = "id_gasto,id_campana,concepto,fecha_gasto,monto";

    private final SupabaseRestClient client;

    public GastoRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Gasto> search(String q, Long idCampana, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .eq("id_campana", idCampana)
                .order("fecha_gasto", true)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("concepto.ilike.*" + safe + "*");
        }
        SupabasePage<Gasto> result = client.getPage(PATH, query.build(), Gasto.class);
        rellenarNombresCampanas(result.content());
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Gasto> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_gasto", id).limit(1);
        Gasto[] rows = client.getArray(PATH, query.build(), Gasto.class);
        if (rows.length == 0) {
            return Optional.empty();
        }
        rellenarNombresCampanas(List.of(rows));
        return Optional.of(rows[0]);
    }

    public Gasto insert(GastoRequest request) {
        Gasto gasto = client.post(PATH, Map.of(), toBody(request), Gasto.class);
        rellenarNombresCampanas(List.of(gasto));
        return gasto;
    }

    public Gasto update(Long id, GastoRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_gasto", id);
        Gasto gasto = client.patch(PATH, query.build(), toBody(request), Gasto.class);
        rellenarNombresCampanas(List.of(gasto));
        return gasto;
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_gasto", id).build());
    }

    public boolean campanaExists(Long idCampana) {
        return client.exists("/campana", SupabaseQuery.of().eq("id_campana", idCampana).build());
    }

    private void rellenarNombresCampanas(List<Gasto> rows) {
        List<Long> ids = rows.stream().map(Gasto::getIdCampana)
                .filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<String, String> params = SupabaseQuery.of()
                .select("id_campana,nombre")
                .in("id_campana", ids.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
        CampanaRef[] refs = client.getArray("/campana", params, CampanaRef.class);
        Map<Long, String> nombres = new HashMap<>();
        for (CampanaRef ref : refs) {
            nombres.put(ref.id, ref.nombre);
        }
        rows.forEach(r -> r.setNombreCampana(nombres.get(r.getIdCampana())));
    }

    private Map<String, Object> toBody(GastoRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id_campana", request.idCampana());
        body.put("concepto", request.concepto().trim());
        body.put("fecha_gasto", request.fechaGasto());
        body.put("monto", request.monto());
        return body;
    }

    /** Fila mínima para resolver el nombre mostrado (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class CampanaRef {
        @JsonProperty("id_campana") private Long id;
        @JsonProperty("nombre") private String nombre;
    }
}
