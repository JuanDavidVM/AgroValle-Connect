# Product Backlog — AgroValle Connect

Producto: plataforma web (Java 17 / Spring Boot) que conecta la oferta agrícola de las fincas del Valle del Cauca con la demanda comercial urbana de Cali y sus alrededores.

- **Priorización:** técnica MoSCoW (M = Must Have, S = Should Have, C = Could Have, W = Won't Have).
- **Estimación:** Story Points en escala de Fibonacci (1, 2, 3, 5, 8, 13), acordados por consenso mediante **Planning Poker**.
- **Criterios de aceptación:** especificación BDD en formato Given–When–Then con contratos técnicos de API REST (endpoint, método HTTP, JWT, código de estado, persistencia en PostgreSQL).
- **Auditoría INVEST:** las 15 historias fueron revisadas para ser Independientes, Negociables, Valiosas, Estimables, Pequeñas y Evaluables.

---

## Tabla resumen

| ID | Historia de Usuario | Módulo | MoSCoW | Story Points |
|----|---------------------|--------|--------|--------------|
| HU-01 | Registro de Agricultores | Productores y Ofertas | **M** | 5 |
| HU-02 | Publicación de Productos (lotes de cosecha) | Productores y Ofertas | **M** | 5 |
| HU-03 | Visualización de Precios Regionales | Catálogo y Búsqueda | **S** | 8 |
| HU-04 | Filtro de Categorías y Municipios | Catálogo y Búsqueda | **M** | 3 |
| HU-05 | Contacto Directo / Intención de Compra | Pedidos Directos | **M** | 5 |
| HU-06 | Autenticación con JWT | Productores y Ofertas | **M** | 5 |
| HU-07 | Registro de Comprador Comercial | Pedidos Directos | **M** | 3 |
| HU-08 | Registro de Finca por Municipio | Productores y Ofertas | **M** | 3 |
| HU-09 | Detalle de una Oferta Publicada | Catálogo y Búsqueda | **S** | 2 |
| HU-10 | Reserva de Inventario y Control de Stock | Pedidos Directos y Stock | **M** | 8 |
| HU-11 | Consolidación del Carrito de Compra | Pedidos Directos y Stock | **S** | 5 |
| HU-12 | Emisión de Orden de Compra Directa | Pedidos Directos y Stock | **M** | 8 |
| HU-13 | Confirmación de Alistamiento del Lote | Logística y Trazabilidad | **S** | 3 |
| HU-14 | Programación de Ruta de Despacho | Logística y Trazabilidad | **C** | 8 |
| HU-15 | Seguimiento en Tiempo Real del Despacho | Logística y Trazabilidad | **W** | 13 |

**Total estimado del Product Backlog: 84 Story Points.**

Distribución MoSCoW: **9 Must (45 SP) · 4 Should (18 SP) · 1 Could (8 SP) · 1 Won't (13 SP)**. El primer incremento funcional (Sesión 6) se construye sobre HU-01, HU-06 y HU-02.

---

## HU-01 — Registro de Agricultores

**Historia:**
Como Agricultor, quiero registrarme en la plataforma, para ofrecer mis productos directamente a los compradores urbanos.

**Priorización:** M

**Estimación:** 5 Story Points

**Escenario BDD:**

* **Given:** que el usuario no registrado ingresa al endpoint `POST /api/v1/auth/register`.
* **When:** envía un JSON con `nombre`, `ubicacion_valle` y `cedula` válida (numérica, entre 6 y 10 dígitos, no existente en la base de datos).
* **Then:** el sistema responde con status `201 Created`, retorna el `id_agricultor` generado y el registro persiste en la tabla `agricultores` de PostgreSQL con la contraseña almacenada mediante hash BCrypt.

*Justificación de la estimación:* CRUD sencillo, pero suma el hash de credenciales, las validaciones de unicidad de cédula y la prueba automatizada en JUnit 5.

---

## HU-02 — Publicación de Productos

**Historia:**
Como Agricultor, quiero publicar mis cosechas disponibles, para que sean visibles ante comerciantes y restaurantes de Cali.

**Priorización:** M

**Estimación:** 5 Story Points

**Escenario BDD:**

* **Given:** un agricultor autenticado con token JWT válido en el header `Authorization: Bearer <token>`.
* **When:** envía `POST /api/v1/productos` con `tipo`, `categoria`, `cantidad_kg`, `precio_unitario` y `fecha_cosecha`.
* **Then:** el sistema valida que `fecha_cosecha` no sea anterior a la fecha actual y que `cantidad_kg` sea mayor a cero, responde `201 Created` y retorna un ID de producto único persistido en PostgreSQL.

*Justificación de la estimación:* consenso del equipo en Planning Poker (votos 3-5-8 → 5), por la validación de token JWT más las reglas de negocio sobre la fecha.

---

## HU-03 — Visualización de Precios Regionales

**Historia:**
Como Usuario de la plataforma, quiero ver los precios promedio del Valle por producto, para negociar mejor y detectar precios injustos.

**Priorización:** S

**Estimación:** 8 Story Points

**Escenario BDD:**

* **Given:** que existen al menos 50 transacciones registradas del producto "Café" en las últimas 24 horas dentro de PostgreSQL.
* **When:** el usuario solicita `GET /api/v1/precios/promedio?producto=Cafe`.
* **Then:** el sistema calcula la media aritmética del precio unitario sobre esa ventana de tiempo, responde `200 OK` y despliega el valor exacto en pesos colombianos (COP) junto con el número de transacciones consideradas.

*Justificación de la estimación:* requiere consulta agregada con ventana temporal, manejo del caso sin transacciones y pruebas con datos precargados.

---

## HU-04 — Filtro de Categorías y Municipios del Valle

**Historia:**
Como Comprador, quiero filtrar las cosechas por municipio (Dagua, Palmira, Buga, Tuluá, Caicedonia, Jamundí) y categoría, para encontrar productos locales de mi interés rápidamente.

**Priorización:** M

**Estimación:** 3 Story Points

**Escenario BDD:**

* **Given:** que existen productos registrados en PostgreSQL bajo el municipio "Dagua" y la categoría "Frutas" con estado `ACTIVA`.
* **When:** el usuario realiza una petición `GET /api/v1/productos?municipio=Dagua&categoria=Frutas`.
* **Then:** el sistema responde con status `200 OK` y un arreglo JSON que contiene únicamente las ofertas activas correspondientes; si ningún producto coincide, retorna `200 OK` con un arreglo vacío.

*Justificación de la estimación:* consulta con parámetros opcionales sobre una entidad ya existente, sin reglas de negocio complejas.

---

## HU-05 — Contacto Directo / Intención de Compra

**Historia:**
Como Comprador, quiero enviar una solicitud de contacto directo al agricultor, para acordar condiciones de compra y logística sin intermediarios.

**Priorización:** M

**Estimación:** 5 Story Points

**Escenario BDD:**

* **Given:** un comprador autenticado con token JWT y una oferta activa registrada con `id_producto`.
* **When:** envía `POST /api/v1/contacto/mensaje` con el `id_producto` y el mensaje de negociación.
* **Then:** el sistema persiste la interacción en la tabla `mensajes` de PostgreSQL, vincula el mensaje al agricultor dueño de la oferta y retorna `200 OK` con la confirmación de notificación enviada.

*Justificación de la estimación:* involucra dos actores, validación de propiedad de la oferta y el disparo de la notificación.

---

## HU-06 — Autenticación con JWT

**Historia:**
Como Usuario registrado, quiero iniciar sesión con mis credenciales, para obtener un token que me permita consumir los endpoints protegidos de la plataforma.

**Priorización:** M

**Estimación:** 5 Story Points

**Escenario BDD:**

* **Given:** un usuario previamente registrado en PostgreSQL con cédula y contraseña válidas.
* **When:** envía `POST /api/v1/auth/login` con `cedula` y `password`.
* **Then:** el sistema valida las credenciales contra el hash almacenado y responde `200 OK` con un token JWT firmado con expiración de 24 horas; si las credenciales son incorrectas responde `401 Unauthorized` sin revelar cuál de los dos campos falló.

*Justificación de la estimación:* configuración transversal de Spring Security, filtro de autenticación y pruebas de rutas protegidas.

---

## HU-07 — Registro de Comprador Comercial

**Historia:**
Como Comerciante o Restaurante de Cali, quiero registrarme como comprador, para acceder al catálogo de ofertas y contactar agricultores.

**Priorización:** M

**Estimación:** 3 Story Points

**Escenario BDD:**

* **Given:** que un comerciante ingresa al endpoint `POST /api/v1/auth/register/comprador`.
* **When:** envía un JSON con `razon_social`, `nit`, `direccion_comercial`, `ciudad` y `password`.
* **Then:** el sistema valida que el `nit` no esté registrado, crea el usuario con rol `COMPRADOR` mediante el patrón Factory y responde `201 Created` con el `id_comprador` persistido en PostgreSQL.

*Justificación de la estimación:* reutiliza la infraestructura de registro de HU-01, variando el rol y las validaciones.

---

## HU-08 — Registro de Finca por Municipio

**Historia:**
Como Agricultor, quiero registrar mis fincas indicando su municipio, para asociar cada lote de cosecha a su origen real y dar trazabilidad al comprador.

**Priorización:** M

**Estimación:** 3 Story Points

**Escenario BDD:**

* **Given:** un agricultor autenticado con token JWT válido.
* **When:** envía `POST /api/v1/fincas` con `nombre_finca`, `municipio` (dentro del catálogo de municipios del Valle) y `hectareas`.
* **Then:** el sistema rechaza con `400 Bad Request` cualquier municipio fuera del catálogo y, en caso válido, responde `201 Created` asociando la finca al `id_agricultor` del token en PostgreSQL.

*Justificación de la estimación:* entidad simple con relación `@ManyToOne` y validación contra un catálogo cerrado.

---

## HU-09 — Detalle de una Oferta Publicada

**Historia:**
Como Comprador, quiero consultar el detalle completo de una oferta, para conocer cantidad, precio, finca de origen y fecha de cosecha antes de negociar.

**Priorización:** S

**Estimación:** 2 Story Points

**Escenario BDD:**

* **Given:** que existe una oferta con `id_producto = 25` registrada en PostgreSQL.
* **When:** el usuario realiza `GET /api/v1/productos/25`.
* **Then:** el sistema responde `200 OK` con el detalle del producto incluyendo finca, municipio de origen, cantidad disponible y precio unitario; si el ID no existe responde `404 Not Found` con un mensaje descriptivo.

*Justificación de la estimación:* lectura directa por clave primaria sobre una entidad ya modelada.

---

## HU-10 — Reserva de Inventario y Control de Stock

**Historia:**
Como Comprador, quiero que la cantidad que reservo se descuente del stock disponible, para evitar que un mismo lote se venda dos veces.

**Priorización:** M

**Estimación:** 8 Story Points

**Escenario BDD:**

* **Given:** un comprador autenticado con JWT y una oferta con `cantidad_disponible = 100` kg en PostgreSQL.
* **When:** envía `POST /api/v1/reservas` con `id_producto` y `cantidad_kg = 30`.
* **Then:** el sistema descuenta el stock dentro de una transacción atómica, deja la oferta en 70 kg y responde `201 Created`; si la cantidad solicitada supera el stock disponible responde `409 Conflict` sin modificar el inventario.

*Justificación de la estimación:* concurrencia, transaccionalidad y bloqueo optimista elevan el riesgo técnico y la incertidumbre.

---

## HU-11 — Consolidación del Carrito de Compra

**Historia:**
Como Comprador, quiero consolidar varias ofertas en un solo carrito, para gestionar una compra conjunta a distintos agricultores.

**Priorización:** S

**Estimación:** 5 Story Points

**Escenario BDD:**

* **Given:** un comprador autenticado con JWT y dos ofertas activas de agricultores distintos.
* **When:** envía `POST /api/v1/carrito/items` por cada oferta y luego consulta `GET /api/v1/carrito`.
* **Then:** el sistema responde `200 OK` con el listado de ítems, el subtotal por agricultor y el total general en COP, persistiendo el carrito en PostgreSQL asociado al `id_comprador`.

*Justificación de la estimación:* agregación de ítems, cálculo de totales y manejo del estado del carrito entre peticiones.

---

## HU-12 — Emisión de Orden de Compra Directa

**Historia:**
Como Comprador, quiero emitir una orden de compra a precio justo desde mi carrito, para formalizar la transacción directa con el agricultor.

**Priorización:** M

**Estimación:** 8 Story Points

**Escenario BDD:**

* **Given:** un comprador autenticado con JWT y un carrito con al menos un ítem con stock reservado.
* **When:** envía `POST /api/v1/ordenes` referenciando el `id_carrito`.
* **Then:** el sistema genera una orden por agricultor mediante el patrón Factory, la persiste en PostgreSQL con estado `CREADA`, vacía el carrito y responde `201 Created` con el `numero_orden` y el total en COP.

*Justificación de la estimación:* orquesta varias entidades, divide la orden por agricultor y debe ser transaccional de extremo a extremo.

---

## HU-13 — Confirmación de Alistamiento del Lote

**Historia:**
Como Agricultor, quiero confirmar que el lote de una orden ya está alistado, para que el comprador conozca el avance real de su pedido.

**Priorización:** S

**Estimación:** 3 Story Points

**Escenario BDD:**

* **Given:** un agricultor autenticado con JWT y una orden en estado `CREADA` que le pertenece.
* **When:** envía `PATCH /api/v1/ordenes/{numero_orden}/alistamiento`.
* **Then:** el sistema cambia el estado de la orden a `ALISTADA`, registra la marca de tiempo en PostgreSQL y responde `200 OK`; si la orden pertenece a otro agricultor responde `403 Forbidden`.

*Justificación de la estimación:* cambio de estado con validación de autorización sobre un recurso existente.

---

## HU-14 — Programación de Ruta de Despacho

**Historia:**
Como Agricultor, quiero programar la ruta y fecha de despacho de una orden alistada, para coordinar el transporte y reducir pérdidas poscosecha.

**Priorización:** C

**Estimación:** 8 Story Points

**Escenario BDD:**

* **Given:** un agricultor autenticado con JWT y una orden en estado `ALISTADA`.
* **When:** envía `POST /api/v1/despachos` con `numero_orden`, `fecha_despacho`, `municipio_origen` y `direccion_destino`.
* **Then:** el sistema valida que `fecha_despacho` sea igual o posterior a hoy, crea el despacho en estado `PROGRAMADO` en PostgreSQL, cambia la orden a `EN_DESPACHO` y responde `201 Created` con el `id_despacho`.

*Justificación de la estimación:* nueva entidad logística, validaciones temporales y coordinación con la máquina de estados de la orden.

---

## HU-15 — Seguimiento en Tiempo Real del Despacho

**Historia:**
Como Comprador, quiero seguir en tiempo real la ubicación de mi despacho, para planear la recepción de la mercancía en mi local.

**Priorización:** W

**Estimación:** 13 Story Points

**Escenario BDD:**

* **Given:** un comprador autenticado con JWT y un despacho en estado `EN_RUTA` asociado a su orden.
* **When:** se suscribe al canal `WS /ws/v1/despachos/{id_despacho}` y el transportador emite una actualización de coordenadas.
* **Then:** el sistema notifica a los suscriptores mediante el patrón Observer en menos de 5 segundos, persiste cada punto de la traza en PostgreSQL y responde `401 Unauthorized` ante cualquier suscripción sin token válido.

*Justificación de la estimación:* introduce WebSocket, integración con dispositivo emisor de coordenadas e infraestructura de tiempo real. Se clasifica como **Won't Have** en este release: aporta valor pero excede el alcance del semestre y depende de una fuente de datos GPS que aún no existe.

---

## Registro de la sesión de Planning Poker

| Historia | Votos de la primera ronda | Discusión | Consenso final |
|----------|---------------------------|-----------|----------------|
| HU-02 | 3 · 5 · 8 | Backend la vio como CRUD simple; QA recordó que el DoD exige prueba en JUnit 5 con JWT | **5** |
| HU-10 | 5 · 8 · 13 | La concurrencia sobre el stock fue el factor decisivo | **8** |
| HU-09 | 1 · 2 · 2 | Lectura directa por ID, sin lógica adicional | **2** |

> El resto de las historias se estimó siguiendo la misma dinámica: lectura del PO, votación oculta, revelación simultánea, discusión de los extremos y segunda votación hasta alcanzar consenso.
