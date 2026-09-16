# Sprint 0 — Ejecución en Git y evidencia de Peer Review

Este documento contiene la secuencia exacta que el equipo debe ejecutar para que el repositorio quede con un historial profesional, ramas GitFlow y Pull Requests revisados. **El contenido de los archivos ya está creado; lo que falta es versionarlo correctamente.**

> Regla de oro: no hacer un único commit gigante con todo. El criterio 1 de la rúbrica penaliza directamente los "commits masivos de última hora".

---

## Fase 1 — Configuración de identidad (cada integrante, una sola vez)

```bash
git config --global user.name "Tu Nombre Completo"
git config --global user.email "tu.correo@uniajc.edu.co"

ssh-keygen -t ed25519 -C "tu.correo@uniajc.edu.co"
cat ~/.ssh/id_ed25519.pub
# Pegar en GitHub > Settings > SSH and GPG keys > New SSH Key
```

## Fase 2 — Inicialización del repositorio (Scrum Master o líder técnico)

Crear el repositorio `agrovalle-connect` en GitHub **sin** README ni .gitignore automáticos. Luego, en la carpeta del proyecto:

```bash
git init
git branch -M main
git remote add origin git@github.com:JuanDavidVM/AgroValle-Connect.git
```

## Fase 3 — Commits base sobre `main`

```bash
git add .gitignore
git commit -m "chore: inicializar estructura de carpetas y gitignore"

git add pom.xml
git commit -m "build(maven): configurar proyecto Java 17 con Spring Boot y dependencias base"

git add src/
git commit -m "feat(health): agregar endpoint de verificacion de estado del sistema"

git add checkstyle.xml checkstyle-suppressions.xml
git commit -m "chore(config): configurar Checkstyle con reglas de Google Java Style"

git add package.json .husky/
git commit -m "ci(husky): agregar hooks pre-commit y commit-msg de validacion de calidad"

git push -u origin main
```

## Fase 4 — Crear `develop` y proteger `main`

```bash
git checkout -b develop
git push -u origin develop
```

En GitHub → **Settings > Branches > Add branch ruleset** para `main` y `develop`:

- [x] Require a pull request before merging
- [x] Require approvals: **1**
- [x] Dismiss stale pull request approvals when new commits are pushed
- [x] Require status checks to pass before merging → seleccionar el job `build` del workflow de CI
- [x] Block force pushes

Esta configuración es la evidencia objetiva de que nadie puede saltarse el Peer Review.

## Fase 5 — Ramas de feature y Pull Requests

Cada entregable documental viaja en su propia rama, con su propio PR y su propio revisor. Así se genera la evidencia que exige el criterio 3 de la rúbrica.

### PR #1 — Documentación del repositorio (autor @JuanDavidVM, revisor @RR23desing)

```bash
git checkout develop && git pull origin develop
git checkout -b feature/sprint0-readme
git add README.md
git commit -m "docs(readme): documentar vision del producto, estrategia GitFlow y diagrama Mermaid"
git push origin feature/sprint0-readme
```

### PR #2 — Product Backlog (autor @RR23desing, revisor @JuanQuintero233)

```bash
git checkout develop && git pull origin develop
git checkout -b feature/sprint0-backlog
git add BACKLOG.md
git commit -m "docs(backlog): agregar 15 historias de usuario con MoSCoW, BDD y story points"
git push origin feature/sprint0-backlog
```

### PR #3 — Definition of Done y ADR (autor @JuanQuintero233, revisor @JMendez222)

```bash
git checkout develop && git pull origin develop
git checkout -b feature/sprint0-dod
git add docs/dod.md docs/adr/
git commit -m "docs(dod): formalizar definition of done y registrar ADR de ramificacion"
git push origin feature/sprint0-dod
```

### PR #4 — Pipeline de Integración Continua (autor @JMendez222, revisor @JuanDavidVM)

```bash
git checkout develop && git pull origin develop
git checkout -b feature/sprint0-ci
git add .github/
git commit -m "ci(github-actions): agregar pipeline de build, linter, pruebas y cobertura"
git push origin feature/sprint0-ci
```

### En GitHub, para cada rama

1. Clic en **Compare & pull request**. Base: `develop`.
2. Título descriptivo (ej. `docs(backlog): 15 historias de usuario con BDD y estimación`).
3. Descripción: usar la plantilla `.github/pull_request_template.md`, detallando qué se construyó, qué criterios BDD cubre y que las validaciones locales pasaron.
4. **Assignees:** el autor. **Reviewers:** uno o dos compañeros distintos del autor.
5. El revisor entra a *Files changed*, **deja al menos un comentario técnico en una línea concreta** y luego *Review changes > Approve*.
6. Merge Pull Request → Confirm Merge → **Delete branch**.

> El comentario técnico no es decorativo: la rúbrica distingue "Excelente" de "Aceptable" precisamente por la existencia de comentarios y discusión en la revisión, no solo por la aprobación.

## Fase 6 — Release del Sprint 0

```bash
git checkout develop && git pull origin develop
git checkout -b release/v0.1.0
git push origin release/v0.1.0
# PR de release/v0.1.0 hacia main, aprobado por un par
git checkout main && git pull origin main
git tag -a v0.1.0 -m "Sprint 0: repositorio profesional, backlog y politicas de calidad"
git push origin v0.1.0
# Finalmente, fusionar release/v0.1.0 de vuelta a develop
```

---

## Verificación final del Sprint 0

```bash
mvn clean test          # 100% de pruebas en verde
mvn checkstyle:check    # BUILD SUCCESS, cero advertencias
mvn clean verify        # incluye la verificación de cobertura de JaCoCo
```

| # | Requisito | Dónde se evidencia |
|---|-----------|--------------------|
| 1 | Proyecto Java 17 + Spring Boot | `pom.xml`, `src/main/java` |
| 2 | Maven configurado | `pom.xml` |
| 3 | Git y GitHub configurados | Fases 1 y 2 |
| 4 | Conventional Commits | `git log --oneline` |
| 5 | Estrategia de branching documentada | `README.md` §7, `docs/adr/ADR-001` |
| 6 | Pull Requests y Peer Review | Pestaña *Pull requests* del repositorio |
| 7 | README profesional | `README.md` |
| 8 | Diagrama Mermaid | `README.md` §7.2 |
| 9 | `.gitignore` | Raíz del repositorio |
| 10 | `checkstyle.xml` | Raíz del repositorio |
| 11 | Husky con `.husky/pre-commit` | `.husky/` |
| 12 | BACKLOG con 15 historias | `BACKLOG.md` |
| 13 | BDD Given-When-Then en las 15 | `BACKLOG.md` |
| 14 | Priorización MoSCoW | `BACKLOG.md` (tabla resumen) |
| 15 | Story Points Fibonacci | `BACKLOG.md` (tabla resumen) |
| 16 | `docs/dod.md` | `docs/dod.md` |
| 17 | DoD firmado por el equipo | `docs/dod.md` §3 |

**Comprobaciones de higiene antes de entregar:**

```bash
git ls-files | grep -E "target/|\.idea/|node_modules/|\.env" # no debe devolver nada
git log --oneline                                            # ningún mensaje genérico
git branch -a                                                # main, develop y ramas de release
```
