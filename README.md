# Documentación Técnica — Sistema de Gestión de Citas para Barbería

> **Versión:** 0.0.1-SNAPSHOT  
> **Fecha de generación:** Junio 2026  
> **Tecnología principal:** Spring Boot 3.5 · Java 21 · PostgreSQL

---

## Tabla de Contenidos

1. [Descripción General](#1-descripción-general)
2. [Objetivos del Proyecto](#2-objetivos-del-proyecto)
3. [Stack Tecnológico](#3-stack-tecnológico)
4. [Arquitectura del Sistema](#4-arquitectura-del-sistema)
5. [Modelo de Dominio](#5-modelo-de-dominio)
6. [Esquema de Base de Datos](#6-esquema-de-base-de-datos)
7. [Seguridad y Autenticación](#7-seguridad-y-autenticación)
8. [API REST — Endpoints](#8-api-rest--endpoints)
9. [Casos de Uso](#9-casos-de-uso)
10. [Configuración y Despliegue](#10-configuración-y-despliegue)
11. [Variables de Entorno](#11-variables-de-entorno)
12. [Datos Iniciales (Seed)](#12-datos-iniciales-seed)
13. [Avances por Sprint](#13-avances-por-sprint)

---

## 1. Descripción General

El **Sistema de Gestión de Citas para Barbería** es una plataforma web backend desarrollada en Java con Spring Boot, diseñada para digitalizar y centralizar el proceso de agendamiento de citas en una barbería. Permite a clientes, barberos y administradores interactuar de forma ordenada, autónoma y eficiente, eliminando los problemas habituales del agendamiento manual (citas duplicadas, solapamientos, tiempos muertos).

### Roles del sistema

| Rol | Descripción |
|-----|-------------|
| `ADMINISTRADOR` | Gestiona el personal, los servicios y supervisa toda la agenda |
| `BARBERO` | Configura su disponibilidad y consulta su agenda de citas |
| `CLIENTE` | Registra su cuenta, agenda, modifica y cancela sus propias citas |

---

## 2. Objetivos del Proyecto

| ID | Objetivo |
|----|----------|
| OBJ-01 | Facilitar el agendamiento de citas para clientes de manera rápida, autónoma y sin conflictos de horario |
| OBJ-02 | Permitir a los barberos gestionar su disponibilidad y consultar su agenda diaria |
| OBJ-03 | Brindar al administrador herramientas para gestionar personal, servicios y supervisar citas |
| OBJ-04 | Centralizar la información de citas e historial en un único sistema |

---

## 3. Stack Tecnológico

| Capa | Tecnología | Versión | Descripción |
|------|-----------|---------|-------------|
| **Backend** | Spring Boot | 3.5.11 | Framework principal para lógica de negocio y API REST |
| **Lenguaje** | Java | 21 | Lenguaje de programación |
| **Base de Datos** | PostgreSQL | 16 | Almacenamiento persistente |
| **ORM** | Spring Data JPA / Hibernate | — | Mapeo objeto-relacional |
| **Seguridad** | Spring Security + JWT (JJWT) | 0.12.6 | Autenticación stateless y control de acceso por roles |
| **Migraciones** | Flyway | — | Gestión del esquema de base de datos |
| **Documentación API** | SpringDoc OpenAPI (Swagger UI) | 2.8.6 | Documentación interactiva de la API |
| **Generación PDF** | iTextPDF | 8.0.4 | Exportación de agenda en PDF |
| **Utilidades** | Lombok | — | Reducción de código boilerplate |
| **Contenerización** | Docker / Docker Compose | — | Despliegue reproducible |

---

## 4. Arquitectura del Sistema

El proyecto sigue el patrón de **Arquitectura Hexagonal (Ports & Adapters)**, que desacopla la lógica de negocio de los mecanismos de entrada/salida.

```
com.example.barbershop/
├── domain/                        ← Núcleo del dominio (entidades, excepciones)
│   ├── model/                     ← Modelos de dominio puros
│   └── exception/                 ← Excepciones de negocio
│
├── application/                   ← Lógica de aplicación
│   ├── port/
│   │   ├── in/                    ← Casos de uso (interfaces de entrada)
│   │   └── out/                   ← Puertos de salida (interfaces de repositorio)
│   ├── usecase/                   ← Implementaciones de los casos de uso
│   ├── dto/                       ← Objetos de transferencia de datos
│   └── security/                  ← UserContext (contexto del usuario autenticado)
│
└── infrastructure/                ← Adaptadores de infraestructura
    ├── adapter/
    │   ├── in/web/                ← Controllers REST (adaptadores de entrada)
    │   └── out/persistence/       ← Adaptadores JPA, repositorios, exportador PDF
    ├── security/                  ← JWT Filter, SecurityConfig, UserDetails
    └── config/                    ← Configuración CORS, etc.
```

### Flujo de una solicitud típica

```
Cliente HTTP
    │
    ▼
[JwtFilter]          ← valida y extrae el JWT del header Authorization
    │
    ▼
[REST Controller]    ← recibe la petición, delega al caso de uso
    │
    ▼
[UseCase Impl]       ← ejecuta la lógica de negocio
    │
    ▼
[Repository Port]    ← llama al adaptador de persistencia
    │
    ▼
[JPA Repository]     ← consulta / escribe en PostgreSQL
```

---

## 5. Modelo de Dominio

### Entidades principales

#### `User`
Usuario base del sistema.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `email` | String | Correo electrónico (único) |
| `hashPassword` | String | Contraseña hasheada con BCrypt |
| `role` | Role | Rol asignado (ADMINISTRADOR, BARBERO, CLIENTE) |
| `isPasswordTemporary` | boolean | Indica si el barbero debe cambiar su contraseña en el primer login |
| `createdAt` | LocalDateTime | Fecha de creación |

#### `Client`
Perfil extendido para usuarios con rol CLIENTE.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `user` | User | Referencia al usuario base |
| `names` | String | Nombres |
| `lastNames` | String | Apellidos |
| `phone` | String | Teléfono de contacto |

#### `Employee`
Perfil extendido para usuarios con rol BARBERO o ADMINISTRADOR.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `user` | User | Referencia al usuario base |
| `documentNumber` | String | Número de documento (único) |
| `names` | String | Nombres |
| `lastNames` | String | Apellidos |
| `phone` | String | Teléfono |
| `address` | String | Dirección (opcional) |
| `isActive` | boolean | Estado activo/inactivo |

#### `Service`
Servicio ofrecido por la barbería.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `name` | String | Nombre del servicio |
| `description` | String | Descripción detallada |
| `price` | BigDecimal | Precio en moneda local |
| `durationMinutes` | int | Duración en minutos |
| `isActive` | boolean | Estado activo/inactivo |

#### `Availability`
Disponibilidad horaria de un barbero en una fecha específica.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `employee` | Employee | Barbero al que corresponde |
| `date` | LocalDate | Fecha de disponibilidad |
| `startTime` | LocalTime | Hora de inicio |
| `endTime` | LocalTime | Hora de fin |

#### `Appointment`
Cita agendada.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `client` | Client | Cliente que agenda |
| `employee` | Employee | Barbero asignado |
| `date` | LocalDate | Fecha de la cita |
| `startTime` | LocalTime | Hora de inicio |
| `endTime` | LocalTime | Hora de fin (calculada sumando duración de servicios) |
| `status` | AppointmentStatus | Estado de la cita |
| `totalPrice` | BigDecimal | Precio total de la cita |

#### `AppointmentStatus` (enum)

| Valor | Descripción |
|-------|-------------|
| `PENDING` | Cita pendiente de confirmación o atención |
| `CONFIRMED` | Cita confirmada |
| `CANCELLED` | Cita cancelada |
| `COMPLETED` | Cita completada |

#### `Cancellation`
Registro de cada cancelación realizada.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `userId` | Long | ID del usuario que canceló |
| `appointmentId` | Long | ID de la cita cancelada |
| `cancellationDate` | LocalDateTime | Fecha y hora de la cancelación |
| `reason` | String | Motivo de la cancelación |
| `cancelledBy` | Role | Rol del usuario que canceló |

---

## 6. Esquema de Base de Datos

El esquema es gestionado automáticamente por **Flyway** mediante el script `V1__init.sql`.

```
roles
  └── users (role_id → roles.id)
        ├── clients (user_id → users.id)
        └── employees (user_id → users.id)
              ├── employee_services (employee_id + service_id)
              │     └── services (service_id → services.id)
              ├── availability (employee_id → employees.id)
              └── appointments (employee_id → employees.id)
                    ├── appointment_services (appointment_id → appointments.id)
                    │     └── services (service_id → services.id)
                    └── cancellations (appointment_id → appointments.id)
```

### Tablas

| Tabla | Descripción |
|-------|-------------|
| `roles` | Catálogo de roles del sistema |
| `users` | Usuarios con credenciales y rol |
| `clients` | Datos del perfil de cliente |
| `employees` | Datos del perfil de empleado/barbero |
| `services` | Servicios disponibles en la barbería |
| `employee_services` | Relación N:M entre empleados y servicios que ofrecen |
| `availability` | Franjas horarias de disponibilidad por empleado |
| `appointments` | Citas agendadas |
| `appointment_services` | Servicios incluidos en cada cita |
| `cancellations` | Registro histórico de cancelaciones |

---

## 7. Seguridad y Autenticación

### Mecanismo

El sistema usa autenticación **stateless con JWT (Bearer Token)**. No se mantienen sesiones en el servidor.

### Flujo de autenticación

1. El cliente envía `POST /api/auth/login` con email y contraseña.
2. El servidor valida las credenciales y devuelve un JWT firmado.
3. En las siguientes peticiones, el cliente incluye el token en el header: `Authorization: Bearer <token>`.
4. El `JwtFilter` intercepta cada petición, valida el token y carga el usuario en el contexto de Spring Security.

### Configuración JWT

| Parámetro | Valor por defecto | Variable de entorno |
|-----------|------------------|---------------------|
| Algoritmo | HS256 | — |
| Expiración | 24 horas (86400000 ms) | `JWT_EXPIRATION_MS` |
| Clave secreta | `dev-secret-key-minimo-256-bits-para-hs256-algoritmo` | `JWT_SECRET` |


### Control de acceso por rol

| Endpoint | Rol requerido |
|----------|--------------|
| `/api/auth/**` | Público (sin autenticación) |
| `/api/appointments/services/**`, `/employees/**`, `/summary` | Público |
| `/api/appointments/confirm` | `ROLE_CLIENTE` |
| `/api/appointments/cancel` | `ROLE_CLIENTE` o `ROLE_BARBERO` |
| `/api/appointments/my` | `ROLE_CLIENTE` |
| `/api/admin/**` | `ROLE_ADMINISTRADOR` |
| `/api/employee/**` | `ROLE_BARBERO` |

### Contraseña temporal de barberos

Cuando el administrador registra un barbero, la contraseña se marca como temporal (`isPasswordTemporary = true`). En su primer ingreso, el barbero **debe** cambiar su contraseña mediante `POST /api/auth/change-password` antes de poder operar.

---

## 8. API REST — Endpoints

La documentación interactiva completa está disponible en **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

### Auth — `/api/auth`

#### `POST /api/auth/login`
Autentica un usuario y retorna un JWT.

**Request body:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "MiContraseña123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "CLIENTE"
}
```

---

#### `POST /api/auth/register`
Registra un nuevo cliente en el sistema.

**Request body:**
```json
{
  "email": "cliente@ejemplo.com",
  "password": "Segura123",
  "names": "Juan",
  "lastNames": "Pérez",
  "phone": "3001234567"
}
```

**Response `201 Created`:**
```json
{
  "id": 5,
  "email": "cliente@ejemplo.com",
  "names": "Juan",
  "lastNames": "Pérez"
}
```

---

#### `POST /api/auth/change-password`
Cambia la contraseña del usuario autenticado. Obligatorio para barberos en su primer ingreso.

**Headers:** `Authorization: Bearer <token>`

**Request body:**
```json
{
  "currentPassword": "TemporalPass1",
  "newPassword": "NuevaSegura456",
  "confirmPassword": "NuevaSegura456"
}
```

**Response `204 No Content`**

---

### Citas — `/api/appointments`

#### `GET /api/appointments/services`
Lista todos los servicios que tienen al menos un barbero disponible.

**Response `200 OK`:** Lista de `ServiceAvailabilityResponse`

---

#### `GET /api/appointments/services/{serviceId}/employees`
Lista los barberos que ofrecen el servicio indicado y tienen disponibilidad.

**Path param:** `serviceId` — ID del servicio

**Response `200 OK`:** Lista de `EmployeeAvailabilityResponse`

---

#### `GET /api/appointments/employees/{employeeId}/dates`
Retorna las fechas futuras con disponibilidad de un barbero.

**Path param:** `employeeId` — ID del empleado/barbero

**Response `200 OK`:** `AvailableDatesResponse` con lista de fechas disponibles

---

#### `GET /api/appointments/employees/{employeeId}/slots`
Retorna los slots horarios disponibles para un barbero en una fecha y con los servicios solicitados.

**Query params:**
- `date` (ISO date, ej: `2026-05-20`)
- `serviceIds` (lista de IDs de servicios, ej: `1,3`)

**Response `200 OK`:** Lista de `SlotResponse` con hora de inicio disponible

---

#### `GET /api/appointments/summary`
Muestra un resumen de la cita antes de confirmarla.

**Query params:**
- `employeeId`
- `date`
- `startTime` (ej: `11:30:00`)
- `serviceIds`

**Response `200 OK`:** `AppointmentSummaryResponse` con datos del barbero, servicios, precio total y hora de fin

---

#### `POST /api/appointments/confirm` `CLIENTE`
Confirma y agenda la cita.

**Headers:** `Authorization: Bearer <token>`

**Request body:**
```json
{
  "employeeId": 2,
  "date": "2026-05-20",
  "startTime": "11:30:00",
  "serviceIds": [1, 5]
}
```

**Response `201 Created`:** `ConfirmAppointmentResponse` con los datos completos de la cita creada

---

#### `POST /api/appointments/cancel` `CLIENTE` o `BARBERO`
Cancela una cita existente.

**Headers:** `Authorization: Bearer <token>`

**Request body:**
```json
{
  "appointmentId": 10,
  "reason": "No podré asistir"
}
```

**Response `200 OK`:** `CancelAppointmentResponse` con confirmación de la cancelación

> Los barberos deben proporcionar siempre un motivo. Los clientes tienen restricciones de anticipación mínima para cancelar.

---

#### `GET /api/appointments/my` `CLIENTE`
Lista todas las citas del cliente autenticado.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** Lista de `AppointmentResponse` con historial completo de citas

---

### Administrador — `/api/admin` `ADMINISTRADOR`

#### `POST /api/admin/employees`
Registra un nuevo barbero. La contraseña se genera como temporal.

**Request body:**
```json
{
  "email": "barbero@barberia.com",
  "password": "TempPass1",
  "documentNumber": "12345678",
  "names": "Carlos",
  "lastNames": "Gómez",
  "phone": "3109876543",
  "address": "Calle 10 #5-20",
  "serviceIds": [1, 2, 5]
}
```

**Response `201 Created`:** `RegisterEmployeeResponse`

---

#### `GET /api/admin/employees/{employeeId}/schedule`
Consulta el horario de un barbero en un rango de fechas.

**Query params:**
- `from` (ISO date)
- `to` (ISO date)

**Response `200 OK`:** `BarberScheduleResponse` con slots y citas del periodo

---

#### `POST /api/admin/services`
Crea un nuevo servicio disponible en la barbería.

**Request body:**
```json
{
  "name": "Corte Clásico",
  "description": "Corte tradicional con tijera",
  "price": 15000,
  "durationMinutes": 30
}
```

**Response `201 Created`:** `ServiceResponse`

---

### Empleado/Barbero — `/api/employee` `BARBERO`

#### `POST /api/employee/availability`
Configura la disponibilidad horaria semanal del barbero autenticado.

**Headers:** `Authorization: Bearer <token>`

**Request body:**
```json
{
  "schedules": [
    {
      "date": "2026-05-19",
      "startTime": "08:00:00",
      "endTime": "17:00:00"
    },
    {
      "date": "2026-05-20",
      "startTime": "09:00:00",
      "endTime": "15:00:00"
    }
  ]
}
```

**Response `201 Created`:** Lista de `AvailabilityResponse`

---

#### `GET /api/employee/agenda`
Consulta la agenda futura del barbero autenticado.

**Headers:** `Authorization: Bearer <token>`

**Query params:**
- `vista` — `DIA` o `SEMANA`
- `fecha` — Fecha de referencia (opcional, por defecto hoy)
- `navegacion` — `ANTERIOR` o `SIGUIENTE` (opcional)

**Response `200 OK`:** `BarberAgendaResponse` con citas agrupadas según la vista seleccionada

---

#### `GET /api/employee/agenda/export`
Exporta la agenda del barbero en formato PDF.

**Headers:** `Authorization: Bearer <token>`

**Query params:** Iguales a `/agenda`

**Response `200 OK`:** Archivo PDF (`application/pdf`) descargable como `agenda.pdf`

---

## 9. Casos de Uso

| Caso de Uso | Implementación | Rol |
|-------------|---------------|-----|
| Login | `LoginUseCaseImpl` | Todos |
| Registrar cliente | `RegisterClientUseCaseImpl` | Público |
| Registrar barbero | `RegisterEmployeeUseCaseImpl` | Administrador |
| Cambiar contraseña | `ChangePasswordUseCaseImpl` | Todos |
| Gestionar servicios | `ManageServicesUseCaseImpl` | Administrador |
| Ver horario de barbero | `BarberScheduleUseCaseImpl` | Administrador |
| Configurar disponibilidad | `ScheduleAvailabilityUseCaseImpl` | Barbero |
| Ver agenda futura | `BarberAgendaUseCaseImpl` | Barbero |
| Exportar agenda PDF | `BarberAgendaUseCaseImpl` + `AgendaPdfExporterImpl` | Barbero |
| Consultar servicios/barberos/slots | `AppointmentUseCaseImpl` | Público / Cliente |
| Confirmar cita | `AppointmentUseCaseImpl` | Cliente |
| Cancelar cita | `AppointmentUseCaseImpl` | Cliente, Barbero |
| Ver mis citas | `GetAppointmentsUseCaseImpl` | Cliente |

### Reglas de negocio destacadas

- **Sin solapamiento de citas:** Al confirmar una cita, el sistema valida que el barbero no tenga otro turno en el mismo rango horario.
- **Disponibilidad requerida:** Solo se pueden agendar citas en franjas que el barbero haya habilitado previamente.
- **Cálculo automático de hora fin:** La hora de fin se calcula sumando la duración de todos los servicios seleccionados.
- **Precio total automático:** El precio total de la cita es la suma de los precios de los servicios en el momento del agendamiento.
- **Cancelación con motivo obligatorio para barberos:** Los barberos deben especificar el motivo al cancelar una cita.
- **Límite de cancelaciones:** Los clientes tienen un tope de cancelaciones (`CancellationLimitExceededException`).
- **Cambio de contraseña obligatorio:** Los barberos creados por el administrador deben cambiar su contraseña temporal en el primer acceso.
- **Registro de cancelaciones:** Toda cancelación queda auditada en la tabla `cancellations`.

---

## 10. Configuración y Despliegue

### Requisitos previos

- Java 21
- Maven 3.9+
- Docker y Docker Compose (para despliegue contenerizado)

### Ejecución local con Docker Compose

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd barbershop

# 2. Crear el archivo de variables de entorno
echo "JWT_SECRET=mi-clave-secreta-super-segura-de-al-menos-256-bits" > .env

# 3. Levantar la base de datos y la aplicación
docker-compose up -d

# 4. La aplicación estará disponible en:
#    API:        http://localhost:8080
#    Swagger UI: http://localhost:8080/swagger-ui.html
```

### Ejecución local sin Docker

```bash
# 1. Tener PostgreSQL corriendo en el puerto 5433
# 2. Crear la base de datos: barbershop_db
# 3. Ejecutar la aplicación
./mvnw spring-boot:run

# O compilar y ejecutar el JAR:
./mvnw clean package -DskipTests
java -jar target/barbershop-0.0.1-SNAPSHOT.jar
```

### Dockerfile

La imagen se construye en dos etapas:
1. **Build:** Compila el proyecto con Maven.
2. **Runtime:** Ejecuta el JAR resultante sobre una imagen JRE 21 slim.

```
Puerto expuesto: 8080
```

---

## 11. Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|------------------|
| `SPRING_DATASOURCE_URL` | URL JDBC de la base de datos | `jdbc:postgresql://localhost:5433/barbershop_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `barberia_user` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `barberia_pass` |
| `JWT_SECRET` | Clave secreta para firmar JWT (mínimo 256 bits) | `dev-secret-key-...` *(solo desarrollo)* |
| `JWT_EXPIRATION_MS` | Tiempo de expiración del JWT en milisegundos | `86400000` (24 h) |

---

## 12. Datos Iniciales (Seed)

Al arrancar por primera vez, Flyway ejecuta automáticamente `V1__init.sql`, que inserta:

### Roles
- `ADMINISTRADOR`
- `BARBERO`
- `CLIENTE`

### Usuario administrador por defecto
| Campo | Valor |
|-------|-------|
| Email | `admin@barberia.com` |
| Contraseña | `Admin123` |
| Rol | `ADMINISTRADOR` |

### Servicios precargados (15 servicios)

| Servicio | Precio | Duración |
|----------|--------|----------|
| Corte de Cabello Masculino | $15.000 | 30 min |
| Corte con Lavado y Peinado | $20.000 | 45 min |
| Corte de Cabello Infantil | $13.000 | 30 min |
| Rapado Uniforme | $10.000 | 20 min |
| Arreglo de Barba Tradicional | $12.000 | 25 min |
| Afeitado Completo | $15.000 | 30 min |
| Perfilado de Cejas | $5.000 | 15 min |
| Pigmentación de Barba | $18.000 | 30 min |
| Limpieza Facial Express | $15.000 | 20 min |
| Mascarilla de Carbón Activado | $12.000 | 15 min |
| Tratamiento de Ojeras | $10.000 | 15 min |
| Exfoliación + Mascarilla Hidratante | $22.000 | 35 min |
| Tratamiento Anticaída | $25.000 | 20 min |
| Camuflaje de Canas | $30.000 | 40 min |
| Alisado Keratina Flequillo/Superior | $40.000 | 60 min |

---

## Manejo de Errores

El sistema cuenta con un `GlobalExceptionHandler` centralizado que convierte las excepciones de dominio en respuestas HTTP apropiadas:

| Excepción | Código HTTP |
|-----------|-------------|
| `InvalidCredentialsException` | 401 Unauthorized |
| `AppointmentNotFoundException` | 404 Not Found |
| `EmployeeNotFoundException` | 404 Not Found |
| `ServiceNotFoundException` | 404 Not Found |
| `SlotNotAvailableException` | 409 Conflict |
| `AppointmentAlreadyCancelledException` | 409 Conflict |
| `AppointmentNotOwnedByUserException` | 403 Forbidden |
| `CancellationLimitExceededException` | 422 Unprocessable Entity |
| `EmailAlreadyExistsException` | 409 Conflict |
| `DocumentAlreadyExistsException` | 409 Conflict |
| `PasswordMismatchException` | 400 Bad Request |
| `SamePasswordException` | 400 Bad Request |
| `BarberCancellationReasonNotProvidedException` | 400 Bad Request |
| Errores de validación (`@Valid`) | 400 Bad Request |

---

## 13. Avances por Sprint
### Sprint 1
- Login con roles diferenciados
  
- Registro de clientes
  
- Registro de barberos (por el administrador)

- Agregar disponibilidad semanal del barbero

- Agendar cita (cliente selecciona servicio, fecha, barbero y hora)


### Sprint 2 
- Cancelar cita para barbero

- Cancelar cita para cliente
  
- Visualizar jornada laboral del barbero

- Visualizar agenda futura del barbero

- Visualizar citas del cliente

### Sprint 3

#### HU-01 — Configurar Jornadas Laborales

El administrador puede definir, consultar, modificar y eliminar la jornada laboral semanal (días y horarios) de cada barbero. Esto actúa como una **plantilla recurrente** independiente de la disponibilidad por fecha.

**Casos de uso involucrados:**
- `WorkScheduleUseCaseImpl` → `create`, `update`, `delete`, `findByEmployee`

**Reglas de negocio:**
- La hora de inicio debe ser anterior a la hora de fin.
- El horario debe estar dentro de la franja permitida: **08:00 – 19:00**.
- No se puede registrar más de una jornada para el mismo `dayOfWeek` del mismo barbero (`OverlappingScheduleException`).
- Al actualizar, el solapamiento se valida excluyendo el registro que se está editando.

**Endpoints:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/admin/employees/{employeeId}/schedules` | Crea una jornada laboral para el barbero |
| `PUT` | `/api/admin/employees/schedules/{scheduleId}` | Actualiza una jornada existente |
| `DELETE` | `/api/admin/employees/schedules/{scheduleId}` | Elimina una jornada laboral |
| `GET` | `/api/admin/employees/{employeeId}/schedules` | Lista todas las jornadas del barbero |

**Request body (`POST` / `PUT`):**
```json
{
  "dayOfWeek": "MONDAY",
  "startTime": "08:00:00",
  "endTime": "17:00:00"
}
```

**Response `201 Created` / `200 OK`:**
```json
{
  "id": 1,
  "employeeId": 2,
  "employeeNames": "Carlos",
  "employeeLastNames": "Gómez",
  "dayOfWeek": "MONDAY",
  "startTime": "08:00:00",
  "endTime": "17:00:00"
}
```

**Excepciones manejadas:**

| Excepción | Código HTTP |
|-----------|-------------|
| `EmployeeNotFoundException` | 404 Not Found |
| `InvalidScheduleException` | 400 Bad Request |
| `OverlappingScheduleException` | 409 Conflict |
| `WorkScheduleNotFoundException` | 404 Not Found |

---

#### HU-02 — Modificar Cita (Cliente)

El cliente puede modificar una cita propia que esté en estado `CONFIRMADA`, cambiando el barbero, la fecha, la hora de inicio o los servicios, de forma parcial (solo los campos enviados son actualizados).

**Caso de uso involucrado:**
- `ModifyAppointmentUseCaseImpl` → `modify`

**Reglas de negocio:**
- Solo el cliente propietario de la cita puede modificarla (`UnauthorizedAccessException`).
- Solo se pueden modificar citas en estado `CONFIRMADA`; cualquier otro estado lanza `AppointmentNotModifiableException`.
- Si se cambia el barbero o los servicios, se valida que el barbero tenga asignados todos los servicios solicitados (`ServiceNotAssignedToEmployeeException`).
- Si se cambia fecha, hora o barbero, se valida que el nuevo horario esté dentro de la disponibilidad registrada del barbero (`SlotNotAvailableException`).
- Se verifica que no exista solapamiento con otras citas confirmadas del barbero, excluyendo la cita actual.
- La hora de fin se recalcula sumando la duración total de los servicios.
- El precio total se recalcula con los precios vigentes de los servicios.
- Al completar la modificación, el estado de la cita cambia a `MODIFICADA`.

**Endpoint:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| `PATCH` | `/api/appointments/{appointmentId}/update` | Modifica una cita del cliente autenticado |

**Headers:** `Authorization: Bearer <token>`

**Request body (todos los campos son opcionales; omitir para no modificar):**
```json
{
  "employeeId": 3,
  "date": "2026-06-15",
  "startTime": "10:00:00",
  "serviceIds": [1, 3]
}
```

**Response `200 OK`:**
```json
{
  "appointmentId": 10,
  "employeeNames": "Carlos",
  "employeeLastNames": "Gómez",
  "date": "2026-06-15",
  "startTime": "10:00:00",
  "endTime": "11:00:00",
  "services": ["Corte de Cabello Masculino", "Arreglo de Barba Tradicional"],
  "totalPrice": 27000,
  "totalDuration": 55,
  "status": "MODIFICADA",
  "message": "Tu cita ha sido modificada exitosamente."
}
```

**Excepciones manejadas:**

| Excepción | Código HTTP |
|-----------|-------------|
| `AppointmentNotFoundException` | 404 Not Found |
| `UnauthorizedAccessException` | 403 Forbidden |
| `AppointmentNotModifiableException` | 409 Conflict |
| `EmployeeNotFoundException` | 404 Not Found |
| `ServiceNotFoundException` | 404 Not Found |
| `ServiceNotAssignedToEmployeeException` | 409 Conflict |
| `SlotNotAvailableException` | 409 Conflict |

---

#### HU-03 — Editar Información Barbero

El administrador puede actualizar los datos personales y los servicios asignados de un barbero existente. Todos los campos son opcionales; solo se actualizan los que se envíen con valor.

**Caso de uso involucrado:**
- `UpdateEmployeeUseCaseImpl` → `update`

**Reglas de negocio:**
- El barbero debe existir en el sistema (`EmployeeNotFoundException`).
- Si se envía `serviceIds`, todos los IDs deben corresponder a servicios existentes; de lo contrario lanza `ServiceNotFoundException`.
- Al enviar `serviceIds`, la lista de servicios del barbero se reemplaza completamente con los indicados.
- Los campos de texto vacíos o en blanco son ignorados (no sobreescriben el valor actual).

**Endpoint:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| `PATCH` | `/api/admin/employees/{employeeId}/update` | Actualiza los datos del barbero |

**Request body (todos los campos son opcionales):**
```json
{
  "names": "Carlos Alberto",
  "lastNames": "Gómez Ruiz",
  "phone": "3009876543",
  "address": "Carrera 45 #20-10",
  "serviceIds": [1, 3, 7]
}
```

**Response `200 OK`:**
```json
{
  "employeeId": 2,
  "names": "Carlos Alberto",
  "lastNames": "Gómez Ruiz",
  "phone": "3009876543",
  "address": "Carrera 45 #20-10",
  "isActive": true,
  "services": ["Corte de Cabello Masculino", "Arreglo de Barba Tradicional", "Perfilado de Cejas"],
  "message": "Los datos del barbero han sido actualizados correctamente."
}
```

**Excepciones manejadas:**

| Excepción | Código HTTP |
|-----------|-------------|
| `EmployeeNotFoundException` | 404 Not Found |
| `ServiceNotFoundException` | 404 Not Found |

---

#### HU-04 — Editar Cliente

El cliente puede actualizar sus propios datos de perfil. El administrador también puede editar los datos de cualquier cliente. Los campos son opcionales y solo se actualizan los enviados con valor.

**Caso de uso involucrado:**
- `UpdateClientUseCaseImpl` → `update`

**Reglas de negocio:**
- El cliente debe existir en el sistema (`ClientNotFoundException`).
- Solo el propio cliente o un `ADMINISTRADOR` pueden editar el perfil; cualquier otro usuario recibe `UnauthorizedAccessException`.
- Los campos de texto vacíos o en blanco son ignorados.
- El email no es modificable a través de este endpoint.

**Endpoint:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| `PATCH` | `/api/clients/{clientId}/update` | Actualiza los datos del cliente |

**Headers:** `Authorization: Bearer <token>`

**Request body (todos los campos son opcionales):**
```json
{
  "names": "Juan Carlos",
  "lastNames": "Pérez García",
  "phone": "3001234567"
}
```

**Response `200 OK`:**
```json
{
  "clientId": 5,
  "names": "Juan Carlos",
  "lastNames": "Pérez García",
  "phone": "3001234567",
  "email": "cliente@ejemplo.com",
  "message": "Los datos del cliente han sido actualizados correctamente."
}
```

**Excepciones manejadas:**

| Excepción | Código HTTP |
|-----------|-------------|
| `ClientNotFoundException` | 404 Not Found |
| `UnauthorizedAccessException` | 403 Forbidden |

---

#### HU-05 — Agregar Servicios

El administrador puede crear nuevos servicios disponibles en la barbería, así como actualizar los datos de servicios ya existentes y consultarlos individualmente o en lista.

**Caso de uso involucrado:**
- `ManageServicesUseCaseImpl` → `createService`, `updateService`, `getAllServices`, `getServiceById`

**Reglas de negocio:**
- El nombre del servicio no puede estar en blanco (`IllegalArgumentException`).
- No se pueden crear dos servicios con el mismo nombre, independientemente de mayúsculas o minúsculas (`DuplicateResourceException`).
- El precio debe ser mayor a cero.
- La duración en minutos debe ser mayor a cero.
- Al crear un servicio, su estado inicial es `active = true`.
- Al actualizar, si el nombre cambia, se valida de nuevo la unicidad excluyendo el servicio actual.
- Los precios se formatean en moneda colombiana (COP) en las respuestas.

**Endpoints:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/admin/services` | Crea un nuevo servicio |
| `PUT` | `/api/admin/services/{id}` | Actualiza los datos de un servicio existente |
| `GET` | `/api/admin/services` | Lista todos los servicios (activos e inactivos) |
| `GET` | `/api/admin/services/{id}` | Consulta un servicio por ID |

**Request body (`POST` / `PUT`):**
```json
{
  "name": "Corte Degradé",
  "description": "Corte moderno con transición degradada",
  "price": 18000,
  "durationMinutes": 35
}
```

**Response `201 Created` / `200 OK`:**
```json
{
  "id": 16,
  "name": "Corte Degradé",
  "description": "Corte moderno con transición degradada",
  "price": "$ 18.000,00",
  "durationMinutes": 35,
  "active": true
}
```

**Excepciones manejadas:**

| Excepción | Código HTTP |
|-----------|-------------|
| `DuplicateResourceException` | 409 Conflict |
| `ResourceNotFoundException` | 404 Not Found |
| `IllegalArgumentException` (validación interna) | 400 Bad Request |

---

#### HU-06 — Deshabilitar Servicio

El administrador puede deshabilitar un servicio existente para que no esté disponible para nuevas citas. Los servicios deshabilitados no se eliminan del sistema; las citas activas que ya lo incluyen se mantienen hasta su ejecución o cancelación.

**Caso de uso involucrado:**
- `DisableServiceUseCaseImpl` → `disable`

**Reglas de negocio:**
- El servicio debe existir (`ServiceNotFoundException`).
- No se puede deshabilitar un servicio que ya está inactivo (`ServiceAlreadyDisabledException`).
- El deshabilitar un servicio **no cancela** las citas activas que ya lo incluyen; estas continúan su ciclo normal.
- La respuesta informa cuántas citas activas están asociadas al servicio deshabilitado.
- Un servicio deshabilitado (`active = false`) no aparece en la consulta pública de servicios disponibles para agendar citas.

**Endpoint:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| `PATCH` | `/api/admin/services/{serviceId}/disable` | Deshabilita un servicio |

**Response `200 OK` (sin citas activas):**
```json
{
  "serviceId": 3,
  "name": "Corte de Cabello Infantil",
  "isActive": false,
  "activeAppointmentsCount": 0,
  "message": "El servicio 'Corte de Cabello Infantil' fue deshabilitado correctamente."
}
```

**Response `200 OK` (con citas activas asociadas):**
```json
{
  "serviceId": 3,
  "name": "Corte de Cabello Infantil",
  "isActive": false,
  "activeAppointmentsCount": 2,
  "message": "El servicio 'Corte de Cabello Infantil' fue deshabilitado. Existen 2 cita(s) activa(s) asociadas que se mantendrán hasta su ejecución o cancelación normal."
}
```

**Excepciones manejadas:**

| Excepción | Código HTTP |
|-----------|-------------|
| `ServiceNotFoundException` | 404 Not Found |
| `ServiceAlreadyDisabledException` | 409 Conflict |

---

### Tabla de casos de uso actualizada (incluyendo Sprint 3)

| Caso de Uso | Implementación | Rol |
|-------------|---------------|-----|
| Login | `LoginUseCaseImpl` | Todos |
| Registrar cliente | `RegisterClientUseCaseImpl` | Público |
| Registrar barbero | `RegisterEmployeeUseCaseImpl` | Administrador |
| Cambiar contraseña | `ChangePasswordUseCaseImpl` | Todos |
| Gestionar servicios | `ManageServicesUseCaseImpl` | Administrador |
| Ver horario de barbero | `BarberScheduleUseCaseImpl` | Administrador |
| Configurar disponibilidad | `ScheduleAvailabilityUseCaseImpl` | Barbero |
| Ver agenda futura | `BarberAgendaUseCaseImpl` | Barbero |
| Exportar agenda PDF | `BarberAgendaUseCaseImpl` + `AgendaPdfExporterImpl` | Barbero |
| Consultar servicios/barberos/slots | `AppointmentUseCaseImpl` | Público / Cliente |
| Confirmar cita | `AppointmentUseCaseImpl` | Cliente |
| Cancelar cita | `AppointmentUseCaseImpl` | Cliente, Barbero |
| Ver mis citas | `GetAppointmentsUseCaseImpl` | Cliente |
| **Configurar jornada laboral** | `WorkScheduleUseCaseImpl` | **Administrador** |
| **Modificar cita** | `ModifyAppointmentUseCaseImpl` | **Cliente** |
| **Editar información barbero** | `UpdateEmployeeUseCaseImpl` | **Administrador** |
| **Editar cliente** | `UpdateClientUseCaseImpl` | **Administrador, Cliente** |
| **Agregar / actualizar servicios** | `ManageServicesUseCaseImpl` | **Administrador** |
| **Deshabilitar servicio** | `DisableServiceUseCaseImpl` | **Administrador** |

---

### Nuevos endpoints — Control de acceso (Sprint 3)

Adición a la tabla de control de acceso por rol de la sección 7:

| Endpoint | Rol requerido |
|----------|--------------| 
| `POST /api/admin/employees/{id}/schedules` | `ROLE_ADMINISTRADOR` |
| `PUT /api/admin/employees/schedules/{id}` | `ROLE_ADMINISTRADOR` |
| `DELETE /api/admin/employees/schedules/{id}` | `ROLE_ADMINISTRADOR` |
| `GET /api/admin/employees/{id}/schedules` | `ROLE_ADMINISTRADOR` |
| `PATCH /api/admin/employees/{id}/update` | `ROLE_ADMINISTRADOR` |
| `PATCH /api/admin/services/{id}/disable` | `ROLE_ADMINISTRADOR` |
| `PATCH /api/appointments/{id}/update` | `ROLE_CLIENTE` |
| `PATCH /api/clients/{id}/update` | `ROLE_CLIENTE` o `ROLE_ADMINISTRADOR` |

### Nuevas excepciones — Manejo de errores (Sprint 3)

Adición a la tabla de excepciones de la sección "Manejo de Errores":

| Excepción | Código HTTP |
|-----------|-------------|
| `ClientNotFoundException` | 404 Not Found |
| `AppointmentNotModifiableException` | 409 Conflict |
| `ServiceNotAssignedToEmployeeException` | 409 Conflict |
| `UnauthorizedAccessException` | 403 Forbidden |
| `WorkScheduleNotFoundException` | 404 Not Found |
| `OverlappingScheduleException` | 409 Conflict |
| `InvalidScheduleException` | 400 Bad Request |
| `ServiceAlreadyDisabledException` | 409 Conflict |
| `DuplicateResourceException` | 409 Conflict |
| `ResourceNotFoundException` | 404 Not Found |
