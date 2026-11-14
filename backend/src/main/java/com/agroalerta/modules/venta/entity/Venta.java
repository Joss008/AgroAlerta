package com.agroalerta.modules.venta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de la tabla `venta` tal como la devuelve la API REST de Supabase.
 * Los nombres de columna son snake_case (PostgREST).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Venta {

    @JsonProperty("id_venta")
    private Long id;

    @JsonProperty("id_cosecha")
    private Long idCosecha;

    @JsonProperty("fecha_venta")
    private LocalDate fechaVenta;

    @JsonProperty("cantidad_kg")
    private BigDecimal cantidadKg;

    @JsonProperty("precio_total")
    private BigDecimal precioTotal;

    @JsonProperty("comprador")
    private String comprador;

    // transitorio: no es columna
    private LocalDate fechaCosecha;

    // transitorio: no es columna
    private String nombreCampana;
}
