# Bitácora de Daily Scrum - Sprint 1

Este documento registra los acuerdos, avances y bloqueos discutidos durante las sesiones de **Daily Scrum** correspondientes al **Sprint 1**.

---

## Estructura de Registro

### Sesión 1
* **Fecha:** *[Insertar fecha]*
* **Temas tratados:**
  * Discusión y desglose de las tareas técnicas asociadas a cada historia de usuario.
  * Definición inicial de la arquitectura de tareas.
* **Acuerdos / Resultados:**
  * Se propuso un estándar de 8 tareas técnicas por cada historia de usuario para estructurar el trabajo.

---

### Sesión 2
* **Fecha:** *[Insertar fecha]*
* **Temas tratados:**
  * Consolidación final del backlog de tareas e historias de usuario.
  * Asignación de responsabilidades dentro del equipo de desarrollo.
* **Acuerdos / Resultados:**
  * Se confirmaron las 8 tareas por historia de usuario.
  * Se asignó la responsabilidad de cada historia de usuario a cada integrante del equipo para iniciar la etapa de desarrollo.

---

### Sesión 3
* **Fecha:** *[Insertar fecha]*
* **Temas tratados:**
  * Normalización del formato de documentación de tareas.
  * Preparación de entregables formales.
* **Acuerdos / Resultados:**
  * Se ajustó la estructura y redacción de las tareas para generar el archivo `sprint-1-format.md` listo para revisión final.

---

### Sesión 4
* **Fecha:** *[Insertar fecha]*
* **Temas tratados:**
  * Revisión de la **Demo Ejecutable (Versión 1.0)** del proyecto.
  * Identificación de hallazgos y retroalimentación técnica.
* **Acuerdos / Resultados:**
  * Se documentaron las correcciones necesarias sobre la versión ejecutable para ser resueltas en las siguientes sesiones.

---

### Sesión 5
* **Fecha:** *[Insertar fecha]*
* **Temas tratados:**
  * Resolución de bloqueos técnicos específicos del equipo.
  * Revisión de avance de tareas individuales.
* **Acuerdos / Resultados:**
  * Se aclararon dudas conceptuales y se corrigieron errores en la definición de tareas asignadas al desarrollador **Juan Esteban Quintero**.

---

### Sesión 6
* **Fecha:** *[Insertar fecha]*
* **Temas tratados:**
  * Presentación de la versión final de la demo del **Sprint 1**.
  * Validación de las historias de usuario desarrolladas.
* **Acuerdos / Resultados:**
  * Se aprobó satisfactoriamente la demo del Sprint 1 y el cumplimiento de las historias de usuario planificadas.

---

## Resumen del Estado del Sprint 1

| Sesión | Enfoque Principal | Estado |
| :--- | :--- | :---: |
| **Sesión 1** | Planificación de tareas por historia de usuario |  Completado |
| **Sesión 2** | Asignación de historias y tareas al equipo |  Completado |
| **Sesión 3** | Estandarización e integración (`sprint-1-format.md`) |  Completado |
| **Sesión 4** | Revisión de Demo v1 y control de calidad |  Completado |
| **Sesión 5** | Soporte técnico e incidencias (Desarrollo) |  Completado |
| **Sesión 6** | Presentación y aprobación de Demo Sprint 1 |  Aprobado |

# Bitácora de Daily Scrum - Sprint 2

## Estructura de Registro

### Sesión 1
* **Fecha:** 8 de octubre de 2026
* **Temas tratados:**
  * Unificación del proyecto base antes de iniciar el Sprint 2.
  * Configuración de **Checkstyle**, **JaCoCo** y el pipeline de CI (`build-and-test`).
* **Acuerdos / Resultados:**
  * Se acordó que ningún Pull Request se fusione sin pasar `mvn checkstyle:check` y las pruebas en verde.
  * Se fijó el umbral de cobertura JaCoCo en 60 % de instrucciones y ramas.

---

### Sesión 2
* **Fecha:** 12 de octubre de 2026
* **Temas tratados:**
  * Planificación del **Sprint 2**: objetivo, backlog y capacidad del equipo.
  * Orden de ejecución y dependencias entre historias.
* **Acuerdos / Resultados:**
  * Se seleccionaron HU-08 (3 SP), HU-02 (5 SP) y HU-04 (3 SP), para un total de 11 Story Points.
  * Se definió el orden HU-08 → HU-02 → HU-04 y los límites WIP (In Progress ≤ 3, Code Review ≤ 2).
  * Se acordó trabajar cada historia con TDD en una rama `feature/HU-XX-nombre`.

---

### Sesión 3
* **Fecha:** 14 de octubre de 2026
* **Temas tratados:**
  * Inicio de **HU-08 (Registro de Finca por Municipio)**.
  * Diseño del catálogo de municipios del Valle del Cauca.
* **Acuerdos / Resultados:**
  * Se definió el catálogo de 42 municipios como tabla `municipios`, sembrada al arrancar la aplicación.
  * Se acordó crear la restricción `@MunicipioDelValle` para reutilizarla en HU-04.
  * Se confirmó que la búsqueda de municipio ignore tildes y mayúsculas.

---

### Sesión 4
* **Fecha:** 16 de octubre de 2026
* **Temas tratados:**
  * Avance y revisión de **HU-08**: entidad `Finca`, `FincaService` y endpoint `POST /api/v1/fincas`.
  * Revisión del Pull Request de HU-08 por un compañero distinto del autor.
* **Acuerdos / Resultados:**
  * Se implementó el rechazo de nombres de finca repetidos por agricultor (`409 Conflict`).
  * Se restringió el endpoint al rol AGRICULTOR (`401` sin token, `403` con otro rol).
  * Se aprobó y fusionó HU-08 en `develop` con sus pruebas unitarias y de integración.

---

### Sesión 5
* **Fecha:** 20 de octubre de 2026
* **Temas tratados:**
  * Inicio de **HU-02 (Publicación de Productos)**: catálogo de categorías, entidad `Producto` y validaciones.
  * Decisiones de diseño pendientes de confirmación del Product Owner.
* **Acuerdos / Resultados:**
  * Se creó la tabla `categorias` (Frutas, Verduras, Tubérculos, Granos, Café y Hierbas) y la restricción `@CategoriaValida`.
  * Se implementaron las validaciones: fecha de cosecha no anterior a hoy, cantidad y precio mayores a cero.
  * Se dejó para confirmación del Product Owner que HU-02 reciba el identificador de la finca de origen y que la lista de categorías sea ajustable.

---

### Sesión 6
* **Fecha:** 22 de octubre de 2026
* **Temas tratados:**
  * Avance de **HU-02**: asociación del producto con la finca del agricultor autenticado.
  * Implementación del patrón **Observer**.
* **Acuerdos / Resultados:**
  * Si la finca no existe o pertenece a otro agricultor se responde `404`, para no revelar fincas ajenas.
  * Se publicó el evento `CosechaPublicadaEvent` con `ApplicationEventPublisher`.
  * Se separaron los suscriptores: auditoría en la misma transacción y notificación al productor solo tras el commit.

---

### Sesión 7
* **Fecha:** 26 de octubre de 2026
* **Temas tratados:**
  * Desarrollo de **HU-04 (Filtro de Categorías y Municipios)**.
  * Revisión de bloqueos en las consultas del repositorio.
* **Acuerdos / Resultados:**
  * Se implementó la consulta derivada en `ProductoRepository` con parámetros opcionales de municipio y categoría.
  * Se validaron los parámetros contra los catálogos (`400` si no existen) y se dejó la consulta pública, sin token.
  * Se acordó devolver únicamente ofertas en estado `ACTIVA`, o un arreglo vacío si no hay coincidencias.

---

### Sesión 8
* **Fecha:** 28 de octubre de 2026
* **Temas tratados:**
  * Documentación técnica del Sprint 2.
  * Refactorización según principios SOLID y ajustes a historias del Sprint 1.
* **Acuerdos / Resultados:**
  * Se documentaron la arquitectura, los patrones, el esquema de la base de datos (`schema.sql`) y el documento IEEE 29148.
  * Se separaron los servicios en interfaces con implementaciones en `service/impl` (DIP / OCP) y se creó la superclase `PerfilUsuario` (LSP).
  * HU-07 pasó a exigir dirección comercial y municipio, conforme al DER del equipo.

---

### Sesión 9
* **Fecha:** 30 de octubre de 2026
* **Temas tratados:**
  * Revisión de calidad y cobertura de pruebas.
  * Verificación de la Definition of Done (`docs/dod.md`) en las tres historias.
* **Acuerdos / Resultados:**
  * Se revisó el reporte JaCoCo y se agregaron pruebas para cumplir el umbral de 60 % en instrucciones y ramas.
  * Se confirmó el uso de Conventional Commits, hooks de Husky y Pull Requests aprobados por un compañero.
  * Se verificó que el pipeline de GitHub Actions estuviera en verde.

---

### Sesión 10
* **Fecha:** 31 de octubre de 2026
* **Temas tratados:**
  * **Sprint Review** y **Retrospectiva** del Sprint 2.
  * Pull Request de release a `main` y preparación de la entrega.
* **Acuerdos / Resultados:**
  * Se validaron HU-08, HU-02 y HU-04 frente a sus criterios de aceptación BDD.
  * Se registraron las acciones de mejora de la retrospectiva.
  * Se fusionó el release en `main` y se armó el PDF de entrega para la plataforma.

---

## Resumen del Estado del Sprint 2

| Sesión | Enfoque Principal | Estado |
| :--- | :--- | :---: |
| **Sesión 1** | Unificación del proyecto: Checkstyle, JaCoCo y CI |  Completado |
| **Sesión 2** | Planificación del Sprint 2 y dependencias entre historias |  Completado |
| **Sesión 3** | Inicio de HU-08 y catálogo de municipios |  Completado |
| **Sesión 4** | Cierre y revisión de HU-08 (fincas) |  Completado |
| **Sesión 5** | Inicio de HU-02 y decisiones con el Product Owner |  Completado |
| **Sesión 6** | HU-02: finca de origen y patrón Observer |  Completado |
| **Sesión 7** | Desarrollo de HU-04 (filtros públicos) |  Completado |
| **Sesión 8** | Documentación técnica y refactorización SOLID |  Completado |
| **Sesión 9** | Calidad: JaCoCo, DoD y CI |  Completado |
| **Sesión 10** | Sprint Review, Retrospectiva y entrega |  Completado |
