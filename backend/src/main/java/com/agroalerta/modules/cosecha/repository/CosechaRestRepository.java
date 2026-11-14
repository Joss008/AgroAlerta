package com.agroalerta.modules.cosecha.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.cosecha.dto.CosechaRequest;
import com.agroalerta.modules.cosecha.entity.Cosecha;
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

/** Acceso a la tabla `cosecha` vía API REST de Supabase. */
@Repository
public class CosechaRestRepository {

    private static final String PATH = "/cosecha";
    private static final String COLUMNS = "id_cosecha,id_campana,fecha_cosecha,cantidad_kg,observacion";

    private final SupabaseRestClient client;

    public CosechaRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Cosecha> search(String q, Long idCampana, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .eq("id_campana", idCampana)
                .order("fecha_cosecha", true)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("observacion.ilike.*" + safe + "*");
        }
        SupabasePage<Cosecha> result = client.getPage(PATH, query.build(), Cosecha.class);
        rellenarNombresCampanas(result.content());
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Cosecha> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_cosecha", id).limit(1);
        Cosecha[] rows = client.getArray(PATH, query.build(), Cosecha.class);
        if (rows.length == 0) {
            return Optional.empty();
        }
        rellenarNombresCampanas(List.of(rows));
        return Optional.of(rows[0]);
    }

    public Cosecha insert(CosechaRequest request) {
        Cosecha cosecha = client.post(PATH, Map.of(), toBody(request), Cosecha.class);
        rellenarNombresCampanas(List.of(cosecha));
        return cosecha;
    }

    public Cosecha update(Long id, CosechaRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_cosecha", id);
        Cosecha cosecha = client.patch(PATH, query.build(), toBody(request), Cosecha.class);
        rellenarNombresCampanas(List.of(cosecha));
        return cosecha;
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_cosecha", id).build());
    }

    public boolean campanaExists(Long idCampana) {
        return client.exists("/campana", SupabaseQuery.of().eq("id_campana", idCampana).build());
    }

    private void rellenarNombresCampanas(List<Cosecha> rows) {
        List<Long> ids = rows.stream().map(Cosecha::getIdCampana)
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

    private Map<String, Object> toBody(CosechaRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id_campana", request.idCampana());
        body.put("fecha_cosecha", request.fechaCosecha());
        body.put("cantidad_kg", request.cantidadKg());
        body.put("observacion", blankToNull(request.observacion()));
        return body;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    /** Fila mínima para resolver el nombre mostrado (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class CampanaRef {
        @JsonProperty("id_campana") private Long id;
        @JsonProperty("nombre") private String nombre;
    }
}
