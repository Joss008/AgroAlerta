package com.agroalerta.modules.gasto.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `gasto` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Gasto {

    @JsonProperty("id_gasto")
    private Long id;

    @JsonProperty("id_campana")
    private Long idCampana;

    @JsonProperty("concepto")
    private String concepto;

    @JsonProperty("fecha_gasto")
    private LocalDate fechaGasto;

    @JsonProperty("monto")
    private BigDecimal monto;

    // transitorio: no es columna
    private String nombreCampana;
}
