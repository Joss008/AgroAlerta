package com.agroalerta.modules.cultivo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `cultivo` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Cultivo {

    @JsonProperty("id_cultivo")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("variedad")
    private String variedad;
}
