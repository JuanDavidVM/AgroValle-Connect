# Documentación Tecnológica de API - Sprint 1

Esta documentación detalla los endpoints, esquemas de datos y esquemas de seguridad basados en la interfaz de **Swagger / OpenAPI** de la aplicación.

---

## 🔒 Autenticación y Seguridad

La API está protegida utilizando tokens **JWT (JSON Web Tokens)** mediante autenticación Bearer.

* **Tipo de esquema:** HTTP
* **Esquema Bearer:** `bearerAuth`
* **Formato de Header:** `Authorization: Bearer <tu_token_jwt>`

---

## 📌 Endpoints

### 1. HU-01: Agricultores

#### `POST /api/v1/agricultores`
Registra un nuevo agricultor en la plataforma.

* **Respuestas HTTP:**
  * `201 Created`: Registrado con éxito.
  * `400 Bad Request`: Datos de entrada inválidos.
  * `409 Conflict`: Usuario o documento duplicado.

* **Cuerpo de la Petición (`Request Body`):** `application/json`
  ```json
  {
    "nombreCompleto": "string",
    "documento": "630322",
    "correo": "string",
    "telefono": "1304653169",
    "municipio": "string",
    "contrasena": "stringst"
  }
  ```

* **Respuesta de Ejemplo (`200 OK` / `201 Created`):** `application/json`
  ```json
  {
    "id": 0,
    "nombreCompleto": "string",
    "documento": "string",
    "correo": "string",
    "telefono": "string",
    "municipio": "string",
    "fechaRegistro": "2026-10-01T03:34:22.800Z"
  }
  ```

---

### 2. HU-06: Autenticación JWT

#### `POST /api/v1/auth/login`
Inicia sesión y genera el token Bearer JWT de acceso.

* **Respuestas HTTP:**
  * `200 OK`: Autenticación exitosa.
  * `401 Unauthorized`: Credenciales incorrectas.

* **Cuerpo de la Petición (`Request Body`):** `application/json`
  ```json
  {
    "correo": "string",
    "contrasena": "string"
  }
  ```

* **Respuesta de Ejemplo (`200 OK`):** `application/json`
  ```json
  {
    "token": "string",
    "tipo": "string",
    "expiraEnSegundos": 0,
    "rol": "string"
  }
  ```

---

### 3. Autenticación y Perfil

#### `GET /api/v1/auth/me`
Endpoint protegido que devuelve los datos del usuario asociado al token actual.

* **Seguridad:** Requiere Bearer Auth Token.
* **Respuestas HTTP:**
  * `200 OK`: Información de usuario devuelta.
  * `401 Unauthorized`: Sin token, token inválido o expirado.

* **Respuesta de Ejemplo (`200 OK`):** `application/json`
  ```json
  {
    "correo": "string",
    "rol": "string"
  }
  ```

---

### 4. HU-07: Compradores Comerciales

#### `POST /api/v1/compradores`
Registra un nuevo comprador comercial.

* **Respuestas HTTP:**
  * `201 Created`: Registrado con éxito.
  * `400 Bad Request`: Datos de entrada inválidos.
  * `409 Conflict`: NIT o datos duplicados.

* **Cuerpo de la Petición (`Request Body`):** `application/json`
  ```json
  {
    "razonSocial": "string",
    "nit": "1572227051-9",
    "correo": "string",
    "telefono": "+3708682117350",
    "contrasena": "stringst"
  }
  ```

* **Respuesta de Ejemplo (`200 OK` / `201 Created`):** `application/json`
  ```json
  {
    "id": 0,
    "razonSocial": "string",
    "nit": "string",
    "correo": "string",
    "telefono": "string",
    "fechaRegistro": "2026-10-01T03:34:22.793Z"
  }
  ```

---

## 📑 Esquemas de Modelos (Schemas)

### `AgricultorRequest`
| Campo | Tipo | Requerido |
| :--- | :--- | :---: |
| `nombreCompleto` | String | Sí |
| `documento` | String | Sí |
| `correo` | String | Sí |
| `telefono` | String | Sí |
| `municipio` | String | Sí |
| `contrasena` | String | Sí |

### `AgricultorResponse`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `id` | Integer | Identificador único |
| `nombreCompleto` | String | Nombre completo del agricultor |
| `documento` | String | Documento de identidad |
| `correo` | String | Correo electrónico |
| `telefono` | String | Número telefónico |
| `municipio` | String | Municipio de residencia/producción |
| `fechaRegistro` | String (DateTime) | Fecha de creación del registro |

### `CompradorComercialRequest`
| Campo | Tipo | Requerido |
| :--- | :--- | :---: |
| `razonSocial` | String | Sí |
| `nit` | String | Sí |
| `correo` | String | Sí |
| `telefono` | String | Sí |
| `contrasena` | String | Sí |

### `CompradorComercialResponse`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `id` | Integer | Identificador único |
| `razonSocial` | String | Nombre de la empresa o razón social |
| `nit` | String | Número de identificación tributaria |
| `correo` | String | Correo electrónico de contacto |
| `telefono` | String | Teléfono corporativo |
| `fechaRegistro` | String (DateTime) | Fecha de alta en el sistema |

### `LoginRequest`
| Campo | Tipo | Requerido |
| :--- | :--- | :---: |
| `correo` | String | Sí |
| `contrasena` | String | Sí |

### `LoginResponse`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `token` | String | Token JWT generado |
| `tipo` | String | Tipo de token (Ej. `Bearer`) |
| `expiraEnSegundos` | Long | Tiempo de validez del token |
| `rol` | String | Rol asignado al usuario |

### `PerfilResponse`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `correo` | String | Correo del usuario autenticado |
| `rol` | String | Rol del usuario autenticado |