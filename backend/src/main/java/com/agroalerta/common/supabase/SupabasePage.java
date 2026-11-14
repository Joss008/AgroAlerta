package com.agroalerta.common.supabase;

import java.util.List;

/** Resultado paginado de la API REST de Supabase (Content-Range). */
public record SupabasePage<T>(List<T> content, long totalElements) {
}
