package com.agroalerta.common.supabase;

import com.agroalerta.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Cliente HTTP contra la API REST de Supabase (PostgREST) en /rest/v1.
 * Autentica con la clave secreta del servidor (o la publishable si no hay secreta).
 */
@Component
public class SupabaseRestClient {

    private final RestClient rest;
    private final String restBase;
    private final ObjectMapper objectMapper;

    public SupabaseRestClient(
            @Value("${supabase.url}") String url,
            @Value("${supabase.publishable-key}") String publishableKey,
            @Value("${supabase.secret-key:}") String secretKey,
            ObjectMapper objectMapper) {

        String key = (secretKey == null || secretKey.isBlank()) ? publishableKey : secretKey.trim();
        String base = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        this.restBase = base + "/rest/v1";
        this.rest = RestClient.builder()
                .baseUrl(restBase)
                .defaultHeader("apikey", key)
                .defaultHeader("Authorization", "Bearer " + key)
                .defaultHeader("Accept", "application/json")
                .build();
        this.objectMapper = objectMapper;
    }

    // ------------------------------------------------------------------ GET

    /** GET que devuelve el cuerpo tipado y el total de la cabecera Content-Range. */
    public <T> SupabasePage<T> getPage(String path, Map<String, String> params, Class<T> type) {
        ResponseEntity<String> response = exchange(HttpMethod.GET, path, params, null, true, false);
        T[] array = readArray(response.getBody(), type);
        long total = parseTotal(response.getHeaders().getFirst("Content-Range"), array.length);
        return new SupabasePage<>(java.util.List.of(array), total);
    }

    public <T> T[] getArray(String path, Map<String, String> params, Class<T> type) {
        ResponseEntity<String> response = exchange(HttpMethod.GET, path, params, null, false, false);
        return readArray(response.getBody(), type);
    }

    public <T> java.util.List<T> getList(String path, Map<String, String> params, Class<T> type) {
        return java.util.List.of(getArray(path, params, type));
    }

    // --------------------------------------------------------------- WRITE

    public <T> T post(String path, Map<String, String> params, Object body, Class<T> type) {
        ResponseEntity<String> response = exchange(HttpMethod.POST, path, params, body, false, false);
        return readFirst(response.getBody(), type);
    }

    public <T> T patch(String path, Map<String, String> params, Object body, Class<T> type) {
        ResponseEntity<String> response = exchange(HttpMethod.PATCH, path, params, body, false, false);
        return readFirst(response.getBody(), type);
    }

    public void delete(String path, Map<String, String> params) {
        exchange(HttpMethod.DELETE, path, params, null, false, false);
    }

    /** Cuenta filas sin traer datos (Range 0-0 + Prefer: count=exact). */
    public long count(String path, Map<String, String> params) {
        ResponseEntity<String> response = exchange(HttpMethod.GET, path, params, null, true, true);
        return parseTotal(response.getHeaders().getFirst("Content-Range"), 0);
    }

    /** true si existe al menos una fila que cumple el filtro. */
    public boolean exists(String path, Map<String, String> params) {
        Map<String, String> p = new java.util.LinkedHashMap<>(params);
        p.put("limit", "1");
        ResponseEntity<String> response = exchange(HttpMethod.GET, path, p, null, false, false);
        String body = response.getBody();
        return body != null && !body.isBlank() && !"[]".equals(body.trim());
    }

    // ------------------------------------------------------------- INTERNOS

    private ResponseEntity<String> exchange(HttpMethod method, String path,
                                            Map<String, String> params, Object body,
                                            boolean exactCount, boolean countOnly) {
        URI uri = buildUri(path, params);

        RestClient.RequestBodySpec spec = rest.method(method)
                .uri(uri)
                .headers(h -> {
                    if (exactCount) {
                        h.set("Prefer", "count=exact");
                    }
                    if (countOnly) {
                        h.set("Range", "0-0");
                    } else if (body != null) {
                        h.set("Prefer", "return=representation");
                    }
                });

        if (body != null) {
            spec = spec.contentType(MediaType.APPLICATION_JSON).body(body);
        }

        try {
            return spec.retrieve().toEntity(String.class);
        } catch (RestClientResponseException ex) {
            throw translate(ex);
        }
    }

    /**
     * Construye la URI con los query params de PostgREST. No se puede usar
     * {@code build(Map)} porque solo expande variables de plantilla ({@code {id}})
     * y descarta el resto: hay que añadirlos como queryParam explícitos.
     */
    private URI buildUri(String path, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(restBase + path);
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        return builder.build().encode().toUri();
    }

    private RuntimeException translate(RestClientResponseException ex) {
        String message = ex.getResponseBodyAsString();
        String supabaseMessage = message;
        try {
            JsonNode node = objectMapper.readTree(message);
            if (node.hasNonNull("message")) {
                supabaseMessage = node.get("message").asText();
            }
        } catch (Exception ignored) {
            // el cuerpo no es JSON: se usa tal cual
        }

        HttpStatusCode status = ex.getStatusCode();
        if (status.is4xxClientError()) {
            if (status.value() == 404 || supabaseMessage.toLowerCase().contains("no rows")) {
                return new BusinessException(supabaseMessage, org.springframework.http.HttpStatus.NOT_FOUND);
            }
            if (status.value() == 401 || status.value() == 403) {
                return BusinessException.conflict(
                        "Supabase rechazó la operación (RLS o clave inválida): " + supabaseMessage);
            }
            return new BusinessException(supabaseMessage);
        }
        return new BusinessException("Error de Supabase: " + supabaseMessage,
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private <T> T[] readArray(String body, Class<T> type) {
        if (body == null || body.isBlank()) {
            return createEmptyArray(type);
        }
        try {
            return objectMapper.readValue(body, objectMapper.getTypeFactory()
                    .constructArrayType(type));
        } catch (Exception ex) {
            throw new BusinessException("Respuesta inválida de Supabase: " + ex.getMessage());
        }
    }

    private <T> T readFirst(String body, Class<T> type) {
        T[] array = readArray(body, type);
        if (array.length == 0) {
            throw new BusinessException("Supabase no devolvió el registro resultante",
                    org.springframework.http.HttpStatus.NOT_FOUND);
        }
        return array[0];
    }

    @SuppressWarnings("unchecked")
    private <T> T[] createEmptyArray(Class<T> type) {
        return (T[]) java.lang.reflect.Array.newInstance(type, 0);
    }

    private long parseTotal(String contentRange, int fallback) {
        if (contentRange == null || !contentRange.contains("/")) {
            return fallback;
        }
        String total = contentRange.substring(contentRange.lastIndexOf('/') + 1).trim();
        if ("*".equals(total)) {
            return fallback;
        }
        try {
            return Long.parseLong(total);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
