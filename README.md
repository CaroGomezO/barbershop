# Sistema de Gestión de Citas para una Barbería

## Tabla de Contenido
- [Sistema de Gestión de Citas para una Barbería](#sistema-de-gestión-de-citas-para-una-barbería)
  - [Tabla de Contenido](#tabla-de-contenido)
  - [Contexto del Negocio](#contexto-del-negocio)
  - [Necesidades del Negocio](#necesidades-del-negocio)
  - [Problema a Resolver](#problema-a-resolver)
  - [Objetivos del Proyecto](#objetivos-del-proyecto)
  - [Stack Tecnológico](#stack-tecnológico)
  - [Alcance de la Solución](#alcance-de-la-solución)
  - [Avance por Sprints](#avance-por-sprints)
    - [Sprint 1](#sprint-1)
    - [Sprint 2](#sprint-2)

## Contexto del Negocio
Las barberías son negocios de atención personalizada que dependen directamente de la organización de su agenda para operar de manera eficiente. Actualmente la mayoría de estos negocios gestiona sus citas de forma manual, ya sea por llamadas, por WhatsApp o físicamente en el local, lo que genera problemas frecuentes como citas duplicadas, olvidos, tiempos muertos entre servicios, citas solapadas y una experiencia poco profesional para el cliente.
Es por esto que este proyecto busca desarrollar una plataforma web que digitalice y centralice el proceso de agendamiento de citas para una barbería, permitiendo que clientes, barberos y el administrador interactúen de manera ordenada, autónoma y eficiente.


## Necesidades del Negocio
1. Eliminar los conflictos de agenda causados por citas solapadas o mal   asignadas.

2. Permitir que los clientes agenden sus propias citas sin depender del personal.

3. Dar a cada barbero visibilidad sobre su propia agenda y control sobre su propia disponibilidad.

4. Centralizar la gestión de servicios, precios y personal en un único sistema.


## Problema a Resolver
La barbería no cuenta con una herramienta digital para gestionar reservas. Esto provoca:

- Sobreocupación de barberos por falta de control de disponibilidad real.

- Pérdida de clientes por dificultad para agendar o consultar disponibilidad.

- Dificultad del administrador para supervisar y corregir agenda.

- Ausencia de un registro centralizado de citas y servicios prestados.


## Objetivos del Proyecto
| N° | Objetivo |
| --- | --- |
| OBJ-01 | Facilitar el proceso de agendamiento de citas para los clientes de manera rápida, autónoma y sin conflictos de horario |
| OBJ-02 | Permitir a los barberos gestionar su propia disponibilidad y consultar su agenda diaria |
| OBJ-03 | Brindar al administrador las herramientas necesarias para gestionar el personal, los servicios y supervisar las citas del sistema |
| OBJ-04 | Facilitar el proceso para centralizar la información de citas e historial |

## Stack Tecnológico 

|  | Tecnología | Descripción |
| --- | --- | --- |
| **Backend** | Spring Boot | Framework principal para la lógica de negocio y API REST | 
| **Base de Datos** | PostgreSQL | Almacenamiento persistente de usuarios, citas y servicios |
| **Seguridad** | Spring Security + JWT | Autenticación y control de acceso por roles |
| **ORM** | Spring Data JPA / Hibernate | Mapeo objeto-relacional con la base de datos |


## Alcance de la Solución
| N° | Funcionalidad | Rol |
|----|---------------|-----|
| 1 | Registro autónomo de clientes | Cliente |
| 2 | Autenticación con roles diferenciados | Admin, Barbero, Cliente |
| 3 | Gestión de barberos (registro y consulta) | Admin |
| 4 | Gestión de servicios (nombre, precio y duración) | Admin |
| 5 | Configuración de disponibilidad semanal | Barbero |
| 6 | Visualización de barberos disponibles por servicio y fecha | Cliente |
| 7 | Agendamiento de citas con servicio, fecha, barbero y hora | Cliente |
| 8 | Modificación de citas (fecha, barbero y hora) | Cliente |
| 9 | Cancelación de citas | Cliente |
| 10 | Cancelación de citas con restricción de anticipación mínima | Barbero |
| 11 | Visualización de citas con datos del cliente y servicio | Barbero |
| 12 | Supervisión, edición y cancelación de citas *(solo ante urgencias)* | Admin |
| 13 | Visualización de usuarios registrados | Admin |
| 14 | Visualización del historial de citas | Cliente |
| 15 | Cambio obligatorio de contraseña en primer ingreso del barbero | Barbero |


## Avance por Sprints
### Sprint 1
- Login con roles diferenciados
  
- Registro de clientes
  
- Registro de barberos (por el administrador)

- Agregar disponibilidad semanal del barbero

- Agendar cita (cliente selecciona servicio, fecha, barbero y hora)


### Sprint 2
- Agregar servicios al sistema
  
- Cancelar cita
  
- Visualizar jornada laboral del barbero

- Visualizar agenda futura del barbero

- Visualizar citas del cliente