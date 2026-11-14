package com.agroalerta.modules.riego.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `riego` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Riego {

    @JsonProperty("id_riego")
    private Long id;

    @JsonProperty("id_campana")
    private Long idCampana;

    @JsonProperty("fecha_riego")
    private LocalDate fechaRiego;

    @JsonProperty("cantidad_m3")
    private BigDecimal cantidadM3;

    @JsonProperty("observacion")
    private String observacion;

    private String nombreCampana; // transitorio: no es columna
}
