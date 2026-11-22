# Diagramas UML — AgroValle Connect

Versiones en Mermaid (GitHub las dibuja directamente) de los diagramas que reflejan el código del Sprint 2.
Complementan los diagramas hechos en Visual Paradigm.

## 1. Diagrama de secuencia: HU-02, publicación de una cosecha

```mermaid
sequenceDiagram
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
            S-->>C: ProductoResponse
            C-->>Agricultor: 201 Created
            Note over L2,DB: tras confirmar la transacción (AFTER_COMMIT)
            E->>L2: notificar(evento)
            L2->>DB: SELECT agricultor
            L2->>L2: Notificador.notificar(agricultor, mensaje)
        end
    end
```

## 2. Diagrama de secuencia: HU-01, registro de un agricultor

```mermaid
sequenceDiagram
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

## 3. Diagrama de componentes (paquetes Java)

```mermaid
flowchart LR
    cliente["Cliente (Navegador / Swagger UI)"]

    subgraph seg["security + config"]
        jwtf["JwtAuthenticationFilter"]
        jwts["JwtService"]
        cuds["CustomUserDetailsService"]
    end
    subgraph ctrl["controller"]
        authc["AuthController"]
        agrc["AgricultorController"]
        comc["CompradorComercialController"]
        finc["FincaController"]
        proc["ProductoController"]
    end
    subgraph svc["service (interfaces) + service.impl"]
        auths["AuthService"]
        agrs["AgricultorService"]
        coms["CompradorComercialService"]
        fins["FincaService"]
        pros["ProductoService"]
        usus["UsuarioService"]
        cats["CatalogoService"]
    end
    subgraph evt["event + listener + notificacion"]
        ev["CosechaPublicadaEvent"]
        aud["AuditoriaCosechaListener"]
        noti["NotificacionProductorListener"]
        notif["Notificador"]
    end
    subgraph rep["repository"]
        r["Repositorios JPA<br/>Usuario, Agricultor, Comprador,<br/>Finca, Producto, Municipio,<br/>Categoria, RegistroAuditoria"]
    end
    bd[("PostgreSQL")]

    cliente --> jwtf
    jwtf --> jwts
    jwtf --> cuds
    jwtf --> authc & agrc & comc & finc & proc
    authc --> auths
    agrc --> agrs
    comc --> coms
    finc --> fins
    proc --> pros
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
