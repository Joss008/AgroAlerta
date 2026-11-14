# API REST

Base: `http://localhost:8080/api/v1`
Swagger UI: `http://localhost:8080/swagger-ui.html`
OpenAPI: `http://localhost:8080/v3/api-docs`

## Convenciones

### Paginación

Todo listado acepta:

| Param | Default | Descripción |
|---|---|---|
| `page` | `0` | Página (base 0) |
| `size` | `20` | Tamaño (máx. 100) |
| `q` | — | Búsqueda de texto (insensible a mayúsculas) |

Respuesta:

```json
{ "content": [], "page": 0, "size": 20, "totalElements": 0, "totalPages": 0 }
```

### Errores

```json
{
  "timestamp": "2026-05-10T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validación fallida",
  "path": "/api/v1/agricultores",
  "fieldErrors": [{ "field": "nombres", "message": "Los nombres son obligatorios" }]
}
```

- `400` validación / reglas de negocio (ej. `fecha_fin < fecha_inicio`).
- `404` recurso inexistente.
- `409` conflicto de integridad (borrar con dependientes, venta que supera la cosecha).
- `500` error no controlado.

### Métodos

`GET` lista, `GET /{id}`, `POST` (crea, `201`), `PUT /{id}` (reemplaza),
`DELETE /{id}` (`204`).

---

## Agricultores — `/agricultores`

| Método | Ruta | Query |
|---|---|---|
| GET | `/agricultores` | `q`, `page`, `size` |
| GET | `/agricultores/{id}` | — |
| POST | `/agricultores` | — |
| PUT | `/agricultores/{id}` | — |
| DELETE | `/agricultores/{id}` | — (`409` si tiene parcelas) |

```json
{ "id": 1, "nombres": "Juan", "apellidos": "Pérez", "telefono": "999 888 777" }
```

## Parcelas — `/parcelas`

| Método | Ruta | Query |
|---|---|---|
| GET | `/parcelas` | `q`, `idAgricultor`, `page`, `size` |
| POST/PUT/DELETE | `/parcelas[/{id}]` | DELETE: `409` si tiene campañas |

```json
{ "id": 1, "idAgricultor": 1, "nombre": "Parcela Norte", "areaHectareas": 3.5,
  "ubicacion": "Villa El Salvador", "nombreAgricultor": "Juan Pérez" }
```

## Cultivos — `/cultivos`

| Método | Ruta | Query |
|---|---|---|
| GET | `/cultivos` | `q`, `page`, `size` |
| POST/PUT/DELETE | `/cultivos[/{id}]` | DELETE: `409` si tiene campañas |

```json
{ "id": 1, "nombre": "Espárrago", "variedad": "Violetti" }
```

## Campañas — `/campanas`

| Método | Ruta | Query |
|---|---|---|
| GET | `/campanas` | `q`, `idParcela`, `idCultivo`, `page`, `size` |
| POST/PUT/DELETE | `/campanas[/{id}]` | DELETE: `409` si tiene riegos/gastos/cosechas |

```json
{ "id": 1, "idParcela": 1, "idCultivo": 1, "nombre": "Campaña 2026",
  "fechaInicio": "2026-03-01", "fechaFin": "2026-08-31",
  "nombreParcela": "Parcela Norte", "nombreCultivo": "Espárrago" }
```

Reglas: `fechaFin` opcional; si existe no puede ser anterior a `fechaInicio`.

## Riegos — `/riegos`

| Método | Ruta | Query |
|---|---|---|
| GET | `/riegos` | `q` (observación), `idCampana`, `page`, `size` |
| POST/PUT/DELETE | `/riegos[/{id}]` | — |

```json
{ "id": 1, "idCampana": 1, "fechaRiego": "2026-04-01", "cantidadM3": 120.5,
  "observacion": "Riego por goteo", "nombreCampana": "Campaña 2026" }
```

## Gastos — `/gastos`

| Método | Ruta | Query |
|---|---|---|
| GET | `/gastos` | `q` (concepto), `idCampana`, `page`, `size` |
| POST/PUT/DELETE | `/gastos[/{id}]` | — |

```json
{ "id": 1, "idCampana": 1, "concepto": "Fertilizante", "fechaGasto": "2026-03-15",
  "monto": 450.00, "nombreCampana": "Campaña 2026" }
```

## Cosechas — `/cosechas`

| Método | Ruta | Query |
|---|---|---|
| GET | `/cosechas` | `q` (observación), `idCampana`, `page`, `size` |
| POST/PUT/DELETE | `/cosechas[/{id}]` | DELETE: `409` si tiene ventas; PUT valida ventas acumuladas |

```json
{ "id": 1, "idCampana": 1, "fechaCosecha": "2026-07-20", "cantidadKg": 1500.00,
  "observacion": null, "nombreCampana": "Campaña 2026" }
```

## Ventas — `/ventas`

| Método | Ruta | Query |
|---|---|---|
| GET | `/ventas` | `q` (comprador), `idCosecha`, `page`, `size` |
| POST/PUT/DELETE | `/ventas[/{id}]` | `409` si la cantidad supera lo cosechado |

```json
{ "id": 1, "idCosecha": 1, "fechaVenta": "2026-07-25", "cantidadKg": 1000.00,
  "precioTotal": 12000.00, "comprador": "Cooperativa Cañete",
  "fechaCosecha": "2026-07-20", "nombreCampana": "Campaña 2026" }
```

---

## Dashboard — `/dashboard`

| Ruta | Respuesta |
|---|---|
| `GET /dashboard/resumen` | Contadores, área, producción, ventas, gastos, rentabilidad y agua |
| `GET /dashboard/produccion` | Producción y rendimiento (kg/ha) por campaña |
| `GET /dashboard/ventas` | Cantidad, monto y precio promedio por campaña |
| `GET /dashboard/gastos` | `{ porCampana: [...], porConcepto: [...] }` |
| `GET /dashboard/rentabilidad` | `ventas - gastos`, rendimiento y consumo de agua por campaña |

```json
// GET /dashboard/resumen
{ "agricultores": 12, "parcelas": 20, "cultivos": 6, "campanas": 15,
  "campanasActivas": 7, "areaTotalHectareas": 48.50,
  "produccionTotalKg": 32500.00, "ventasTotalSoles": 145000.00,
  "gastosTotalSoles": 62000.00, "rentabilidadTotalSoles": 83000.00,
  "aguaTotalM3": 12450.000 }
```

```json
// GET /dashboard/produccion (fila)
{ "idCampana": 1, "nombreCampana": "Campaña 2026", "cultivo": "Espárrago",
  "parcela": "Parcela Norte", "areaHectareas": 3.50,
  "produccionKg": 1500.00, "rendimientoKgHa": 428.57 }
```

```json
// GET /dashboard/rentabilidad (fila)
{ "idCampana": 1, "nombreCampana": "Campaña 2026", "produccionKg": 1500.00,
  "ventasSoles": 18000.00, "gastosSoles": 7500.00, "rentabilidadSoles": 10500.00,
  "rendimientoKgHa": 428.57, "consumoAguaM3": 950.000 }
```

Moneda: soles peruanos (S/). Fechas ISO `yyyy-MM-dd`.
