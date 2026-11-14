package com.agroalerta.modules.cosecha.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `cosecha` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Cosecha {

    @JsonProperty("id_cosecha")
    private Long id;

    @JsonProperty("id_campana")
    private Long idCampana;

    @JsonProperty("fecha_cosecha")
    private LocalDate fechaCosecha;

    @JsonProperty("cantidad_kg")
    private BigDecimal cantidadKg;

    @JsonProperty("observacion")
    private String observacion;

    // transitorio: no es columna
    private String nombreCampana;
}
