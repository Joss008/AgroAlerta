package com.agroalerta.modules.agricultor.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.agricultor.dto.AgricultorRequest;
import com.agroalerta.modules.agricultor.entity.Agricultor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Acceso a la tabla `agricultor` vía API REST de Supabase. */
@Repository
public class AgricultorRestRepository {

    private static final String PATH = "/agricultor";
    private static final String COLUMNS = "id_agricultor,nombres,apellidos,telefono";

    private final SupabaseRestClient client;

    public AgricultorRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Agricultor> search(String q, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .order("apellidos", false)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("nombres.ilike.*" + safe + "*,apellidos.ilike.*" + safe + "*");
        }
        SupabasePage<Agricultor> result = client.getPage(PATH, query.build(), Agricultor.class);
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Agricultor> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_agricultor", id).limit(1);
        Agricultor[] rows = client.getArray(PATH, query.build(), Agricultor.class);
        return rows.length == 0 ? Optional.empty() : Optional.of(rows[0]);
    }

    public Agricultor insert(AgricultorRequest request) {
        return client.post(PATH, Map.of(), toBody(request), Agricultor.class);
    }

    public Agricultor update(Long id, AgricultorRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_agricultor", id);
        return client.patch(PATH, query.build(), toBody(request), Agricultor.class);
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_agricultor", id).build());
    }

    public long countParcelasByAgricultor(Long id) {
        return client.count("/parcela", SupabaseQuery.of().eq("id_agricultor", id).build());
    }

    private Map<String, Object> toBody(AgricultorRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombres", request.nombres().trim());
        body.put("apellidos", request.apellidos().trim());
        body.put("telefono", blankToNull(request.telefono()));
        return body;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
