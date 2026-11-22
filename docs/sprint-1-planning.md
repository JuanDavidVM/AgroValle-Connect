# Sprint 1 — AgroValle Connect

## 1. Propuesta de las 3 Historias para el Sprint

Para el Sprint 1 se proponen cinco historias de usuario que permiten construir una primera versión funcional de AgroValle Connect, comenzando con el registro y autenticación y continuando con la gestión y consulta de productos agrícolas.

- **HU-01: Registro de Agricultores:** Como Agricultor, quiero registrarme en la plataforma para ofrecer mis productos.
- **HU-06: Autenticación con JWT:** Como Usuario registrado, quiero iniciar sesión con mis credenciales, para obtener un token que me permita consumir los endpoints protegidos de la plataforma.
- **HU-07: Registro de Comprador Comercial:** Como comprador quiero poder registrarme en la plataforma para comprar productos.

Estas historias corresponden a funcionalidades que ya están definidas en el documento original de AgroValle Connect.

## 2. Product Backlog de las Historias Seleccionadas

| ID | Nombre de la Historia | Módulo del Sistema | MoSCoW | Story Points |
| --- | --- | --- | --- | --- |
| HU-01 | Registro de Agricultores | Autenticación / Registro | Must Have | 3 |
| HU-06 | Autenticación e Inicio de Sesión (JWT) | Seguridad | Must Have | 5 |
| HU-07 | Registro de Comprador Comercial | Pedidos Directos | Must Have | 3 |

## 3. Selección para el SPRINT 1

**Capacidad:** 11 Story Points

### Sprint Goal del Sprint 1

> "Habilitar el registro inicial de agricultores y la autenticación del inicio de sesión agrícolas y registro de compradores comerciales, validando la persistencia de la información en PostgreSQL y la arquitectura REST."

### Historias seleccionadas para el Sprint 1

#### 1. HU-01: Registro de Agricultores — 3 Story Points

**Por qué entra:** Es el punto inicial del sistema. Permite implementar el registro del agricultor, la entidad correspondiente, el repositorio, el servicio y el controlador REST, además de comprobar que la información quede almacenada en PostgreSQL. El escenario definido espera una respuesta **201 Created**.

#### 2. HU-06: Autenticación con JWT — 5 Story Points

**Por qué entra:** Permite implementar un método de autenticación para el Usuario registrado, que desea iniciar sesión con sus credenciales, para obtener un token que me permita consumir los endpoints protegidos de la plataforma.

#### 3. HU-07: Registro de Comprador Comercial — 3 Story Points

**Por qué entra:** Permite implementar el sistema para que los compradores puedan registrarse y autenticarse para hacer uso del servicio sobre la información almacenada de los productos disponibles. La historia está estimada en 3 Story Points.

## 4. Desglose de Tareas Técnicas

### HU-01: Registro de Agricultores — 3 Story Points

| ID Tarea | Descripción Técnica | Componente / Tecnología | Atributo ISO 25010 |
| --- | --- | --- | --- |
| T01.1 | Crear la entidad Agricultor con sus atributos principales y anotaciones JPA. | Java 17 / JPA | Integridad de Datos |
| T01.2 | Agregar validaciones para los campos obligatorios mediante @NotNull, @NotBlank, @Email, etc. | Jakarta Validation | Adecuación Funcional |
| T01.4 | Implementar AgricultorService con la lógica para registrar y consultar agricultores. | Spring Boot / Service | Correctitud Funcional |
| T01.5 | Implementar AgricultorController con endpoint POST /api/v1/agricultores. | Spring Boot / REST Controller | Adecuación Funcional |
| T01.6 | Configurar persistencia en PostgreSQL y verificar que el registro sea almacenado correctamente. | PostgreSQL / Spring Data JPA | Fiabilidad |
| T01.7 | Implementar manejo de errores para datos inválidos o duplicados. | Spring Boot / Exception Handler | Fiabilidad |
| T01.8 | Crear pruebas unitarias para el servicio y pruebas del endpoint de registro, verificando respuesta 201 Created. | JUnit 5 / Mockito | Fiabilidad |

### HU-06: Autenticación con JWT — 5 Story Points

| ID Tarea | Descripción Técnica | Componente / Tecnología | Atributo ISO 25010 |
| --- | --- | --- | --- |
| T06.1 | Crear/configurar la entidad Usuario con credenciales y rol de acceso. | Java 17 / JPA | Integridad de Datos |
| T06.2 | Crear UsuarioRepository para consultar usuarios por correo o nombre de usuario. | Spring Data JPA | Mantenibilidad |
| T06.3 | Configurar dependencias y parámetros necesarios para autenticación JWT. | Spring Security / JWT | Seguridad |
| T06.4 | Implementar el servicio encargado de generar y validar tokens JWT. | Java / JWT | Seguridad |
| T06.5 | Crear DTO para recibir las credenciales de inicio de sesión. | Java / DTO | Adecuación Funcional |
| T06.6 | Implementar endpoint POST /api/v1/auth/login. | Spring Boot / REST | Adecuación Funcional |
| T06.7 | Configurar filtro JWT para validar el token en las solicitudes protegidas. | Spring Security | Seguridad |
| T06.8 | Configurar autorización de endpoints según autenticación y roles. | Spring Security | Seguridad |
| T06.9 | Implementar respuestas para credenciales incorrectas, token inválido o token expirado. | Spring Boot / Exception Handler | Fiabilidad |

### HU-07: Registro de Comprador Comercial — 3 Story Points

| ID Tarea | Descripción Técnica | Componente / Tecnología | Atributo ISO 25010 |
| --- | --- | --- | --- |
| T07.1 | Crear la entidad CompradorComercial con los datos requeridos para el registro. | Java 17 / JPA / Hibernate | Integridad de Datos |
| T07.2 | Implementar validaciones de los campos obligatorios y datos de contacto. | Jakarta Validation | Adecuación Funcional |
| T07.3 | Crear CompradorComercialRepository extendiendo JpaRepository. | Spring Data JPA | Mantenibilidad |
| T07.4 | Implementar CompradorComercialService para registrar y consultar compradores. | Spring Boot / Service | Correctitud Funcional |
| T07.5 | Implementar CompradorComercialController con endpoint POST /api/v1/compradores. | Spring Boot / REST Controller | Adecuación Funcional |
| T07.6 | Validar que no existan compradores duplicados utilizando correo o identificador empresarial. | Spring Data JPA | Fiabilidad |
| T07.7 | Configurar persistencia de la información en PostgreSQL. | PostgreSQL / JPA | Integridad de Datos |
