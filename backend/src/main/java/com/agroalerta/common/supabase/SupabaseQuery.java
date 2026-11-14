package com.agroalerta.common.supabase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Construye los query params de PostgREST: select, filtros eq/ilike,
 * orden y paginación (limit/offset).
 */
public final class SupabaseQuery {

    private final Map<String, String> params = new LinkedHashMap<>();

    public static SupabaseQuery of() {
        return new SupabaseQuery();
    }

    public SupabaseQuery select(String columns) {
        params.put("select", columns);
        return this;
    }

    public SupabaseQuery eq(String column, Object value) {
        if (value != null) {
            params.put(column, "eq." + value);
        }
        return this;
    }

    /** Búsqueda insensible a mayúsculas conteniendo el texto. */
    public SupabaseQuery ilike(String column, String value) {
        if (value != null && !value.isBlank()) {
            String safe = value.trim().replace("%", "").replace("*", "");
            params.put(column, "ilike.*" + safe + "*");
        }
        return this;
    }

    /** Filtro OR de PostgREST: or=(col1.ilike.*x*,col2.ilike.*x*) */
    public SupabaseQuery or(String expression) {
        if (expression != null && !expression.isBlank()) {
            params.put("or", "(" + expression + ")");
        }
        return this;
    }

    /** Filtro por lista de valores: col=in.(1,2,3) */
    public SupabaseQuery in(String column, String csvValues) {
        if (csvValues != null && !csvValues.isBlank()) {
            params.put(column, "in.(" + csvValues + ")");
        }
        return this;
    }

    public SupabaseQuery order(String column, boolean desc) {
        params.put("order", column + (desc ? ".desc" : ".asc") + ".nullslast");
        return this;
    }

    public SupabaseQuery range(int from, int to) {
        params.put("offset", String.valueOf(from));
        params.put("limit", String.valueOf(to - from + 1));
        return this;
    }

    public SupabaseQuery limit(int limit) {
        params.put("limit", String.valueOf(limit));
        return this;
    }

    public Map<String, String> build() {
        return params;
    }
}
