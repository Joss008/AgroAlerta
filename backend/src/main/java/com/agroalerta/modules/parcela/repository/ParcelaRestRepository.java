package com.agroalerta.modules.parcela.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.parcela.dto.ParcelaRequest;
import com.agroalerta.modules.parcela.entity.Parcela;
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

/** Acceso a la tabla `parcela` vía API REST de Supabase. */
@Repository
public class ParcelaRestRepository {

    private static final String PATH = "/parcela";
    private static final String COLUMNS = "id_parcela,id_agricultor,nombre,area_hectareas,ubicacion";

    private final SupabaseRestClient client;

    public ParcelaRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Parcela> search(String q, Long idAgricultor, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .eq("id_agricultor", idAgricultor)
                .order("nombre", false)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("nombre.ilike.*" + safe + "*,ubicacion.ilike.*" + safe + "*");
        }
        SupabasePage<Parcela> result = client.getPage(PATH, query.build(), Parcela.class);
        rellenarNombresAgricultores(result.content());
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Parcela> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_parcela", id).limit(1);
        Parcela[] rows = client.getArray(PATH, query.build(), Parcela.class);
        if (rows.length == 0) {
            return Optional.empty();
        }
        rellenarNombresAgricultores(List.of(rows[0]));
        return Optional.of(rows[0]);
    }

    public Parcela insert(ParcelaRequest request) {
        Parcela parcela = client.post(PATH, Map.of(), toBody(request), Parcela.class);
        rellenarNombresAgricultores(List.of(parcela));
        return parcela;
    }

    public Parcela update(Long id, ParcelaRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_parcela", id);
        Parcela parcela = client.patch(PATH, query.build(), toBody(request), Parcela.class);
        rellenarNombresAgricultores(List.of(parcela));
        return parcela;
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_parcela", id).build());
    }

    public long countCampanasByParcela(Long id) {
        return client.count("/campana", SupabaseQuery.of().eq("id_parcela", id).build());
    }

    private void rellenarNombresAgricultores(List<Parcela> rows) {
        List<Long> ids = rows.stream().map(Parcela::getIdAgricultor)
                .filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<String, String> params = SupabaseQuery.of()
                .select("id_agricultor,nombres,apellidos")
                .in("id_agricultor", ids.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
        AgricultorRef[] refs = client.getArray("/agricultor", params, AgricultorRef.class);
        Map<Long, String> nombres = new HashMap<>();
        for (AgricultorRef ref : refs) {
            nombres.put(ref.id, (ref.nombres + " " + ref.apellidos).trim());
        }
        rows.forEach(r -> r.setNombreAgricultor(nombres.get(r.getIdAgricultor())));
    }

    private Map<String, Object> toBody(ParcelaRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id_agricultor", request.idAgricultor());
        body.put("nombre", request.nombre().trim());
        body.put("area_hectareas", request.areaHectareas());
        body.put("ubicacion", blankToNull(request.ubicacion()));
        return body;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    /** Fila mínima para resolver el nombre mostrado (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class AgricultorRef {
        @JsonProperty("id_agricultor")
        private Long id;
        @JsonProperty("nombres")
        private String nombres;
        @JsonProperty("apellidos")
        private String apellidos;
    }
}
