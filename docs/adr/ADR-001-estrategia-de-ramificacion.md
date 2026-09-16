# ADR-001: Adopcion de GitFlow como estrategia de ramificacion

- **Estado:** Aceptada
- **Fecha:** Sprint 0
- **Decisores:** Equipo de desarrollo de AgroValle Connect

## Contexto

La Guia de Trabajo Practico V2 plantea dos alternativas de control de versiones:
Trunk-Based Development y GitFlow. La eleccion debe justificarse en el `README.md`
explicando como minimiza tiempos de espera y previene conflictos de fusion extensos.

El equipo trabaja en un entorno academico con las siguientes caracteristicas:

- Integrantes con disponibilidad asincrona (no hay integracion varias veces al dia).
- Entregas evaluadas por sesiones, con incrementos versionados.
- Peer Review obligatorio antes de fusionar a las ramas principales.
- El pipeline de CI en GitHub Actions se habilita plenamente en la Sesion 10.

## Decision

Se adopta **GitFlow** con las ramas `main`, `develop`, `feature/HU-XX-*` y
`release/vX.X.X`.

## Justificacion

Trunk-Based Development exige integracion continua real y disciplina extrema con
ramas de vida muy corta; sin un pipeline de CI activo desde el primer dia, un error
integrado directamente rompe la linea principal para todo el equipo. GitFlow aisla
cada historia de usuario en su propia rama, mantiene `main` siempre desplegable y
permite versionar los incrementos evaluados mediante ramas `release/`.

El riesgo conocido de GitFlow es la deuda por integracion tardia. Se mitiga con dos
politicas del equipo:

1. Una rama por historia de usuario, con vida maxima de una sesion de trabajo.
2. Sincronizacion obligatoria con `develop` antes de abrir el Pull Request.

## Consecuencias

**Positivas:** `main` protegida y siempre estable; trazabilidad directa entre rama,
historia de usuario y Pull Request; versionado semantico natural con las ramas de release.

**Negativas:** mayor numero de fusiones que en Trunk-Based; requiere disciplina para
que las ramas no acumulen dias de divergencia.
