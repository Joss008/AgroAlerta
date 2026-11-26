package com.agroalerta.modules.cultivo.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.cultivo.dto.CultivoRequest;
import com.agroalerta.modules.cultivo.entity.Cultivo;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Acceso a la tabla `cultivo` vía API REST de Supabase. */
@Repository
public class CultivoRestRepository {

    private static final String PATH = "/cultivo";
    private static final String COLUMNS = "id_cultivo,nombre,variedad,foto_url";

    private final SupabaseRestClient client;

    public CultivoRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Cultivo> search(String q, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .order("nombre", false)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("nombre.ilike.*" + safe + "*,variedad.ilike.*" + safe + "*");
        }
        SupabasePage<Cultivo> result = client.getPage(PATH, query.build(), Cultivo.class);
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Cultivo> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_cultivo", id).limit(1);
        Cultivo[] rows = client.getArray(PATH, query.build(), Cultivo.class);
        return rows.length == 0 ? Optional.empty() : Optional.of(rows[0]);
    }

    public Cultivo insert(CultivoRequest request) {
        return client.post(PATH, Map.of(), toBody(request), Cultivo.class);
    }

    public Cultivo update(Long id, CultivoRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_cultivo", id);
        return client.patch(PATH, query.build(), toBody(request), Cultivo.class);
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_cultivo", id).build());
    }

    public long countCampanasByCultivo(Long id) {
        return client.count("/campana", SupabaseQuery.of().eq("id_cultivo", id).build());
    }

    private Map<String, Object> toBody(CultivoRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", request.nombre().trim());
        body.put("variedad", blankToNull(request.variedad()));
        body.put("foto_url", blankToNull(request.fotoUrl()));
        return body;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
