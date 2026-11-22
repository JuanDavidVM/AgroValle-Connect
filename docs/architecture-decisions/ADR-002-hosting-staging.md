# ADR-002: Hosting de staging en Render con contenedor Docker

## Estado

**Aprobado.** 22 de noviembre de 2026. Implementada y en operación.

Supera la propuesta inicial del Sprint 3, que planteaba documentar la selección de
plataforma como ADR-001; ese número ya estaba asignado a la decisión de ramificación
(`docs/adr/`), por lo que esta decisión se numera como ADR-002.

El despliegue descrito aquí está en funcionamiento y verificado:
`GET https://agrovalle-connect-dsr6.onrender.com/api/v1/productos` responde 200 con la
lista de productos en JSON.

## Contexto

El corte 3 exige un entorno de staging accesible desde internet para la demostración del
incremento (requisitos R-12.3 y R-13.4). La verificación acordada con la docente es una
llamada HTTP al endpoint público de productos, sin pasos manuales previos.

Las fuerzas que condicionaron la decisión:

- **Presupuesto cero.** El proyecto es académico y el equipo son cuatro estudiantes. La
  plataforma debía ofrecer un plan gratuito que no exigiera registrar tarjeta de crédito.
- **Aplicación Spring Boot sobre Java 17.** El artefacto de despliegue es un JAR ejecutable
  construido con Maven, no una aplicación Node o Python, que es lo que la mayoría de los
  planes gratuitos soporta de forma nativa.
- **Base de datos ya existente en Supabase.** El PostgreSQL del proyecto estaba operando en
  Supabase desde el Sprint 1, con el esquema, los datos de prueba y las credenciales ya
  propagadas al equipo. Mover la base de datos habría invalidado trabajo hecho.
- **Despliegue reproducible y trazable.** El pipeline de CI (`ci.yml`) ya corría pruebas,
  Checkstyle y JaCoCo sobre cada PR. El despliegue tenía que colgar de ese pipeline, no
  ejecutarse a mano desde el panel de la plataforma, para que cada versión en staging
  tuviera una ejecución de CI verde detrás.
- **Recursos limitados en el plan gratuito.** Cualquier opción sin costo implica un
  contenedor pequeño, con memoria y CPU acotadas, y suspensión por inactividad.

## Decisión

Staging se despliega en **Render**, plan gratuito, como servicio web basado en contenedor,
con la siguiente configuración:

1. **Empaquetado con `Dockerfile` multietapa.** Render no ofrece runtime nativo de Java, de
   modo que el servicio no puede construirse con un buildpack. La primera etapa compila con
   Maven sobre JDK 17 y produce el JAR; la segunda copia únicamente ese JAR sobre una imagen
   JRE 17. Un `.dockerignore` mantiene el contexto de build limitado al código fuente.
2. **JVM ajustada al contenedor.** El plan gratuito asigna 512 MB de RAM y 0.1 CPU. El
   arranque usa `-XX:MaxRAMPercentage=70`, para que el heap se calcule sobre el límite real
   del contenedor y deje margen al metaspace y a los hilos, y `-XX:+UseSerialGC`, porque los
   recolectores paralelos no aportan nada con una fracción de CPU y sí añaden hilos y
   consumo de memoria.
3. **La base de datos permanece en Supabase.** No se usa el PostgreSQL gratuito de Render:
   expira a los 30 días, lo que obligaría a recrear la base y rotar credenciales en medio
   del corte. La conexión se hace por JDBC con SSL hacia Supabase.
4. **Auto-Deploy de Render desactivado.** El único camino hacia staging es el workflow
   `cd.yml`, que se dispara por `workflow_run` cuando CI termina correctamente e invoca el
   deploy hook de Render a través del secret `RENDER_DEPLOY_HOOK_URL`. Render no observa el
   repositorio por su cuenta, de modo que no existe forma de publicar un commit que no haya
   pasado por el pipeline.
5. **Estrategia de despliegue: reemplazo de instancia única.** De las estrategias de
   minimización de riesgo evaluadas, Blue-Green exige duplicar la infraestructura completa y
   Canary requiere un balanceador capaz de repartir el tráfico por porcentaje; ninguna es
   viable con una sola instancia en plan gratuito. Rolling tampoco aplica porque no existe
   cluster de nodos. El despliegue sustituye el contenedor en ejecución por la nueva imagen,
   con una ventana breve de indisponibilidad que es aceptable en un entorno de demostración.
   La reversión consiste en redesplegar el commit anterior desde el historial de `cd.yml`.
6. **Verificación posterior al despliegue.** Un despliegue se considera correcto cuando
   `GET /api/v1/productos` responde 200 con la lista en JSON. Esa llamada es el healthcheck
   funcional del servicio y la evidencia que se adjunta al cierre del corte.
7. **Configuración por variables de entorno.** `SUPABASE_DB_URL`, `SUPABASE_DB_USERNAME`,
   `SUPABASE_DB_PASSWORD`, `JWT_SECRET` y `PORT` son obligatorias; `CORS_ORIGINS`,
   `SWAGGER_PUBLICO`, `DDL_AUTO` y `JWT_EXPIRATION` son opcionales y tienen valor por
   defecto en el perfil de staging. Ningún secreto vive en el repositorio.

## Consecuencias

### A favor

- Staging tiene una URL pública estable y sin costo, suficiente para la demostración del
  corte y para pruebas manuales del equipo.
- El contenedor es el mismo artefacto en local y en staging, de modo que un fallo de entorno
  se reproduce en la máquina del desarrollador con `docker build` y `docker run`.
- Con Auto-Deploy apagado, lo que está publicado en staging corresponde siempre a un commit
  con CI verde. El historial de ejecuciones de `cd.yml` es la bitácora de despliegues.
- La separación entre cómputo (Render) y datos (Supabase) permite redesplegar o recrear el
  servicio sin tocar la base de datos.

### En contra

- **Arranque en frío.** El servicio se suspende a los 15 minutos sin tráfico. La primera
  petición después de la suspensión levanta el contenedor y puede tardar decenas de
  segundos. Para la demostración hay que calentar el servicio con una llamada previa.
- **Mantenimiento del `Dockerfile`.** Al no haber runtime nativo de Java, cada cambio de
  versión de JDK o de estrategia de build obliga a modificar el `Dockerfile`, trabajo que un
  buildpack habría absorbido.
- **Margen de memoria estrecho.** Con 512 MB y 0.1 CPU no hay espacio para pruebas de carga
  ni para medir rendimiento. Los tiempos observados en staging no son representativos.
- **Dos proveedores en lugar de uno.** Render y Supabase se administran por separado, con dos
  paneles, dos juegos de credenciales y latencia de red entre ambos en cada consulta.
- **Sin SLA ni respaldos gestionados.** El plan gratuito no garantiza disponibilidad. Staging
  es un entorno de demostración, no un precedente para producción.

### Riesgos aceptados

- Swagger UI queda accesible en staging, controlado por la variable `SWAGGER_PUBLICO`.
  Cerrarlo impediría la demostración, porque Swagger UI se autentica por cabecera y no por
  cookie: con 401 la página no carga. El riesgo es acotado porque staging no contiene datos
  reales de usuarios.
- Los secretos se administran manualmente en el panel de Render y en GitHub Secrets. Una
  rotación exige actualizar ambos lugares y el `.env` local, como ocurrió al rotar las
  credenciales de Supabase y el `JWT_SECRET` el 22 de noviembre de 2026.

### Seguimiento

- Si el proyecto requiere un entorno con disponibilidad continua, la revisión del plan de
  Render queda para el Sprint 4; esta decisión no la prejuzga.
- El `Dockerfile` y `cd.yml` son los artefactos que materializan esta decisión. Cualquier
  cambio en la topología de despliegue debe reflejarse en el diagrama de despliegue del
  documento UML y, si altera lo decidido aquí, en un ADR que supere a este.

## Referencias

- `Dockerfile` y `.dockerignore` en la raíz del repositorio (PR #61).
- `.github/workflows/cd.yml` (PR #62).
- `docs/architecture/diagramas-uml.md`, diagrama de despliegue.
- `docs/adr/ADR-001` sobre estrategia de ramificación.
- Formato: Michael Nygard, *Documenting Architecture Decisions* (2011).
