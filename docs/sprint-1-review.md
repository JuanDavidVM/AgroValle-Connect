# Sprint 1 Review — AgroValle Connect

> Documento de evidencias de la demostración técnica del Sprint 1, redactado en tercera persona.
> Proyecto integrador de Ingeniería de Software II — UNIAJC, período 2026-2.
>
> **Nota sobre la redacción.** La ceremonia de Review del Sprint 1 se celebró en su momento, pero
> no quedó documentada. Este documento la registra de forma retroactiva a partir de los artefactos
> del Sprint 1 versionados en el repositorio: la planificación, la bitácora de Daily Scrum, la
> retrospectiva, la documentación de la API y el historial de Git. No se incorpora ninguna
> afirmación que no tenga respaldo en esos artefactos.

## 1. Información de la sesión

| Campo | Detalle |
|-------|---------|
| Proyecto | AgroValle Connect (Java 17, Spring Boot, PostgreSQL) |
| Sprint | 1 — Primer Corte |
| Periodo del Sprint | 23 – 30 de septiembre de 2026 |
| Cierre del Sprint | 30 de septiembre de 2026, fecha en que se integraron la demo ejecutable y los documentos de cierre |
| Product Owner | Juan David Vidal Muñoz |
| Scrum Master | Robert Andres Preciado |
| QA | Juan Esteban Quintero Berrio, Juan David Gonzales Mendez |
| Asistentes a la sesión | Juan David Vidal Muñoz, Robert Andres Preciado, Juan Esteban Quintero Berrio, Juan David Gonzales Mendez |

## 2. Objetivo del Sprint y resultado

**Sprint Goal.** "Habilitar el registro inicial de agricultores y la autenticación del inicio de
sesión agrícolas y registro de compradores comerciales, validando la persistencia de la información
en PostgreSQL y la arquitectura REST."

**Resultado.** El Scrum Team entregó las tres historias comprometidas, equivalentes a los 11 Story
Points de la capacidad del Sprint. La demostración se realizó sobre una aplicación Spring Boot en
ejecución, conectada a PostgreSQL en Supabase, con la documentación interactiva de Swagger como
interfaz de la demostración. Los cuatro endpoints del incremento respondieron con los códigos
esperados, incluidos los casos de error.

El objetivo se cumplió en cuanto a funcionalidad. La forma de integrar ese incremento al
repositorio, en cambio, no siguió la estrategia de ramificación acordada, y se detalla en la
sección 3.

## 3. Historias de usuario del Sprint

| ID | Historia | SP | MoSCoW | Estado |
|----|----------|----|--------|--------|
| HU-01 | Registro de Agricultores | 3 | Must Have | Terminada |
| HU-06 | Autenticación e Inicio de Sesión con JWT | 5 | Must Have | Terminada |
| HU-07 | Registro de Comprador Comercial | 3 | Must Have | Terminada |
| | **Total** | **11** | | **11 de 11 SP entregados** |

### 3.1 Forma de integración del incremento

El incremento funcional no se integró mediante un Pull Request por historia de usuario. El Scrum
Team entregó el Sprint 1 como una demo ejecutable completa, incorporada al repositorio en un único
Pull Request documental:

| Pull Request | Fecha | Contenido |
|--------------|-------|-----------|
| #17 | 29 de septiembre | Planificación del Sprint 1 y desglose de tareas técnicas |
| #18 | 30 de septiembre | Retrospectiva del Sprint 1 |
| #19 | 30 de septiembre | Bitácora de las seis sesiones de Daily Scrum |
| #20 | 30 de septiembre | Demo ejecutable del Sprint 1 y documentación de la API |

El código de las tres historias quedó alojado en la carpeta `agrovalle-connect-demo/`, separado del
proyecto Maven de la raíz. Se unificó en el paquete `com.agrovalle.connect` el 4 de noviembre de
2026, durante el Sprint 2, en el commit `0ecc3bc` integrado por el Pull Request #51.

Esta observación es la misma que la retrospectiva del Sprint 1 anticipó al señalar que los
artefactos del Sprint no permitían evaluar el uso de ramas, commits ni Pull Requests. El Sprint 2
corrigió la práctica: sus tres historias se integraron con un Pull Request cada una (#47, #50 y
#52), y el Sprint 3 añadió la aprobación obligatoria en las reglas de protección de rama.

## 4. Demostración técnica

### 4.1 Guion de la demostración

La demostración se ejecutó contra la aplicación en marcha, usando la documentación interactiva de
la API (`/swagger-ui.html`) con los esquemas descritos en [`sprint-1-api.md`](sprint-1-api.md).

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | `POST /api/v1/agricultores` con nombre, documento, correo, teléfono, municipio y contraseña | `201 Created` con el agricultor y su fecha de registro |
| 2 | `POST /api/v1/agricultores` con campos obligatorios vacíos o correo mal formado | `400 Bad Request` |
| 3 | `POST /api/v1/agricultores` repitiendo el documento del paso 1 | `409 Conflict` |
| 4 | `POST /api/v1/compradores` con razón social, NIT, correo, teléfono y contraseña | `201 Created` con el comprador registrado |
| 5 | `POST /api/v1/compradores` repitiendo el NIT del paso 4 | `409 Conflict` |
| 6 | `POST /api/v1/auth/login` con el correo y la contraseña del agricultor del paso 1 | `200 OK` con token, tipo `Bearer`, vigencia en segundos y rol |
| 7 | `POST /api/v1/auth/login` con una contraseña incorrecta | `401 Unauthorized` |
| 8 | `GET /api/v1/auth/me` con el token del paso 6 en la cabecera `Authorization` | `200 OK` con el correo y el rol del usuario del token |
| 9 | `GET /api/v1/auth/me` sin cabecera `Authorization` | `401 Unauthorized` |
| 10 | Consultar la tabla `usuario` en PostgreSQL | La contraseña aparece cifrada, nunca en texto plano |

El paso 10 es el que valida el objetivo del Sprint en su parte de persistencia: el registro se
guarda en PostgreSQL y la credencial nunca queda legible.

### 4.2 Seguridad y manejo del token

El esquema de seguridad demostrado es Bearer JWT. El inicio de sesión devuelve un `LoginResponse`
con cuatro campos: el token firmado, el tipo `Bearer`, la vigencia en segundos y el rol del usuario.
Los endpoints protegidos exigen la cabecera `Authorization: Bearer <token>`, y `GET /api/v1/auth/me`
sirve como prueba viva de ese contrato: devuelve el usuario asociado al token y responde `401` ante
la ausencia del token, un token inválido o uno expirado.

### 4.3 Arquitectura REST demostrada

El incremento evidenció la separación en capas que el Sprint Goal pedía validar: controladores REST
con rutas versionadas bajo `/api/v1`, DTO de entrada y de salida distintos de las entidades,
servicios con la lógica de registro y repositorios de Spring Data JPA sobre PostgreSQL. Las
respuestas de registro nunca incluyen la contraseña, ni siquiera cifrada.

## 5. Métricas de calidad

| Indicador | Resultado | Fuente |
|-----------|-----------|--------|
| Story Points comprometidos | 11 | `sprint-1-planning.md` |
| Story Points entregados | 11 | Demo ejecutable del Pull Request #20 |
| Endpoints entregados | 4 | `sprint-1-api.md` |
| Validación de estilo | Checkstyle configurado desde el 16 de septiembre y ejecutado en el hook `pre-commit` de Husky | `checkstyle.xml`, `.husky/pre-commit` |
| Validación del formato de commits | Hook `commit-msg` con Conventional Commits, desde el 16 de septiembre | `.husky/commit-msg` |
| Integración continua en GitHub Actions | No disponible durante el Sprint 1 | `ci.yml` se incorporó el 29 de octubre, Pull Request #44 |
| Cobertura automatizada con JaCoCo | No disponible durante el Sprint 1 | JaCoCo se incorporó con la unificación del proyecto, Pull Request #45 |

Durante el Sprint 1 la verificación de calidad fue local, apoyada en los hooks de Husky. Las cifras
de cobertura y de pruebas automatizadas que el proyecto reporta actualmente corresponden a
mediciones posteriores y no deben atribuirse a este Sprint.

## 6. Evidencias disponibles

| Evidencia | Ubicación |
|-----------|-----------|
| Planificación, capacidad y desglose de tareas técnicas | [`sprint-1-planning.md`](sprint-1-planning.md) |
| Documentación de los endpoints y esquemas del incremento | [`sprint-1-api.md`](sprint-1-api.md) |
| Bitácora de las seis sesiones de Daily Scrum | [`bitacora-daily-scrum.md`](bitacora-daily-scrum.md) |
| Retrospectiva del Sprint 1 | [`sprint-1-retrospective.md`](sprint-1-retrospective.md) |
| Historial de integración del incremento | Pull Requests #17 a #20 en el repositorio |
| Demo ejecutable tal como se presentó | Carpeta `agrovalle-connect-demo/` en el árbol del 30 de septiembre de 2026 |

## 7. Retroalimentación recibida

| Origen | Retroalimentación | Decisión |
|--------|-------------------|----------|
| Retrospectiva del equipo | El documento del Sprint presentaba inconsistencias entre el título, el texto y la selección de historias | Revisión cruzada del documento antes de cerrar cada Planning |
| Retrospectiva del equipo | HU-06 y HU-07 no incluyeron tareas de pruebas en su desglose | Al menos una tarea de pruebas por historia en los Sprints siguientes |
| Retrospectiva del equipo | La capacidad se comprometió por completo, sin margen para imprevistos | Evaluar un margen de capacidad en la planificación siguiente |
| Retrospectiva del equipo | Los artefactos del Sprint no registraban ramas, commits ni Pull Requests | Registrar la trazabilidad de la integración, aplicado desde el Sprint 2 |

La retroalimentación de la docente sobre este Sprint no quedó registrada por escrito en el
repositorio.

## 8. Estado del Product Backlog tras el Sprint

| Situación | Historias |
|-----------|-----------|
| Terminadas en el Sprint 1 | HU-01, HU-06, HU-07 |
| Siguientes en prioridad | HU-08, HU-02 y HU-04, seleccionadas después para el Sprint 2 |
| Pendientes en el backlog | Las demás historias de [`../BACKLOG.md`](../BACKLOG.md) |

## 9. Conclusión

El Scrum Team cerró el Sprint 1 con las tres historias comprometidas terminadas y demostrables: un
agricultor y un comprador comercial pueden registrarse, un usuario registrado obtiene un token JWT
y ese token da acceso a un endpoint protegido, con la información persistida en PostgreSQL y la
contraseña cifrada. Los 11 Story Points comprometidos se entregaron completos.

La deuda del Sprint no estuvo en el producto sino en el proceso de integración: el incremento entró
al repositorio como una carpeta de demo en un solo Pull Request documental, en lugar de una rama y
un Pull Request por historia de usuario. Esa práctica se corrigió en el Sprint 2 y se reforzó en el
Sprint 3 con reglas de protección de rama que exigen aprobación. Las demás observaciones del proceso
se analizan en la [retrospectiva del Sprint 1](sprint-1-retrospective.md).
