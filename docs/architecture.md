# Arquitectura

Monolito modular por capas. Sin microservicios.

```
agroalerta-canete/
├── frontend/   React + Vite + Tailwind + React Router
├── backend/    Java 21 + Spring Boot + Maven
├── database/   Scripts SQL auxiliares
└── docs/       Documentación técnica
```

## Backend (`backend/`)

```
com.agroalerta/
├── AgroAlertaApplication.java
├── config/        OpenAPI, CORS/seguridad
├── security/      SecurityConfig (JWT-ready, abierto por defecto)
├── common/
│   ├── exception/ ApiError, ResourceNotFoundException, BusinessException,
│   │              GlobalExceptionHandler (@RestControllerAdvice)
│   ├── response/  PageResponse<T>
│   ├── supabase/  SupabaseRestClient, SupabaseQuery, SupabasePage
│   └── util/      Pagination
└── modules/
    ├── agricultor | parcela | cultivo | campana
    ├── riego | gasto | cosecha | venta
    └── dashboard
```

Cada módulo mantiene separación por capas:

| Capa | Responsabilidad |
|---|---|
| `controller` | Rutas REST `/api/v1/...`, validación `@Valid`, códigos HTTP |
| `service` + `service/impl` | Reglas de negocio (cascadas, coherencia de datos) |
| `repository` | `*RestRepository`: lecturas/escrituras contra `/rest/v1` |
| `entity` | Modelo de fila JSON de Supabase (`@JsonProperty` snake_case) |
| `dto` | Records de entrada (`*Request`) y salida (`*Response`) |
| `mapper` | Conversión entity → DTO de respuesta |

### Acceso a datos

- `SupabaseRestClient` es el único punto de salida HTTP: `getArray/getPage`,
  `post`, `patch`, `delete`, `count` y `exists` contra `SUPABASE_URL/rest/v1`
  con `apikey` + `Authorization: Bearer`.
- `SupabaseQuery` traduce los parámetros de PostgREST: `select`, `eq`, `ilike`,
  `or`, `in`, `order`, `limit/offset`.
- Los nombres mostrados (`nombreAgricultor`, `nombreCampana`…) **no son
  columnas**: el repositorio los resuelve con una segunda petición `in (…)`.
- Las agregaciones del dashboard (conteos, sumas, agrupaciones) se calculan en
  Java sobre las filas devueltas, recorriendo todas las páginas.
- No hay transacciones ni `@Transactional`: cada petición REST es atómica por sí
  sola.

### Dependencias entre módulos

Grafo acíclico: `parcela → agricultor`, `campana → parcela + cultivo`,
`riego/gasto/cosecha → campana`, `venta → cosecha`, `dashboard → todos (solo lectura)`.

### Transversal

- **Validación**: anotaciones Jakarta en los DTOs; errores en `fieldErrors`.
- **Excepciones**: `ResourceNotFoundException` (404), `BusinessException` (400/409),
  `GlobalExceptionHandler` centraliza el formato de error.
- **Paginación**: `PageResponse` con `page/size/totalElements/totalPages`,
  derivada de la cabecera `Content-Range` de PostgREST.
- **Swagger**: `springdoc-openapi` con tags por módulo.

## Frontend (`frontend/`)

```
src/
├── main.jsx, App.jsx
├── routes/          Router + navegación (9 secciones)
├── modules/         1 pantalla por módulo (CRUD + dashboard)
├── components/
│   ├── layout/      AppLayout, Sidebar (desktop + drawer móvil)
│   ├── common/      PageHeader, Modal, ConfirmDialog, CrudPage, StatCard...
│   ├── forms/       FormField, inputs, EntityForm
│   ├── tables/      DataTable
│   └── charts/      Wrappers de recharts
├── services/        api.js (axios) + 1 archivo por módulo
├── hooks/           useFetch, usePaginatedFetch, useSelectOptions...
├── contexts/        ToastContext
└── utils/           format.js, errors.js, forms.js
```

- Todas las llamadas HTTP pasan por `src/services/`; el navegador **no** habla
  directamente con Supabase ni con PostgreSQL.
- Estados de carga, error y vacío en todas las vistas.
- Sin datos mock: una vez conectado el backend se usan solo datos reales.

## Flujo de datos

```
UI (React) → services/api.js (axios) → Spring Controller → Service (reglas)
           → *RestRepository → SupabaseRestClient → Supabase REST (/rest/v1)
```

> El navegador nunca habla con Supabase: solo con Spring Boot.

## Seguridad

- CORS permitido solo desde `FRONTEND_URL`.
- Credenciales en variables de entorno; `.env` no se versiona.
- Pendiente: JWT de Supabase Auth en Spring Security + autorización por
  agricultor (vinculando un UUID adicional sin reemplazar `id_agricultor`).
