package com.agroalerta.modules.campana.repository;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.supabase.SupabasePage;
import com.agroalerta.common.supabase.SupabaseQuery;
import com.agroalerta.common.supabase.SupabaseRestClient;
import com.agroalerta.modules.campana.dto.CampanaRequest;
import com.agroalerta.modules.campana.entity.Campana;
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

/** Acceso a la tabla `campana` vía API REST de Supabase. */
@Repository
public class CampanaRestRepository {

    private static final String PATH = "/campana";
    private static final String COLUMNS = "id_campana,id_parcela,id_cultivo,nombre,fecha_inicio,fecha_fin";

    private final SupabaseRestClient client;

    public CampanaRestRepository(SupabaseRestClient client) {
        this.client = client;
    }

    public PageResponse<Campana> search(String q, Long idParcela, Long idCultivo, int page, int size) {
        SupabaseQuery query = SupabaseQuery.of()
                .select(COLUMNS)
                .eq("id_parcela", idParcela)
                .eq("id_cultivo", idCultivo)
                .order("fecha_inicio", true)
                .range(page * size, page * size + size - 1);
        if (q != null && !q.isBlank()) {
            String safe = q.trim().replace("%", "").replace("*", "");
            query.or("nombre.ilike.*" + safe + "*");
        }
        SupabasePage<Campana> result = client.getPage(PATH, query.build(), Campana.class);
        rellenarNombres(result.content());
        return PageResponse.of(result.content(), page, size, result.totalElements());
    }

    public Optional<Campana> findById(Long id) {
        SupabaseQuery query = SupabaseQuery.of().select(COLUMNS).eq("id_campana", id).limit(1);
        Campana[] rows = client.getArray(PATH, query.build(), Campana.class);
        if (rows.length == 0) {
            return Optional.empty();
        }
        rellenarNombres(List.of(rows[0]));
        return Optional.of(rows[0]);
    }

    public Campana insert(CampanaRequest request) {
        Campana campana = client.post(PATH, Map.of(), toBody(request), Campana.class);
        rellenarNombres(List.of(campana));
        return campana;
    }

    public Campana update(Long id, CampanaRequest request) {
        SupabaseQuery query = SupabaseQuery.of().eq("id_campana", id);
        Campana campana = client.patch(PATH, query.build(), toBody(request), Campana.class);
        rellenarNombres(List.of(campana));
        return campana;
    }

    public void delete(Long id) {
        client.delete(PATH, SupabaseQuery.of().eq("id_campana", id).build());
    }

    public long countRiegosByCampana(Long id) {
        return client.count("/riego", SupabaseQuery.of().eq("id_campana", id).build());
    }

    public long countGastosByCampana(Long id) {
        return client.count("/gasto", SupabaseQuery.of().eq("id_campana", id).build());
    }

    public long countCosechasByCampana(Long id) {
        return client.count("/cosecha", SupabaseQuery.of().eq("id_campana", id).build());
    }

    private void rellenarNombres(List<Campana> rows) {
        rellenarNombresParcelas(rows);
        rellenarNombresCultivos(rows);
    }

    private void rellenarNombresParcelas(List<Campana> rows) {
        List<Long> ids = rows.stream().map(Campana::getIdParcela)
                .filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<String, String> params = SupabaseQuery.of()
                .select("id_parcela,nombre")
                .in("id_parcela", ids.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
        ParcelaRef[] refs = client.getArray("/parcela", params, ParcelaRef.class);
        Map<Long, String> nombres = new HashMap<>();
        for (ParcelaRef ref : refs) {
            nombres.put(ref.id, ref.nombre);
        }
        rows.forEach(r -> r.setNombreParcela(nombres.get(r.getIdParcela())));
    }

    private void rellenarNombresCultivos(List<Campana> rows) {
        List<Long> ids = rows.stream().map(Campana::getIdCultivo)
                .filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<String, String> params = SupabaseQuery.of()
                .select("id_cultivo,nombre")
                .in("id_cultivo", ids.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
        CultivoRef[] refs = client.getArray("/cultivo", params, CultivoRef.class);
        Map<Long, String> nombres = new HashMap<>();
        for (CultivoRef ref : refs) {
            nombres.put(ref.id, ref.nombre);
        }
        rows.forEach(r -> r.setNombreCultivo(nombres.get(r.getIdCultivo())));
    }

    private Map<String, Object> toBody(CampanaRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id_parcela", request.idParcela());
        body.put("id_cultivo", request.idCultivo());
        body.put("nombre", request.nombre().trim());
        body.put("fecha_inicio", request.fechaInicio());
        body.put("fecha_fin", request.fechaFin());
        return body;
    }

    /** Fila mínima para resolver el nombre mostrado (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class ParcelaRef {
        @JsonProperty("id_parcela")
        private Long id;
        @JsonProperty("nombre")
        private String nombre;
    }

    /** Fila mínima para resolver el nombre mostrado (no es una entidad JPA). */
    @Getter
    @NoArgsConstructor
    private static final class CultivoRef {
        @JsonProperty("id_cultivo")
        private Long id;
        @JsonProperty("nombre")
        private String nombre;
    }
}
