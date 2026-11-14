package com.agroalerta.modules.riego.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.riego.dto.RiegoRequest;
import com.agroalerta.modules.riego.entity.Riego;
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

/** Acceso a la tabla `riego` vía API REST de Supabase. */
@Repository
public class RiegoRestRepository {

    private static final String PATH = "/riego";
    private static final String COLUMNS = "id_riego,id_campana,fecha_riego,cantidad_m3,observacion";

    private final SupabaseRestClient client;

    public RiegoRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Riego> search(String q, Long idCampana, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .eq("id_campana", idCampana)
                .order("fecha_riego", true)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            query.ilike("observacion", q);
        }
        SupabasePage<Riego> result = client.getPage(PATH, query.build(), Riego.class);
        rellenarNombresCampanas(result.content());
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Riego> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_riego", id).limit(1);
        Riego[] rows = client.getArray(PATH, query.build(), Riego.class);
        if (rows.length == 0) {
            return Optional.empty();
        }
        rellenarNombresCampanas(List.of(rows[0]));
        return Optional.of(rows[0]);
    }

    public Riego insert(RiegoRequest request) {
        Riego riego = client.post(PATH, Map.of(), toBody(request), Riego.class);
        rellenarNombresCampanas(List.of(riego));
        return riego;
    }

    public Riego update(Long id, RiegoRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_riego", id);
        Riego riego = client.patch(PATH, query.build(), toBody(request), Riego.class);
        rellenarNombresCampanas(List.of(riego));
        return riego;
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_riego", id).build());
    }

    private void rellenarNombresCampanas(List<Riego> rows) {
        List<Long> ids = rows.stream().map(Riego::getIdCampana)
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

    private Map<String, Object> toBody(RiegoRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id_campana", request.idCampana());
        body.put("fecha_riego", request.fechaRiego());
        body.put("cantidad_m3", request.cantidadM3());
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
        @JsonProperty("id_campana")
        private Long id;
        @JsonProperty("nombre")
        private String nombre;
    }
}
