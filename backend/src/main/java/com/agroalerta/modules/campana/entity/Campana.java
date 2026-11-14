package com.agroalerta.modules.campana.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `campana` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Campana {

    @JsonProperty("id_campana")
    private Long id;

    @JsonProperty("id_parcela")
    private Long idParcela;

    @JsonProperty("id_cultivo")
    private Long idCultivo;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("fecha_inicio")
    private LocalDate fechaInicio;

    @JsonProperty("fecha_fin")
    private LocalDate fechaFin;

    private String nombreParcela; // transitorio: no es columna

    private String nombreCultivo; // transitorio: no es columna
}
