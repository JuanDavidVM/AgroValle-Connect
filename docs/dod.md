# Definition of Done (DoD) — AgroValle Connect

**Contrato técnico innegociable del equipo.** Ninguna Historia de Usuario se considera terminada, ni ningún Pull Request se fusiona a `develop` o `main`, hasta que **todos** los ítems de este checklist estén cumplidos.

Este DoD se fundamenta en los atributos de calidad de la norma **ISO/IEC 25010**, en particular *Mantenibilidad*, *Adecuación Funcional* y *Fiabilidad*.

---

## 1. Checklist de cumplimiento obligatorio

### Build Local
- [ ] El proyecto compila sin errores con Java 17: `mvn clean compile`.
- [ ] No existen dependencias declaradas sin uso ni versiones conflictivas en el `pom.xml`.
- [ ] La aplicación arranca correctamente: `mvn spring-boot:run`.

### Linter — Checkstyle (Mantenibilidad / Modularidad)
- [ ] `mvn checkstyle:check` finaliza en `BUILD SUCCESS`.
- [ ] **Cero advertencias** de estilo estático bajo las reglas de `checkstyle.xml` (Google Java Style adaptado).
- [ ] No se usan supresiones locales (`@SuppressWarnings`, comentarios `CHECKSTYLE:OFF`) para esquivar una regla.

### Pruebas (Adecuación Funcional / Fiabilidad)
- [ ] El **100%** de las pruebas unitarias e integradas existentes pasa: `mvn clean test`.
- [ ] La historia entregada incorpora al menos una prueba nueva que verifica su escenario BDD.
- [ ] La cobertura de código medida con **JaCoCo es igual o superior al 60%**: `mvn verify`.
- [ ] Se siguió el ciclo TDD *Red–Green–Refactor* cuando la historia lo permitía.

### Peer Review
- [ ] El cambio llegó a la rama principal mediante **Pull Request**, nunca por push directo.
- [ ] El PR fue **revisado y aprobado explícitamente por al menos un compañero** de equipo.
- [ ] El revisor inspeccionó la pestaña *Files changed* buscando *code smells*, respeto del patrón MVC y cumplimiento de los principios SOLID.
- [ ] Los comentarios técnicos del revisor fueron resueltos o discutidos antes del merge.
- [ ] El autor **no aprueba su propio** Pull Request.

### Documentación
- [ ] El `README.md` está actualizado si el cambio afecta ejecución, configuración o arquitectura.
- [ ] La documentación técnica de `/docs` refleja el estado real del sistema.
- [ ] Toda decisión arquitectónica relevante quedó registrada como **ADR** en `docs/adr/`.
- [ ] Las clases y métodos públicos tienen Javadoc.

### Conventional Commits
- [ ] Todos los mensajes siguen el estándar `tipo(alcance): descripción`.
- [ ] Se usan exclusivamente los tipos válidos: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.
- [ ] **Prohibidos** los mensajes genéricos: `cambios`, `avance`, `update`, `final`, `subiendo`, `arreglo`, `prueba`.
- [ ] El historial permite derivar el versionamiento semántico (`feat` → MINOR, `fix` → PATCH, `BREAKING CHANGE` → MAJOR).

### Husky — Automatización de Calidad
- [ ] El hook `.husky/pre-commit` está instalado y activo en la máquina de cada integrante.
- [ ] El hook ejecuta `mvn checkstyle:check` y `mvn test` y **bloquea el commit** si alguno falla.
- [ ] El hook `.husky/commit-msg` rechaza mensajes que no cumplan Conventional Commits.
- [ ] Nadie usa `--no-verify` para saltarse las validaciones.

### Cumplimiento de Criterios de Aceptación
- [ ] El escenario **Given–When–Then** declarado en `BACKLOG.md` se cumple de forma verificable.
- [ ] El contrato de API respeta lo especificado: endpoint, método HTTP, códigos de estado, validaciones y persistencia en PostgreSQL.
- [ ] Los endpoints protegidos exigen token **JWT** válido y responden `401 Unauthorized` sin él.
- [ ] El Product Owner del equipo validó el comportamiento observable de la historia.

### Higiene del Repositorio
- [ ] No se versionan `target/`, `node_modules/`, `.idea/`, `.vscode/` ni archivos `.iml`.
- [ ] No se versionan credenciales, claves ni archivos `.env`.
- [ ] La rama de trabajo se eliminó del remoto después del merge.

---

## 2. Cómo verificar el DoD en un solo paso

```bash
mvn clean verify
```

Este comando encadena compilación, Checkstyle (fase `validate`), pruebas JUnit 5 y la verificación de cobertura mínima de JaCoCo. Si termina en `BUILD SUCCESS`, los ítems técnicos automatizables del DoD están cumplidos; los de revisión humana (Peer Review, documentación, criterios de aceptación) se verifican en el Pull Request.

---

## 3. Aprobación y firma del equipo

Los abajo firmantes declaran haber leído, comprendido y aceptado este Definition of Done como contrato técnico vinculante del proyecto AgroValle Connect, y se comprometen a no fusionar código que no lo satisfaga en su totalidad.

| # | Nombre completo | Código / Usuario GitHub | Rol en el equipo | Fecha | Firma |
|---|-----------------|-------------------------|------------------|-------|-------|
| 1 | Juan David Vidal Muñoz | `@JuanDavidVM` | Product Owner | ____/____/______ | ______________ |
| 2 | Robert Andres Preciado | `@RR23desing` | Scrum Master | ____/____/______ | ______________ |
| 3 | Juan Esteban Quintero Berrio | `@JuanQuintero233` | QA | ____/____/______ | ______________ |
| 4 | Juan David Gonzales Mendez | `@JMendez222` | QA | ____/____/______ | ______________ |

**Docente:** Paola Andrea Bedoya Toro — Ingeniería de Software II
**Institución:** Institución Universitaria Antonio José Camacho (UNIAJC)

**Versión del DoD:** 1.0 — aprobada en el Sprint 0.
Cualquier modificación posterior requiere consenso del equipo y debe registrarse como una nueva versión de este documento mediante un commit `docs(dod): ...`.
