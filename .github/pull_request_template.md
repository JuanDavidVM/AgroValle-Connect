## Descripcion del cambio

<!-- Que se construyo y por que. Referencia la historia de usuario: Closes #<numero> / HU-XX -->

**Historia de Usuario:** HU-XX — <nombre>
**Rama origen:** `feature/HU-XX-<nombre>` → **Rama destino:** `develop`

## Criterios de aceptacion BDD cubiertos

- [ ] **Given** ...
- [ ] **When** ...
- [ ] **Then** ...

## Evidencia tecnica

- [ ] `mvn clean test` pasa en local (100% de las pruebas en verde)
- [ ] `mvn checkstyle:check` finaliza sin advertencias
- [ ] Cobertura JaCoCo >= 60%
- [ ] Commits bajo Conventional Commits
- [ ] Documentacion (`README.md` / `/docs`) actualizada si aplica
- [ ] No se incluyen `target/`, `.idea/` ni credenciales

<!-- Pega aqui la salida de la consola o una captura del build local -->

## Checklist de Peer Review (revisor)

- [ ] La arquitectura en capas (MVC) se respeta
- [ ] No se detectan code smells ni violaciones evidentes de SOLID
- [ ] Los nombres son descriptivos y el codigo es legible
- [ ] Las pruebas cubren el escenario BDD declarado

**Revisor asignado:** @<usuario>
