# Retrospectiva del Sprint 1

## 1. Objetivo de la retrospectiva

La retrospectiva tiene como propósito analizar la forma en que el equipo de desarrollo organizó y ejecutó el trabajo del Sprint 1, con el fin de identificar prácticas que conviene incorporar, abandonar o mantener. El análisis se centra en el proceso de trabajo y no en el detalle técnico de la implementación. Sus resultados alimentan la mejora continua del equipo mediante acciones concretas que puedan aplicarse y verificarse en el siguiente Sprint.

Los hallazgos se derivan exclusivamente de la información registrada en el documento del Sprint 1 de AgroValle Connect. Cuando una práctica no cuenta con evidencia en dicho documento, se indica de manera expresa y no se emiten conclusiones sobre ella.

## 2. Contexto del Sprint

- **Proyecto:** AgroValle Connect.
- **Sprint analizado:** Sprint 1.
- **Capacidad del Sprint:** 11 Story Points.
- **Sprint Goal:** "Habilitar el registro inicial de agricultores y la autenticación del inicio de sesión agrícolas y registro de compradores comerciales, validando la persistencia de la información en PostgreSQL y la arquitectura REST."
- **Historias trabajadas:**

| ID | Historia | Story Points |
| --- | --- | --- |
| HU-01 | Registro de Agricultores | 3 |
| HU-06 | Autenticación con JWT | 5 |
| HU-07 | Registro de Comprador Comercial | 3 |

## 3. Start — Empezar

Las siguientes prácticas se identificaron a partir de situaciones observables en la documentación del Sprint.

1. **Revisar la coherencia del documento del Sprint antes de darlo por cerrado.** Se observó que el apartado de propuesta de historias se titula como "3 historias", pero su texto introductorio menciona cinco historias y hace referencia a la gestión y consulta de productos agrícolas, funcionalidad que no forma parte del alcance seleccionado. Se propone realizar una revisión cruzada del documento antes de cada Planning para que título, texto, backlog y selección coincidan.
2. **Mantener la numeración de tareas técnicas sin vacíos.** En el desglose de HU-01 los identificadores pasan de T01.2 a T01.4, sin una tarea T01.3. Se propone verificar la secuencia de identificadores al elaborar el desglose y registrar si una tarea fue descartada o fusionada.
3. **Incluir tareas de pruebas en el desglose de cada historia.** Únicamente HU-01 contempla una tarea explícita de pruebas (T01.8). El desglose de HU-06 y de HU-07 no registra tareas de pruebas, pese a que el contexto del Sprint incluye pruebas automatizadas. Se propone que cada historia cuente con al menos una tarea de pruebas identificable y estimable.
4. **Redactar el Sprint Goal con una sola lectura posible.** La formulación actual combina en una misma oración el registro de agricultores, la autenticación y el registro de compradores, y su redacción admite interpretaciones distintas. Se propone revisar el Sprint Goal en voz alta durante el Planning y ajustarlo hasta que pueda explicarse sin aclaraciones adicionales.
5. **Delimitar el alcance de cada historia respecto de las demás.** La justificación de HU-07 menciona que los compradores podrán registrarse y autenticarse, mientras que la autenticación corresponde a HU-06. Se propone dejar escrito en cada historia qué queda dentro y fuera de su alcance, para evitar solapamientos al momento de implementar.

## 4. Stop — Dejar de hacer

1. **Dejar de redactar el documento del Sprint reutilizando texto de planificaciones previas sin contrastarlo.** La presencia de referencias a cinco historias y a productos agrícolas en un Sprint de tres historias sugiere que parte del texto no se actualizó al definir la selección final. No existe evidencia en las fuentes sobre el origen de esta discrepancia, por lo que la acción se limita a evitar que se repita.
2. **Dejar de comprometer la totalidad de la capacidad sin margen explícito.** La suma de las historias seleccionadas (3 + 5 + 3) coincide con la capacidad de 11 Story Points, de modo que el Sprint se planificó sin holgura. Las fuentes no contienen información sobre si esto generó dificultades durante la ejecución; por ello, la acción es preventiva: evaluar en el próximo Planning si conviene reservar un margen para imprevistos.
3. **Dejar de asumir que una historia está cubierta por pruebas si el desglose no lo indica.** La ausencia de tareas de pruebas en HU-06 y HU-07 no permite verificar su cobertura a partir del documento.

No se identificaron en las fuentes conflictos entre integrantes, incumplimientos de plazos ni incidentes de ejecución, por lo que no se registran acciones de este tipo.

## 5. Continue — Continuar

Las siguientes prácticas cuentan con evidencia en el documento del Sprint y se recomienda mantenerlas.

1. **Justificar la inclusión de cada historia en el Sprint.** El documento explica, para cada historia, el motivo por el cual entra en el Sprint, lo que facilita comprender las prioridades.
2. **Estimar las historias y compararlas con la capacidad disponible.** Las historias cuentan con Story Points y priorización MoSCoW, y su suma queda contrastada con la capacidad del Sprint.
3. **Desglosar cada historia en tareas técnicas asociadas a un componente y a un atributo de calidad ISO 25010.** Esta relación permite vincular el trabajo con criterios de calidad desde la planificación.
4. **Organizar el desglose por capas y responsabilidades.** Las tareas se agrupan por entidad, validación, repositorio, servicio, controlador, persistencia y manejo de errores, lo que ordena el trabajo de cada historia.
5. **Relacionar el Sprint Goal con la validación de la persistencia en PostgreSQL y de la arquitectura REST.** El objetivo incluye un criterio de validación técnica verificable.

Los archivos disponibles para esta retrospectiva no contienen información sobre el uso de ramas, Conventional Commits, Pull Requests, revisión de código ni seguimiento diario del Sprint. Por esta razón no se formulan conclusiones sobre el funcionamiento de dichas prácticas.

## 6. Hallazgos del Sprint

### Aspectos positivos

- Las historias seleccionadas se justificaron individualmente y se estimaron en Story Points.
- La capacidad del Sprint fue explícita y consistente con la suma de las historias.
- El desglose de tareas vinculó cada una con un componente tecnológico y un atributo de calidad ISO 25010.
- El Sprint Goal incorporó un criterio de validación técnica relacionado con la persistencia y la arquitectura REST.

### Dificultades identificadas

- Inconsistencia entre el título del apartado de propuesta de historias (tres) y su texto (cinco historias, con mención a productos agrícolas).
- Vacío en la numeración de tareas de HU-01 (falta T01.3).
- Desglose de tareas sin tareas de pruebas para HU-06 y HU-07.
- Redacción del Sprint Goal con posibilidad de interpretaciones distintas.
- Posible solapamiento de alcance entre HU-06 y HU-07 respecto de la autenticación de compradores.

### Oportunidades de mejora

- Realizar una revisión cruzada del documento del Sprint antes de cada Planning.
- Verificar la secuencia de identificadores de tareas y registrar las tareas descartadas.
- Incluir tareas de pruebas en el desglose de todas las historias.
- Revisar y simplificar la redacción del Sprint Goal.
- Declarar el alcance incluido y excluido de cada historia.
- Evaluar un margen de capacidad en el próximo Planning.
- Registrar en el seguimiento del Sprint evidencia del uso de ramas, commits y Pull Requests, de modo que la próxima retrospectiva pueda evaluarlos.

## 7. Acciones para el siguiente Sprint

| Acción | Responsable | Momento de aplicación |
| ------ | ----------- | --------------------- |
| Realizar una revisión cruzada del documento del Sprint (título, texto, backlog y selección) antes de cerrar el Planning | Equipo de desarrollo | Próximo Sprint |
| Verificar que la numeración de tareas no tenga vacíos y registrar las tareas descartadas o fusionadas | Equipo de desarrollo | Próximo Sprint |
| Incluir al menos una tarea de pruebas por historia en el desglose técnico | Equipo de desarrollo | Próximo Sprint |
| Revisar la redacción del Sprint Goal hasta que admita una sola interpretación | Equipo de desarrollo | Próximo Sprint |
| Documentar para cada historia el alcance incluido y excluido | Equipo de desarrollo | Próximo Sprint |
| Evaluar un margen de capacidad respecto de los Story Points comprometidos | Equipo de desarrollo | Próximo Sprint |
| Mantener la justificación por historia, la estimación frente a la capacidad y el vínculo de cada tarea con un atributo ISO 25010 | Equipo de desarrollo | Próximo Sprint |
| Registrar en el seguimiento del Sprint el uso de ramas, commits y Pull Requests para poder evaluarlos en la siguiente retrospectiva | Equipo de desarrollo | Próximo Sprint |

## 8. Conclusión

El análisis del Sprint 1 permitió identificar que las principales oportunidades de mejora se concentran en la preparación y consistencia de la documentación del Sprint, en la inclusión sistemática de tareas de pruebas y en la delimitación del alcance entre historias. Como prácticas a mantener se determinó la justificación de cada historia, la estimación frente a la capacidad y la asociación de las tareas con atributos de calidad. Dado que las fuentes disponibles no registran información sobre ramas, commits, Pull Requests ni seguimiento diario, se propone incorporar su registro para fundamentar la evaluación en futuras retrospectivas. La aplicación de las acciones definidas en el Sprint 2 permitirá verificar su efecto y sostener el ciclo de mejora continua del equipo.
