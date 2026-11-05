# Retrospectiva del Sprint 2 — AgroValle Connect

> Redactada en tercera persona. Los hallazgos se derivan de la evidencia del repositorio (planificación, Pull Requests,
> historial de commits, pipeline y documentación). 
> del equipo y no pueden inferirse del repositorio.

## 1. Objetivo de la retrospectiva

Analizar cómo el Scrum Team organizó y ejecutó el Sprint 2, identificar qué prácticas conviene iniciar, detener o
mantener, y verificar el efecto de las acciones acordadas en la
[retrospectiva del Sprint 1](sprint-1-retrospective.md). El análisis se centra en el proceso de trabajo; el resultado
técnico se documenta en la [Sprint Review](sprint-2-review.md).

## 2. Contexto del Sprint

| Campo | Detalle |
|-------|---------|
| Sprint | 2 (Segundo Corte, 30 %) |
| Periodo planificado | 29 de octubre – 3 de noviembre de 2026; entrega el 4 de noviembre, 23:59 |
| Capacidad y compromiso | 11 SP comprometidos sobre 11 SP de capacidad |
| Historias | HU-08 (3 SP), HU-02 (5 SP), HU-04 (3 SP) |
| Marco de trabajo | Scrumban con GitFlow; límites WIP: In Progress ≤ 3, Code Review ≤ 2 |
| Fecha de la retrospectiva | **5 de Octubre del 2026** |

## 3. Cronología observable

| Fecha | Hito | Evidencia |
|-------|------|-----------|
| 29 de octubre | Pipeline de CI en GitHub Actions | Pull Request #44 |
| 30 de octubre | Unificación del proyecto, planificación del Sprint y HU-08 | Pull Requests #45, #46 y #47 |
| 2 de noviembre | Diseño, refactorización SOLID y HU-02 con Observer | Pull Requests #48, #49 y #50 |
| 4 de noviembre | Reestructuración del repositorio, HU-04, documentación técnica y simulación de fallo de CI | Pull Requests #51, #52 y #53 |

La planificación fijó el 2 de noviembre para HU-04 y la documentación técnica, y el 3 de noviembre para la Review, la
Retrospectiva y el cierre de `main`. Los Pull Requests de HU-04 y de la documentación se fusionaron el 4 de noviembre,
el día del límite de entrega.

## 4. Seguimiento de las acciones del Sprint 1

| Acción acordada en el Sprint 1 | Estado | Evidencia en el Sprint 2 |
|--------------------------------|--------|--------------------------|
| Revisión cruzada del documento del Sprint antes de cerrar el Planning | Cumplida | `sprint-2-planning.md` mantiene coherentes el objetivo, el backlog (3 historias, 11 SP), el orden de ejecución y el calendario. |
| Numeración de tareas sin vacíos | No aplica | El desglose usa listas de verificación sin identificadores de tarea. |
| Al menos una tarea de pruebas por historia | Cumplida | Cada historia del desglose incluye un punto de pruebas (unitarias, de integración y de suscriptores). |
| Sprint Goal con una sola interpretación | Parcial | El objetivo es más concreto que el del Sprint 1, pero sigue reuniendo tres capacidades en un mismo párrafo. |
| Alcance incluido y excluido por historia | Parcial | Cada historia tiene criterio BDD y orden de dependencia; no declara de forma explícita lo que queda fuera. |
| Margen de capacidad | No cumplida | Se comprometieron de nuevo 11 SP sobre 11 SP de capacidad, y el propio documento calificó el riesgo de calendario como alto. |
| Mantener justificación por historia y estimación frente a capacidad | Cumplida | Tabla de dependencias y tabla de decisiones de diseño con su justificación. |
| Registrar ramas, commits y Pull Requests | Cumplida | Rama `feature/HU-XX` por historia, Conventional Commits y Pull Requests numerados del #44 al #53. |

## 5. Start — Empezar

1. **Cotejar el Sprint contra la especificación de entrega antes del último día.** En la revisión de cierre del
   repositorio se detectó que faltaban los wireframes, la Sprint Review y la Retrospectiva del Sprint 2, entregables que
   la especificación exige. Se propone una lista de verificación basada en la rúbrica que se revise a mitad del Sprint.
2. **Fijar una fecha interna de cierre con holgura.** La planificación previó cerrar `main` el 3 de noviembre, pero tres
   Pull Requests se fusionaron el 4. Se propone cerrar la integración al menos un día antes del límite.
3. **Convertir los entregables documentales en tareas con responsable.** Los documentos de ejecución (bitácora, Review,
   Retrospectiva) y los wireframes no figuraban como tareas del desglose técnico.
4. **Registrar la bitácora del Daily Scrum el mismo día de cada sesión.** La bitácora del Sprint 1 conserva fechas sin
   diligenciar y la del Sprint 2 se redactó al cierre.
5. **Reservar margen de capacidad.** Es una acción pendiente del Sprint 1 que no se aplicó.

## 6. Stop — Dejar de hacer

1. **Dejar archivos y paquetes fuera de su ubicación.** El Pull Request #51 existió para reubicar documentos y eliminar
   paquetes subidos fuera de su lugar. Aun después de ese ajuste, la revisión de cierre encontró una carpeta de demostración
   duplicada (`agrovalle-connect-demo/`) y una segunda clase `@SpringBootApplication` residual.
2. **Concentrar integración, documentación técnica y pruebas del pipeline en los dos últimos días.** Tres de los diez
   Pull Requests del Sprint se fusionaron el último día.
3. **Dejar decisiones de producto sin cierre.** La inclusión del identificador de finca en HU-02 y la lista inicial de
   categorías quedaron marcadas como pendientes de confirmación del Product Owner desde la planificación.

## 7. Continue — Continuar

1. **Desarrollo guiado por pruebas con pruebas por capa.** Pruebas unitarias con Mockito, de integración con MockMvc y de
   suscriptores, con una cobertura local muy por encima del umbral de 60 %.
2. **Una rama y un Pull Request por historia con GitFlow y Conventional Commits.** Facilitan la trazabilidad y la
   revisión por pares.
3. **Pipeline con compuertas.** Checkstyle, pruebas y verificación de cobertura en cada cambio, más la simulación de un
   fallo en una rama de característica (Pull Request #53) para comprobar que el pipeline bloquea la integración.
4. **Orden de ejecución por dependencias.** HU-08 → HU-02 → HU-04 evitó retrabajo entre historias.
5. **Tabla de decisiones de diseño con justificación.** Permite explicar por qué los catálogos son tablas, por qué se
   eligió Observer y por qué la notificación se envía después de confirmar la transacción.

## 8. Hallazgos del Sprint

### Aspectos positivos

- Las tres historias comprometidas se integraron mediante Pull Requests.
- El patrón Observer quedó desacoplado en producción, con semántica transaccional distinta para auditoría y notificación.
- La cobertura de pruebas supera ampliamente el umbral y el pipeline lo verifica.
- La documentación técnica refleja las decisiones de diseño y se mantuvo trazable con el código.

### Dificultades identificadas

- Concentración del trabajo de integración y documentación en el último día.
- Entregables documentales no incluidos en el desglose técnico (wireframes, Review, Retrospectiva).
- Residuos del proceso de unificación del proyecto (carpeta duplicada y clase de arranque repetida).
- Decisiones de producto pendientes de confirmación.
- Margen de capacidad nulo de nuevo.

### Información que aporta el equipo

| Tema | Observación del equipo |
|------|------------------------|
| Cumplimiento de los límites WIP (In Progress ≤ 3, Code Review ≤ 2) | **[Limites marcados dentro del WIP]** |
| Bloqueos o esperas en la revisión de Pull Requests | **[Bloqueos listos para proteger la rama main y esspera del PR]** |
| Comunicación y reparto de trabajo dentro del equipo | **[Se reparten las tares de forma continua en el equipo]** |
| Qué se haría distinto en el siguiente Sprint | **[Desarollo de sprint 3 funcionamiento de la app y presentacion al grupo]** |

## 9. Acciones para el Sprint 3

| Acción | Responsable propuesto | Verificación |
|--------|-----------------------|--------------|
| Revisar a mitad del Sprint la lista de verificación basada en la especificación de entrega | Scrum Master | Lista revisada en el Daily Scrum del punto medio |
| Fijar la fecha interna de cierre de `main` un día antes del límite | Scrum Master | Último Pull Request fusionado antes de esa fecha |
| Incluir los entregables documentales como tareas con responsable en el desglose técnico | Product Owner y equipo | Tareas visibles en el tablero |
| Registrar la bitácora el mismo día de cada sesión | Scrum Master | Fechas diligenciadas en cada entrada |
| Reservar al menos un margen de capacidad al comprometer historias | Equipo Scrum | Compromiso inferior a la capacidad en el Planning |
| Cerrar las decisiones pendientes con el Product Owner antes de iniciar la historia afectada | Product Owner | Decisión registrada en el documento del Sprint |
| Revisar el árbol del repositorio antes de cada Pull Request de integración para no dejar archivos ni paquetes residuales | QA | Pull Request sin archivos fuera de su ubicación |
| Declarar el alcance incluido y excluido de cada historia | Product Owner | Sección de alcance en el desglose |

## 10. Conclusión

El Sprint 2 cumplió su compromiso funcional y consolidó prácticas de ingeniería como TDD, integración continua y
revisión mediante Pull Requests. Las principales oportunidades de mejora se concentran en la gestión del tiempo hacia la
fecha de entrega, en tratar los entregables documentales como trabajo planificado y en mantener ordenado el repositorio.
De las ocho acciones del Sprint 1, cuatro se cumplieron, una no aplicó, dos se cumplieron de forma parcial y una no se
cumplió. Las acciones del Sprint 3 se verificarán en la siguiente retrospectiva.
