# Planificación del Sprint 2 — AgroValle Connect

> Documento de planificación y descomposición técnica del Sprint 2, redactado en tercera persona.
> Proyecto integrador de Ingeniería de Software II — UNIAJC, período 2026-2.

## 1. Información general

| Campo | Detalle |
|-------|---------|
| Proyecto | AgroValle Connect (Java 17, Spring Boot, PostgreSQL) |
| Sprint | 2 (Segundo Corte, 30 %) |
| Periodo planificado | 29 de octubre – 3 de noviembre de 2026 (entrega en plataforma el 4 de noviembre, 23:59) |
| Product Owner | Juan David Vidal Muñoz |
| Scrum Master | Robert Andres Preciado |
| QA | Juan Esteban Quintero Berrio, Juan David Gonzales Mendez |
| Marco de trabajo | Scrumban con GitFlow; WIP limits: In Progress ≤ 3, Code Review ≤ 2 |

## 2. Objetivo del Sprint

Un agricultor autenticado puede registrar sus fincas por municipio del Valle y publicar lotes de cosecha asociados a una
finca, y un comprador puede filtrar las ofertas por municipio y categoría. La publicación de una cosecha notifica a otros
componentes mediante el patrón Observer, sin acoplar el servicio emisor a los receptores.

## 3. Backlog del Sprint

| ID | Historia de Usuario | MoSCoW | Story Points |
|----|---------------------|--------|--------------|
| HU-08 | Registro de Finca por Municipio | M | 3 |
| HU-02 | Publicación de Productos (lotes de cosecha) | M | 5 |
| HU-04 | Filtro de Categorías y Municipios | M | 3 |
| | **Total** | | **11** |

La capacidad del equipo se fijó en 11 Story Points, igual que en el Sprint 1. El Sprint 1 entregó el registro de
agricultores (HU-01), la autenticación con JWT (HU-06) y el registro de compradores (HU-07); esa base de seguridad y
de usuarios es la que se reutiliza en este Sprint.

## 4. Orden de ejecución y dependencias

```
HU-08 (finca) ──▶ HU-02 (publicar cosecha) ──▶ HU-04 (filtrar ofertas)
```

- **HU-08 primero:** cada lote de cosecha debe poder asociarse a una finca de origen; sin finca no hay trazabilidad.
- **HU-02 después:** consume la finca de HU-08 y es la historia donde se aplica el patrón Observer.
- **HU-04 al final:** consulta los productos publicados en HU-02 y filtra por el municipio de la finca de origen.

## 5. Descomposición técnica

Cada historia se desarrolla con TDD (Red → Green → Refactor) en una rama `feature/HU-XX-nombre`, con Pull Request hacia
`develop` y aprobación de un compañero distinto del autor.

### HU-08 — Registro de Finca por Municipio (3 SP)

**Criterio de aceptación (BDD).** *Given* un agricultor autenticado con JWT; *When* envía `POST /api/v1/fincas` con
nombre, municipio y hectáreas; *Then* el sistema responde `201 Created` y asocia la finca al agricultor del token; si el
municipio no pertenece al catálogo del Valle responde `400 Bad Request`.

- [ ] Catálogo cerrado de los 42 municipios del Valle del Cauca (enumeración `Municipio`).
- [ ] Restricción de validación `@MunicipioDelValle`, que reutilizará HU-04.
- [ ] Entidad `Finca` (muchas fincas por agricultor) y `FincaRepository`.
- [ ] `FincaService`: asocia la finca al agricultor del token y rechaza nombres repetidos del mismo agricultor (`409`).
- [ ] `POST /api/v1/fincas` restringido al rol AGRICULTOR (`401` sin token, `403` con otro rol).
- [ ] Pruebas: unitarias con Mockito, de integración con MockMvc y del catálogo.

### HU-02 — Publicación de Productos (5 SP)

**Criterio de aceptación (BDD).** *Given* un agricultor autenticado con JWT; *When* envía `POST /api/v1/productos` con
tipo, categoría, cantidad en kg, precio unitario y fecha de cosecha; *Then* el sistema valida que la fecha no sea anterior
a hoy y que la cantidad sea mayor a cero, responde `201 Created` y retorna un identificador único persistido.

- [ ] Entidad `Producto` asociada a una `Finca` del agricultor autenticado, con estado `ACTIVA`.
- [ ] Validaciones: fecha de cosecha no anterior a hoy, cantidad > 0, precio > 0.
- [ ] La finca debe pertenecer al agricultor del token (`403` en caso contrario, `404` si no existe).
- [ ] Evento `CosechaPublicadaEvent` publicado con `ApplicationEventPublisher` (patrón Observer).
- [ ] Suscriptores independientes (`@EventListener`): auditoría y notificación al productor.
- [ ] Pruebas: servicio, controlador y suscriptores de forma aislada.

### HU-04 — Filtro de Categorías y Municipios (3 SP)

**Criterio de aceptación (BDD).** *Given* productos registrados bajo un municipio y una categoría con estado `ACTIVA`;
*When* se consulta `GET /api/v1/productos?municipio=Dagua&categoria=Frutas`; *Then* el sistema responde `200 OK` con las
ofertas activas que coinciden, o con un arreglo vacío si no hay coincidencias.

- [ ] Consulta derivada en `ProductoRepository` con parámetros opcionales (municipio de la finca y categoría).
- [ ] Reutilización de `@MunicipioDelValle` para validar el parámetro `municipio` (`400` si no existe).
- [ ] Solo se devuelven ofertas en estado `ACTIVA`.
- [ ] Pruebas: repositorio, servicio y controlador (con y sin coincidencias).

## 6. Decisiones de diseño

| Decisión | Justificación |
|----------|---------------|
| El municipio de una finca es una enumeración cerrada, no texto libre | La historia exige validar contra el catálogo del Valle; la enumeración evita datos inconsistentes y se reutiliza en HU-04. |
| La búsqueda de municipio ignora tildes y mayúsculas | "Tulua" y "Tuluá" deben resolver al mismo municipio; el nombre oficial se devuelve con tildes. |
| Una finca no puede repetir nombre dentro del mismo agricultor | Evita duplicados accidentales y aporta una restricción de unicidad en el modelo de datos. |
| HU-02 recibe el identificador de la finca de origen | Cumple el propósito de HU-08 (trazabilidad) y permite que HU-04 filtre por el municipio de la finca. **Pendiente de confirmación del Product Owner**, porque el escenario BDD original de HU-02 no lista ese campo. |
| El patrón comportamental elegido es Observer | Encaja con la publicación de cosechas; Strategy de fletes no aplica todavía porque aún no existen órdenes ni envíos. |

## 7. Calendario

| Fecha | Actividad |
|-------|-----------|
| Jue 29 oct | Unificación del proyecto: Checkstyle, JaCoCo y CI (`build-and-test`) |
| Vie 30 oct | Planificación del Sprint 2 y HU-08 |
| Sáb 31 oct | HU-02: entidad, validaciones y endpoint |
| Dom 1 nov | HU-02: patrón Observer y sus suscriptores |
| Lun 2 nov | HU-04 y documentación técnica (arquitectura, patrones, esquema, IEEE 29148) |
| Mar 3 nov | Sprint Review, Retrospectiva, PR de release a `main` y verificación de CI y JaCoCo |
| Mié 4 nov | Armado y entrega del PDF (límite 23:59) |

## 8. Definition of Done aplicada

Ninguna historia se da por terminada sin cumplir [`docs/dod.md`](dod.md): compilación local, `mvn checkstyle:check`
sin advertencias, pruebas en verde, cobertura JaCoCo ≥ 60 % en instrucciones y ramas, Conventional Commits, hooks de
Husky activos, Pull Request aprobado por un compañero y criterios de aceptación BDD verificados.

## 9. Riesgos y mitigaciones

| Riesgo | Probabilidad | Mitigación |
|--------|--------------|------------|
| Cobertura de ramas por debajo de 60 % al sumar código nuevo | Media | Pruebas escritas antes del código (TDD) y revisión del reporte JaCoCo en cada PR. |
| Calendario corto hasta la entrega del 4 de noviembre | Alta | Historias en orden de dependencia, PR pequeños y cierre de `main` el 3 de noviembre. |
| Cambios de esquema en la base de datos de Supabase | Media | Las tablas nuevas se crean con `ddl-auto=update`; el script `schema.sql` se documenta el 2 de noviembre. |
| Retrasos en la revisión de Pull Requests | Media | WIP limit de 2 en Code Review y revisor asignado desde que se abre el PR. |

## 10. Criterio de finalización del Sprint

El Sprint 2 se considera terminado cuando las tres historias están fusionadas en `develop`, el pipeline de GitHub
Actions está en verde, la cobertura cumple el umbral y existen la Sprint Review y la Retrospectiva del Sprint 2.
