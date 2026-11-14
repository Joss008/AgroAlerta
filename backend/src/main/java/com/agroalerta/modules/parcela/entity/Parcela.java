package com.agroalerta.modules.parcela.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `parcela` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Parcela {

    @JsonProperty("id_parcela")
    private Long id;

    @JsonProperty("id_agricultor")
    private Long idAgricultor;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("area_hectareas")
    private BigDecimal areaHectareas;

    @JsonProperty("ubicacion")
    private String ubicacion;

    private String nombreAgricultor; // transitorio: no es columna
}
