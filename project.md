# AgroAlerta Cañete – Control Agrícola

## Objetivo
Aplicación web full stack para registrar y controlar agricultores, parcelas, cultivos, campañas agrícolas, riegos, gastos, cosechas y ventas de la producción en Cañete.

## Arquitectura y tecnologías
- **Frontend:** React + Vite, JavaScript, Tailwind CSS y React Router.
- **Backend:** Java 21 + Spring Boot + Maven, organizado como monolito modular por capas.
- **Base de datos:** Supabase PostgreSQL.
- **Comunicación:** API REST JSON bajo `/api/v1`.
- **Persistencia:** Spring Data JPA/Hibernate y PostgreSQL JDBC.
- **Migraciones:** Flyway.
- **Documentación API:** Springdoc OpenAPI/Swagger.

El repositorio se divide en `frontend/` y `backend/`. `database/` contiene scripts SQL auxiliares y `docs/` la documentación. No implementar microservicios.

## Módulos funcionales
1. Agricultores
2. Parcelas
3. Cultivos
4. Campañas
5. Riegos
6. Gastos
7. Cosechas
8. Ventas
9. Dashboard e indicadores

## Modelo de datos
Respetar el modelo entregado por el usuario y sus ocho tablas:
- `agricultor`: `id_agricultor`, `nombres`, `apellidos`, `telefono`.
- `parcela`: `id_parcela`, `id_agricultor`, `nombre`, `area_hectareas`, `ubicacion`.
- `cultivo`: `id_cultivo`, `nombre`, `variedad`.
- `campana`: `id_campana`, `id_parcela`, `id_cultivo`, `nombre`, `fecha_inicio`, `fecha_fin`.
- `riego`: `id_riego`, `id_campana`, `fecha_riego`, `cantidad_m3`, `observacion`.
- `gasto`: `id_gasto`, `id_campana`, `concepto`, `fecha_gasto`, `monto`.
- `cosecha`: `id_cosecha`, `id_campana`, `fecha_cosecha`, `cantidad_kg`, `observacion`.
- `venta`: `id_venta`, `id_cosecha`, `fecha_venta`, `cantidad_kg`, `precio_total`, `comprador`.

Los tipos de datos y claves deben seguir el diagrama original. Las claves foráneas son: parcela → agricultor; campaña → parcela y cultivo; riego/gasto/cosecha → campaña; venta → cosecha. Confirmar el diagrama original antes de generar el SQL definitivo. No renombrar campos ni eliminar relaciones arbitrariamente.

## Reglas de negocio
- Una parcela pertenece a un agricultor existente.
- Una campaña referencia una parcela y un cultivo existentes.
- `fecha_fin` no puede ser anterior a `fecha_inicio`.
- Riegos, gastos y cosechas pertenecen a una campaña existente.
- Una venta pertenece a una cosecha existente.
- La cantidad vendida acumulada no puede superar la cantidad cosechada.
- Áreas, cantidades y montos no pueden ser negativos.
- Evitar eliminaciones en cascada que destruyan información histórica o ventas relacionadas.
- Calcular producción total, ventas, gastos, rentabilidad por campaña, rendimiento por hectárea y consumo de agua.

## API
Usar rutas REST versionadas:
- `/api/v1/agricultores`
- `/api/v1/parcelas`
- `/api/v1/cultivos`
- `/api/v1/campanas`
- `/api/v1/riegos`
- `/api/v1/gastos`
- `/api/v1/cosechas`
- `/api/v1/ventas`
- `/api/v1/dashboard/resumen`
- `/api/v1/dashboard/produccion`
- `/api/v1/dashboard/ventas`
- `/api/v1/dashboard/gastos`
- `/api/v1/dashboard/rentabilidad`

Implementar CRUD, DTOs, validaciones, paginación, filtros, manejo global de excepciones y códigos HTTP apropiados.

## Organización del backend
Cada módulo debe mantener separación por capas: `controller`, `service`, `repository`, `entity`, `dto` y `mapper`. Mantener configuración transversal en `config/`, seguridad en `security/` y manejo común en `common/`.

## Organización del frontend
Organizar por módulos funcionales. Centralizar las llamadas HTTP en `src/services/`; usar componentes reutilizables para formularios, tablas, modales, navegación, tarjetas y gráficos. React debe comunicarse con Spring Boot; no realizar CRUD directamente desde el navegador contra PostgreSQL.

## Seguridad y configuración
- Usar variables de entorno para credenciales y configuración.
- No subir `.env` al repositorio.
- Nunca exponer contraseñas, `service_role` ni secretos en el frontend.
- Preparar autenticación con Supabase Auth y validación de JWT en Spring Security si se habilita el acceso multiusuario. Vincular la cuenta mediante un UUID adicional, sin reemplazar `id_agricultor`.
- Definir y documentar las reglas de autorización por agricultor antes de habilitar datos privados.

## Persistencia y calidad
- Usar Flyway para migraciones en `backend/src/main/resources/db/migration`.
- Configurar Hibernate con `ddl-auto: validate`; no permitir que Hibernate altere automáticamente el esquema.
- Crear índices para claves foráneas y restricciones `NOT NULL`, `CHECK` y `UNIQUE` cuando correspondan.
- Probar backend y frontend por separado y luego realizar pruebas de integración contra Supabase.
- No sustituir operaciones reales por datos simulados una vez conectada la base de datos.

## Interfaz
Diseño responsive, moderno y sencillo, con identidad visual agrícola: verde como color principal, fondos claros, navegación lateral y dashboard con indicadores y gráficos. Secciones: Dashboard, Agricultores, Parcelas, Cultivos, Campañas, Riegos, Gastos, Cosechas y Ventas.

## Orden de implementación
1. Inspeccionar el repositorio y confirmar el modelo original.
2. Inicializar React/Vite y Spring Boot.
3. Preparar SQL y migraciones.
4. Implementar y probar módulos backend.
5. Documentar API con Swagger.
6. Implementar frontend por módulos.
7. Integrar frontend, backend y Supabase.
8. Ejecutar pruebas funcionales y corregir errores.
9. Documentar instalación y ejecución.

## Instrucción para el agente
Antes de modificar archivos, inspecciona el repositorio y este documento. Trabaja por fases, conserva la estructura modular, evita archivos monolíticos y no inventes campos o reglas que contradigan el diagrama original. Si falta una decisión relevante, documenta la duda y solicita confirmación en lugar de asumirla. Mantén actualizado el README y la documentación técnica.
