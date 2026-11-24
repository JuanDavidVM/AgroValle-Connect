# Documento consolidado de arquitectura — Diagramas UML

AgroValle Connect · Ingeniería de Software II · UNIAJC · período 2-2026

Todos los diagramas están escritos en bloques ` ```mermaid `, de modo que GitHub los
dibuja en la vista previa del archivo sin herramientas externas y quedan versionados
como texto en el repositorio. Reemplazan a los diagramas exportados como imagen desde
Visual Paradigm, que se conservan en `docs/diseno/` como respaldo histórico.

Cada diagrama refleja el código realmente implementado, incluido el flujo de compra completo
—reserva de inventario, carrito, emisión de órdenes y alistamiento—: los nombres de
clases, métodos, endpoints y tablas son los del repositorio, no nombres de diseño
preliminar. Donde el flujo real difiere del esquema de la Sesión 13 se indica en una nota.

**Inventario de diagramas (mínimo exigido: 3 de secuencia, 2 de colaboración, 1 de
componentes, 1 de despliegue):**

| # | Tipo | Alcance | Historia |
| --- | --- | --- | --- |
| 1 | Secuencia | Publicación de una cosecha | HU-02 |
| 2 | Secuencia | Registro de un agricultor | HU-01 |
| 3 | Secuencia | Autenticación e inicio de sesión con JWT | HU-06 |
| 4 | Secuencia | Reserva de inventario con bloqueo optimista | HU-10 |
| 5 | Secuencia | Emisión de órdenes de compra con Factory | HU-12 |
| 6 | Colaboración | Evento desacoplado con Observer al publicar cosecha | HU-02 |
| 7 | Colaboración | Registro de finca con validación de municipio | HU-08 |
| 8 | Estados | Ciclo de vida de la orden y de la reserva | HU-10 a HU-13 |
| 9 | Componentes | Arquitectura multicapa de Spring Boot | — |
| 10 | Despliegue | Topología de staging en la nube | — |

## Trazabilidad de BDD a UML

Los escenarios Given-When-Then del `BACKLOG.md` se traducen en los mensajes de los
diagramas. La correspondencia es directa:

| Historia | Escenario BDD | Endpoint | Diagrama |
| --- | --- | --- | --- |
| HU-01 Registro de agricultores | Dado un visitante con documento no registrado, cuando envía sus datos, entonces queda registrado | `POST /api/v1/agricultores` | 2 |
| HU-02 Publicación de productos | Dado un agricultor autenticado con una finca propia, cuando publica una cosecha, entonces la oferta queda activa | `POST /api/v1/productos` | 1 y 4 |
| HU-06 Autenticación JWT | Dado un usuario registrado, cuando envía credenciales correctas, entonces recibe un token Bearer | `POST /api/v1/auth/login` | 3 |
| HU-08 Registro de finca | Dado un agricultor autenticado, cuando registra una finca en un municipio del Valle, entonces la finca queda asociada a él | `POST /api/v1/fincas` | 7 |
| HU-09 Detalle de oferta | Dado un visitante, cuando consulta una oferta por su identificador, entonces recibe finca, municipio, cantidad y precio | `GET /api/v1/productos/{id}` | 9 |
| HU-10 Reserva de inventario | Dado un comprador autenticado y una oferta activa, cuando reserva una cantidad disponible, entonces el stock se descuenta y la reserva queda activa | `POST /api/v1/reservas` | 4 y 8 |
| HU-11 Carrito | Dado un comprador con ofertas de varios agricultores, cuando consulta su carrito, entonces recibe el subtotal por agricultor y el total | `GET /api/v1/carrito` | 9 |
| HU-12 Emisión de orden | Dado un carrito con stock reservado, cuando emite la orden, entonces se genera una por agricultor en estado CREADA y el carrito queda vacío | `POST /api/v1/ordenes` | 5 y 8 |
| HU-13 Alistamiento | Dado un agricultor con una orden propia en estado CREADA, cuando confirma el alistamiento, entonces la orden pasa a ALISTADA con su marca de tiempo | `PATCH /api/v1/ordenes/{numero}/alistamiento` | 8 |

La numeración de las historias es la del `BACKLOG.md` del equipo. La autenticación es
HU-06 en el backlog del proyecto.

## 1. Diagrama de secuencia: HU-02, publicación de una cosecha

```mermaid
sequenceDiagram
    autonumber
    actor Agricultor
    participant C as ProductoController
    participant S as ProductoServiceImpl
    participant A as AgricultorService
    participant F as FincaRepository
    participant K as CatalogoService
    participant P as ProductoRepository
    participant E as ApplicationEventPublisher
    participant L1 as AuditoriaCosechaListener
    participant L2 as NotificacionProductorListener
    participant DB as PostgreSQL

    Agricultor->>C: POST /api/v1/productos (JWT, JSON)
    C->>C: validar el cuerpo (@Valid)
    alt datos inválidos
        C-->>Agricultor: 400 Bad Request con errores por campo
    else datos válidos
        C->>S: publicar(correo, request)
        S->>A: obtenerPorCorreo(correo)
        A->>DB: SELECT agricultor por correo del usuario
        S->>F: findByIdAndAgricultorId(fincaId, agricultorId)
        F->>DB: SELECT finca
        alt la finca no existe o no es del agricultor
            S-->>C: RecursoNoEncontradoException
            C-->>Agricultor: 404 Not Found
        else la finca es del agricultor
            S->>K: obtenerCategoria(nombre)
            S->>P: save(producto ACTIVA)
            P->>DB: INSERT INTO productos
            S->>E: publishEvent(CosechaPublicadaEvent)
            E->>L1: registrar(evento)
            L1->>DB: INSERT INTO registros_auditoria
            Note over S,L2: al retornar publicar() se confirma la transacción
            E->>L2: notificar(evento) en fase AFTER_COMMIT
            L2->>DB: SELECT agricultor
            L2->>L2: Notificador.notificar(agricultor, mensaje)
            S-->>C: ProductoResponse
            C-->>Agricultor: 201 Created
        end
    end
```

El método `publicar` está anotado con `@Transactional`, de modo que la confirmación
ocurre al salir del servicio. Por eso el oyente de notificación, suscrito con
`@TransactionalEventListener(phase = AFTER_COMMIT)`, se ejecuta antes de que el
controlador devuelva el 201, mientras el de auditoría, con `@EventListener`, corre dentro
de la misma transacción.

## 2. Diagrama de secuencia: HU-01, registro de un agricultor

```mermaid
sequenceDiagram
    autonumber
    actor Visitante
    participant C as AgricultorController
    participant S as AgricultorServiceImpl
    participant K as CatalogoService
    participant U as UsuarioService
    participant R as AgricultorRepository
    participant DB as PostgreSQL

    Visitante->>C: POST /api/v1/agricultores (JSON)
    C->>C: validar el cuerpo (@Valid)
    alt datos inválidos
        C-->>Visitante: 400 Bad Request con errores por campo
    else datos válidos
        C->>S: registrar(request)
        S->>R: existsByDocumento(documento)
        R->>DB: SELECT
        alt documento ya registrado
            S-->>C: ConflictoException
            C-->>Visitante: 409 Conflict
        else documento nuevo
            S->>K: obtenerMunicipio(nombre)
            S->>U: crear(correo, contrasena, AGRICULTOR)
            U->>DB: INSERT INTO usuario (contraseña cifrada)
            S->>R: save(agricultor)
            R->>DB: INSERT INTO agricultores
            S-->>C: AgricultorResponse
            C-->>Visitante: 201 Created (sin contraseña)
        end
    end
```

## 3. Diagrama de secuencia: HU-06, autenticación e inicio de sesión con JWT

```mermaid
sequenceDiagram
    autonumber
    actor U as Agricultor o Comprador
    participant C as AuthController
    participant S as AuthServiceImpl
    participant AM as AuthenticationManager
    participant UDS as CustomUserDetailsService
    participant R as UsuarioRepository
    participant DB as PostgreSQL
    participant J as JwtService

    U->>C: POST /api/v1/auth/login (correo, contrasena)
    C->>C: validar el cuerpo (@Valid)
    C->>S: login(request)
    S->>S: normalizar el correo con trim y minúsculas
    S->>AM: authenticate(UsernamePasswordAuthenticationToken)
    AM->>UDS: loadUserByUsername(correo)
    UDS->>R: findByCorreo(correo)
    R->>DB: SELECT sobre la tabla usuario
    alt el correo no existe o la contraseña no coincide
        DB-->>R: sin filas o hash distinto
        AM-->>S: BadCredentialsException
        S-->>C: BadCredentialsException
        C-->>U: 401 Unauthorized
    else credenciales correctas
        DB-->>R: usuario con password_hash y rol
        R-->>UDS: usuario encontrado
        UDS-->>AM: UserDetails con rol y hash
        AM->>AM: comparar el hash BCrypt con el PasswordEncoder
        AM-->>S: Authentication autenticado
        S->>S: rolDe(auth) quita el prefijo ROLE_
        S->>J: generarToken(correo, rol)
        J-->>S: JWT firmado con HS256
        S->>J: getExpirationMs()
        S-->>C: LoginResponse(token, Bearer, expiraEn, rol)
        C-->>U: 200 OK con el token
    end
```

La verificación de la contraseña no la hace el servicio: la delega en el
`AuthenticationManager` de Spring Security, que a su vez consulta
`CustomUserDetailsService` y compara el hash con el `PasswordEncoder` configurado. El
servicio solo normaliza el correo, extrae el rol y arma el token. Esta delegación es la
razón de que `AuthServiceImpl` no dependa de `UsuarioRepository`.

Las peticiones posteriores a `GET /api/v1/auth/me` no repiten este flujo: las resuelve
`JwtAuthenticationFilter`, que valida el token en cada petición antes de llegar al
controlador.

## 4. Diagrama de secuencia: HU-10, reserva de inventario

```mermaid
sequenceDiagram
    autonumber
    actor Comprador
    participant C as ReservaController
    participant S as ReservaServiceImpl
    participant K as CompradorComercialService
    participant P as ProductoRepository
    participant R as ReservaRepository
    participant DB as PostgreSQL

    Comprador->>C: POST /api/v1/reservas (JWT, productoId, cantidadKg)
    C->>C: validar el cuerpo (@Valid)
    C->>S: reservar(correo, request)
    S->>K: obtenerPorCorreo(correo)
    S->>P: findById(productoId)
    P->>DB: SELECT producto
    alt la oferta no existe
        S-->>C: RecursoNoEncontradoException
        C-->>Comprador: 404 Not Found
    else la oferta no está activa
        S-->>C: ConflictoException
        C-->>Comprador: 409 Conflict
    else la cantidad supera el stock
        S-->>C: ConflictoException con el stock restante
        C-->>Comprador: 409 Conflict, inventario intacto
    else hay stock suficiente
        S->>S: cantidadDisponible = cantidadDisponible - cantidadKg
        S->>P: saveAndFlush(producto)
        P->>DB: UPDATE productos SET cantidad, version = version + 1 WHERE version = ?
        alt otra operación cambió la versión primero
            DB-->>P: 0 filas afectadas
            P-->>S: ObjectOptimisticLockingFailureException
            C-->>Comprador: 409 Conflict, vuelve a intentarlo
        else la versión coincide
            S->>R: save(reserva ACTIVA)
            R->>DB: INSERT INTO reservas
            S-->>C: ReservaResponse con el stock restante
            C-->>Comprador: 201 Created
        end
    end
```

La columna `version` de `productos` es lo que impide vender dos veces el mismo lote. El
`UPDATE` solo afecta filas cuya versión siga siendo la que se leyó: si dos compradores
reservan a la vez, el segundo encuentra cero filas afectadas y recibe 409 en lugar de
llevarse un inventario que ya no existe. El `saveAndFlush` fuerza esa comprobación dentro
de la transacción y no al cerrarla.

## 5. Diagrama de secuencia: HU-12, emisión de órdenes de compra

```mermaid
sequenceDiagram
    autonumber
    actor Comprador
    participant C as OrdenController
    participant S as OrdenServiceImpl
    participant I as ItemCarritoRepository
    participant F as OrdenFactory
    participant O as OrdenRepository
    participant R as ReservaRepository
    participant P as ProductoRepository
    participant DB as PostgreSQL

    Comprador->>C: POST /api/v1/ordenes (JWT)
    C->>S: emitir(correo)
    S->>I: findByCompradorIdOrderByIdAsc(compradorId)
    I->>DB: SELECT renglones del carrito
    alt el carrito está vacío
        S-->>C: ConflictoException
        C-->>Comprador: 409 Conflict
    else el carrito tiene renglones
        S->>S: agrupar los renglones por agricultor
        loop por cada agricultor del carrito
            S->>F: crear(comprador, agricultor, renglones)
            F->>F: estado CREADA, numero de orden, copia del precio vigente
            F-->>S: Orden con sus DetalleOrden
            S->>O: save(orden)
            O->>DB: INSERT INTO ordenes y detalles_orden
            loop por cada renglón
                S->>R: buscar reservas ACTIVAS del producto
                alt lo reservado no alcanza
                    S-->>C: ConflictoException
                    C-->>Comprador: 409, ninguna orden emitida
                else lo reservado alcanza
                    S->>R: marcar CONSUMIDA y ligar a la orden
                    opt sobra cantidad reservada
                        S->>P: devolver el sobrante al stock
                    end
                end
            end
        end
        S->>I: deleteAll(renglones del carrito)
        S-->>C: una OrdenResponse por agricultor
        C-->>Comprador: 201 Created con numero de orden y total
    end
```

Tres propiedades que el diagrama hace visibles. El precio se copia a `detalles_orden` en
el momento de emitir, de modo que una orden ya emitida no cambia de valor si el agricultor
reajusta su oferta después. La orden no descuenta inventario: lo descontó la reserva, y
aquí solo se consume, lo que evita el doble descuento. Y todo ocurre en una sola
transacción, así que el 409 por reserva insuficiente deja el carrito intacto y sin ninguna
orden a medio emitir.

## 6. Diagrama de colaboración: HU-02, evento desacoplado con Observer

Mermaid no ofrece un tipo nativo de diagrama de comunicación UML, de modo que se modela
como grafo de objetos con los mensajes numerados en el orden en que ocurren, que es la
información que aporta un diagrama de colaboración frente a uno de secuencia: qué objeto
conoce a qué otro.

```mermaid
flowchart TB
    A["Agricultor"]
    C["ProductoController"]
    S["ProductoServiceImpl"]
    AS["AgricultorService"]
    FR["FincaRepository"]
    CS["CatalogoService"]
    PR["ProductoRepository"]
    EP["ApplicationEventPublisher"]
    L1["AuditoriaCosechaListener<br/>@EventListener"]
    L2["NotificacionProductorListener<br/>@TransactionalEventListener"]
    N["Notificador"]
    DB[("PostgreSQL")]

    A -->|"1. POST /api/v1/productos con JWT"| C
    C -->|"2. publicar(correo, request)"| S
    S -->|"3. obtenerPorCorreo(correo)"| AS
    S -->|"4. findByIdAndAgricultorId(fincaId, agricultorId)"| FR
    S -->|"5. obtenerCategoria(categoria)"| CS
    S -->|"6. save(producto en estado ACTIVA)"| PR
    PR -->|"7. INSERT INTO productos"| DB
    S -->|"8. publishEvent(CosechaPublicadaEvent)"| EP
    EP -->|"9. registrar(evento)"| L1
    L1 -->|"10. INSERT INTO registros_auditoria"| DB
    EP -->|"11. notificar(evento) tras el commit"| L2
    L2 -->|"12. findById(agricultorId)"| DB
    L2 -->|"13. notificar(agricultor, mensaje)"| N
    C -->|"14. 201 Created con ProductoResponse"| A
```

El valor del patrón está en lo que el diagrama no muestra: no existe ninguna flecha de
`ProductoServiceImpl` hacia los dos oyentes. El servicio publica un
`CosechaPublicadaEvent` y desconoce quién lo consume, de modo que agregar un tercer
suscriptor no obliga a modificar el servicio. El evento es un `record` que transporta
datos simples, no entidades JPA, para que los oyentes no dependan de la sesión de
persistencia.

## 7. Diagrama de colaboración: HU-08, registro de finca por municipio

```mermaid
flowchart TB
    A["Agricultor"]
    C["FincaController"]
    V["MunicipioDelValleValidator<br/>ConstraintValidator"]
    S["FincaServiceImpl"]
    AS["AgricultorService"]
    CS["CatalogoService"]
    MR["MunicipioRepository"]
    FR["FincaRepository"]
    DB[("PostgreSQL")]

    A -->|"1. POST /api/v1/fincas con JWT"| C
    C -->|"2. isValid(municipio) al procesar @Valid"| V
    V -->|"3. buscarMunicipio(nombre)"| CS
    CS -->|"4. findAll() y filtrado en memoria"| MR
    MR -->|"5. SELECT sobre municipios"| DB
    C -->|"6. 400 Bad Request si el municipio no es del Valle"| A
    C -->|"7. registrar(correo, request)"| S
    S -->|"8. obtenerPorCorreo(correo)"| AS
    S -->|"9. existsByAgricultorIdAndNombreIgnoreCase"| FR
    C -->|"10. 409 Conflict si ya existe una finca con ese nombre"| A
    S -->|"11. obtenerMunicipio(municipio)"| CS
    S -->|"12. save(finca)"| FR
    FR -->|"13. INSERT INTO fincas"| DB
    C -->|"14. 201 Created con FincaResponse"| A
```

La validación del municipio ocurre antes del servicio, no dentro: `MunicipioDelValle` es
una anotación de Bean Validation cuyo validador se dispara cuando el controlador procesa
`@Valid`, y consulta el catálogo a través de `CatalogoService`. El servicio asume que el
municipio ya es válido y se ocupa de la regla de negocio propia: que el agricultor no
tenga dos fincas con el mismo nombre.

Los mensajes 6 y 10 son rutas alternativas de error, no pasos del camino feliz: las
excepciones suben del servicio al controlador y las traduce `GlobalExceptionHandler`, que
es quien construye la respuesta HTTP. `CatalogoService.buscarMunicipio` resuelve el
nombre con `findAll()` y un filtro en memoria sobre el catálogo de municipios, que es una
tabla pequeña y estable.

## 8. Diagrama de estados: ciclo de vida de la orden y de la reserva

```mermaid
stateDiagram-v2
    direction LR
    state "Reserva" as R {
        [*] --> ACTIVA : POST /api/v1/reservas
        ACTIVA --> CONSUMIDA : la orden la incorpora
        ACTIVA --> CANCELADA : anulacion (pendiente)
        CONSUMIDA --> [*]
        CANCELADA --> [*]
    }
    state "Orden" as O {
        [*] --> CREADA : POST /api/v1/ordenes
        CREADA --> ALISTADA : PATCH alistamiento del agricultor
        ALISTADA --> [*]
    }
```

Las dos transiciones hacia `CONSUMIDA` y hacia `ALISTADA` son las únicas que el código
ejecuta hoy. `CANCELADA` existe en el modelo porque anular una reserva devuelve stock y
esa operación todavía no tiene historia asignada: se deja declarada para que el día que
llegue no haya que migrar datos. El paso de `CREADA` a `ALISTADA` solo lo puede disparar
el agricultor dueño de la orden, y es irreversible: un segundo intento responde 409 para
no pisar la marca de tiempo original.

## 9. Diagrama de componentes: arquitectura multicapa de Spring Boot

```mermaid
flowchart LR
    cliente["Cliente (Navegador / Swagger UI)"]

    subgraph seg["Capa de seguridad — security + config"]
        jwtf["JwtAuthenticationFilter"]
        jwts["JwtService"]
        cuds["CustomUserDetailsService"]
    end
    subgraph ctrl["Capa de presentación — controller"]
        authc["AuthController"]
        agrc["AgricultorController"]
        comc["CompradorComercialController"]
        finc["FincaController"]
        proc["ProductoController"]
        resc["ReservaController"]
        carc["CarritoController"]
        ordc["OrdenController"]
    end
    subgraph svc["Capa de negocio — service + service.impl"]
        auths["AuthService"]
        agrs["AgricultorService"]
        coms["CompradorComercialService"]
        fins["FincaService"]
        pros["ProductoService"]
        usus["UsuarioService"]
        cats["CatalogoService"]
        ress["ReservaService"]
        cars["CarritoService"]
        ords["OrdenService"]
    end
    subgraph fac["Construcción de agregados — factory"]
        of["OrdenFactory"]
    end
    subgraph evt["Eventos de dominio — event + listener + notificacion"]
        ev["CosechaPublicadaEvent"]
        aud["AuditoriaCosechaListener"]
        noti["NotificacionProductorListener"]
        notif["Notificador"]
    end
    subgraph rep["Capa de persistencia — repository"]
        r["Repositorios JPA<br/>Usuario, Agricultor, Comprador, Finca,<br/>Producto, Municipio, Categoria, RegistroAuditoria,<br/>Reserva, ItemCarrito, Orden"]
    end
    bd[("PostgreSQL en Supabase")]

    cliente --> jwtf
    jwtf --> jwts
    jwtf --> cuds
    jwtf --> authc & agrc & comc & finc & proc & resc & carc & ordc
    authc --> auths
    agrc --> agrs
    comc --> coms
    finc --> fins
    proc --> pros
    resc --> ress
    carc --> cars
    ordc --> ords
    auths --> jwts
    auths --> cuds
    agrs --> usus
    coms --> usus
    agrs --> cats
    coms --> cats
    fins --> agrs
    fins --> cats
    pros --> agrs
    pros --> cats
    ress --> coms
    cars --> coms
    ords --> coms
    ords --> agrs
    ords --> of
    pros -.-> ev
    ev -.-> aud
    ev -.-> noti
    noti --> notif
    svc --> r
    aud --> r
    noti --> r
    cuds --> r
    r --> bd
```

Las cuatro capas quedan separadas por paquete: los controladores solo hablan con
interfaces de servicio, los servicios solo con repositorios, y ninguna capa salta sobre
la siguiente. Las flechas punteadas son comunicación por evento, no invocación directa.
El esquema relacional está en `docs/diseno/schema.sql` y se aplica desde el editor SQL de
Supabase; la migración versionada con Flyway está diferida al Sprint 4.

## 10. Diagrama de despliegue: topología de staging

```mermaid
flowchart TB
    dev["Equipo de desarrollo<br/>Pull Request a develop"]

    subgraph gh["GitHub"]
        repo["Repositorio AgroValle-Connect<br/>rulesets en main y develop"]
        ci["ci.yml<br/>Maven, JUnit 5, Checkstyle, JaCoCo"]
        cd["cd.yml<br/>disparado por workflow_run"]
        sec["GitHub Secrets<br/>RENDER_DEPLOY_HOOK_URL"]
    end

    subgraph render["Render PaaS (plan gratuito)"]
        hook["Deploy hook"]
        build["Build de imagen<br/>Dockerfile multietapa"]
        app["Contenedor Spring Boot 3.3.5<br/>JRE 17, perfil staging<br/>512 MB, MaxRAMPercentage=70, UseSerialGC"]
        envs["Variables de entorno<br/>SUPABASE_DB_*, JWT_SECRET, PORT"]
    end

    subgraph supa["Supabase"]
        pg[("PostgreSQL<br/>esquema en 3FN")]
    end

    docente["Docente y equipo<br/>GET /api/v1/productos"]

    dev --> repo
    repo --> ci
    ci -->|"solo si el pipeline termina en verde"| cd
    sec --> cd
    cd -->|"POST al deploy hook"| hook
    hook --> build
    build --> app
    envs --> app
    app -->|"JDBC con SSL"| pg
    docente -->|"HTTPS"| app
```

El Auto-Deploy nativo de Render está desactivado: la única ruta hacia staging es `cd.yml`,
que se dispara por `workflow_run` cuando la integración continua termina correctamente.
No existe forma de publicar un commit que no haya pasado por el pipeline. El servicio se
suspende a los 15 minutos sin tráfico, de modo que la primera petición después de una
pausa levanta el contenedor y tarda más de lo normal.

## Decisiones de arquitectura relacionadas

- [`docs/architecture-decisions/ADR-002-hosting-staging.md`](../architecture-decisions/ADR-002-hosting-staging.md)
  — plataforma de hosting de staging, empaquetado en contenedor, estrategia de despliegue
  y verificación posterior. Es la decisión que sustenta el diagrama 7.
- [`docs/adr/ADR-001-estrategia-de-ramificacion.md`](../adr/ADR-001-estrategia-de-ramificacion.md)
  — adopción de GitFlow, que explica por qué el disparador del pipeline distingue entre
  `develop` y `main`.

El ADR de hosting lleva el número 002 porque el 001 ya estaba asignado a la estrategia de
ramificación desde el Sprint 0. El propio ADR documenta el desfase frente al ejemplo de la
Sesión 12.
