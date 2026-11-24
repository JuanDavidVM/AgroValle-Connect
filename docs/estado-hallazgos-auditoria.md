# Estado final de los hallazgos de auditoría — Corte 3

AgroValle Connect · Ingeniería de Software II · UNIAJC · período 2-2026
Fecha de cierre: 23 de noviembre de 2026.

La [auditoría integral del repositorio](https://claude.ai/artifact/19rhJUqDVvCA6w2mHRNJo6), con fecha
del 21 de noviembre de 2026, registró 31 hallazgos. Este documento deja constancia del estado de
cada uno al cierre del corte y del commit o la acción que lo resolvió.

## Resumen

| Estado | Cantidad | Hallazgos |
| --- | --- | --- |
| Resueltos y verificados | 23 | AV-001 a AV-014, AV-016, AV-019 a AV-021, AV-023, AV-025, AV-026, AV-028, AV-031 |
| Aceptados sin corrección | 1 | AV-015 |
| Retirados por falso positivo | 1 | AV-029 |
| Diferidos al Sprint 4 | 4 | AV-022, AV-024, AV-027, AV-030 |
| Por confirmar en la copia local | 2 | AV-017, AV-018 |

`feature/sprint1-readme` se conserva a propósito: tiene 4 commits que no están en `main` y es la
arquitectura inicial del proyecto, de modo que su borrado queda a decisión de su autor.

Las dos incidencias de severidad **crítica** y las once de severidad **alta** están cerradas. Los
cinco hallazgos diferidos son de severidad media y ninguno bloquea los requisitos del corte.

## Detalle

| ID | Sev. | Hallazgo | Estado | Resolución |
| --- | --- | --- | --- | --- |
| AV-001 | Crítica | `application-staging.yml` es YAML inválido | Resuelto | PR #58, rama `fix/perfil-staging`, con la prueba `PerfilesYamlTest` que impide la regresión |
| AV-002 | Crítica | El perfil staging declara `security.jwt.*` y el código lee `app.jwt.*` | Resuelto | PR #58; las claves pasaron a `app.jwt.*`, que es lo que `JwtService` lee |
| AV-003 | Alta | `.env` con credenciales pobladas distribuido fuera de Git | Resuelto | Credenciales de Supabase y `JWT_SECRET` rotadas el 22 de noviembre y propagadas a Render y al `.env` local |
| AV-004 | Alta | No existe job ni workflow de despliegue continuo | Resuelto | PR #62, `cd.yml` disparado por `workflow_run` al terminar CI en verde sobre `main` |
| AV-005 | Alta | `develop` abandonada: el GitFlow documentado no se usa | Resuelto | PR #56; `develop` reactivada como rama de integración y mantenida al día desde entonces |
| AV-006 | Alta | 9 de 14 PR fusionados por el autor de la rama, ciclo medio de 7,2 min | Resuelto | Ruleset de `develop` con una aprobación obligatoria antes de fusionar |
| AV-007 | Alta | Commit directo a `main` sin Pull Request | Resuelto | Rulesets de `main` y `develop` que exigen Pull Request |
| AV-008 | Alta | `sprint-2-review.md` con 7 marcadores de evidencia sin rellenar | Resuelto | PR #73; los siete marcadores reemplazados por capturas reales y cifras medidas |
| AV-009 | Alta | Contradicción de cobertura dentro del mismo review | Resuelto | PR #73; se elimina el "superior al 80 %" y queda la cifra de una ejecución limpia |
| AV-010 | Alta | Falta `docs/sprint-1-review.md` | Resuelto | PR #70; ceremonia documentada de forma retroactiva a partir de los artefactos versionados |
| AV-011 | Alta | HU-01: el escenario BDD declara un endpoint que no existe | Resuelto | PR #78; `BACKLOG.md` pasa a declarar `POST /api/v1/agricultores`, el endpoint que el código expone. Se corrigió también HU-07, que tenía el mismo defecto |
| AV-012 | Alta | Falta `docs/metrics/sprint-metrics-corte3.md` | Resuelto | PR #74; informe con velocidad, Cycle Time medido sobre el historial de Git e indicadores de calidad |
| AV-013 | Alta | Falta el ADR de hosting y estrategia de despliegue | Resuelto | PR #67, `docs/architecture-decisions/ADR-002-hosting-staging.md` en formato Nygard |
| AV-014 | Alta | Faltan 4 de los 7 diagramas Mermaid y la ruta no coincide | Resuelto | PR #69; los 7 diagramas en `docs/architecture/diagramas-uml.md`, verificados con `mermaid-cli` |
| AV-015 | Alta | 5 commits fuera de Conventional Commits por ediciones desde la web | Aceptado | El historial no se reescribe. La práctica está corregida hacia adelante con el hook `commit-msg` |
| AV-016 | Media | 18 ramas `feature/*` fusionadas y no borradas | Resuelto | Las 18 originales y las 14 acumuladas en los Bloques C, D y E se borraron tras fusionarse. Al cierre el remoto solo conserva `main`, `develop` y `feature/sprint1-readme` |
| AV-017 | Media | Dos archivos sin rastrear contradicen los `.properties` versionados | Por confirmar | Hallazgo de la copia local: comprobar que `application.yml` y su variante de staging sin rastrear ya no existan en el directorio de trabajo |
| AV-018 | Media | La copia local va 3 commits por detrás del remoto | Por confirmar | Comprobar con `git rev-list --count develop..origin/main`, que debe dar 0 o 1 según el último merge |
| AV-019 | Media | 120 archivos con diff falso por CRLF, sin `.gitattributes` | Resuelto | PR #59; `.gitattributes` con normalización de fin de línea |
| AV-020 | Media | Sin configuración CORS | Resuelto | PR #64; CORS configurable por entorno mediante `CORS_ORIGINS` |
| AV-021 | Media | Swagger en `permitAll()` sin condición de perfil | Resuelto | PR #64; exposición condicionada a la variable `SWAGGER_PUBLICO` |
| AV-022 | Media | Sin seguridad a nivel de método | Diferido | Sprint 4. La autorización por ruta en `SecurityConfig` cubre los siete endpoints actuales |
| AV-023 | Media | `ddl-auto=update` sin herramienta de migraciones versionadas | Resuelto | Flyway gestiona el esquema desde `src/main/resources/db/migration/`, con `V1` como baseline de la base existente y `V2` para el cambio de `mensajes`. `ddl-auto` pasa a `validate` |
| AV-024 | Media | 7 tablas de `schema.sql` sin entidad JPA | Diferido | Sprint 4. Corresponden a las historias HU-09 a HU-15, fuera del alcance del corte |
| AV-025 | Media | `GlobalExceptionHandler` es la clase con más instrucciones sin cubrir | Resuelto | PR #78; pruebas de los siete manejadores. El paquete `exception` pasó de 87 % a 100 % y las instrucciones sin cubrir bajaron de 50 a 32 |
| AV-026 | Media | La plantilla de PR declara destino `develop` y todo va a `main` | Resuelto | Con `develop` reactivada, la plantilla describe el flujo real |
| AV-027 | Media | Sin `CODEOWNERS`, plantillas de issue ni Dependabot | Diferido | Sprint 4 |
| AV-028 | Baja | Checkstyle se ejecuta dos veces por pipeline | Resuelto | PR #62; el paso redundante se eliminó de `ci.yml` |
| AV-029 | Baja | `pom.xml` usa `<n>` en lugar de `<name>` | Retirado | Falso positivo de la auditoría: la etiqueta siempre estuvo correcta |
| AV-030 | Baja | Tabla `usuario` en singular frente al resto en plural | Diferido | Sprint 4. Ahora es viable: el renombrado entraría como una migración de Flyway |
| AV-031 | Baja | Rama `release/v0.1.0` muerta y 3 ramas fuera de convención | Resuelto | `release/v0.1.0`, `JuanDavidVM-patch-1`, `Hu-01-Registro-de-Agricultores` y `Hu-06-Autenticación-Con-jwt` eliminadas del remoto |

## Incidencia posterior al cierre de la auditoría

El 23 de noviembre el servicio de staging dejó de alcanzar la base de datos: la conexión TCP se
abría y el servidor la cerraba durante el handshake TLS. Se descartaron por eliminación el código,
las credenciales, el usuario del pooler, las restricciones de red y el propio pooler, que aceptaba
la misma conexión desde otra red. Recrear el servicio en otra región de Render lo resolvió, con el
cambio de subdominio correspondiente. El diagnóstico completo y la limitación que expone están
registrados en las consecuencias del [ADR-002](architecture-decisions/ADR-002-hosting-staging.md).
