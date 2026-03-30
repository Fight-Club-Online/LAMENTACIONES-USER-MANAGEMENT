# 📌 Lamentaciones – Gestión de usuarions, Registro y Login

---

## 👤 Developers
- Juan Pablo Caballero
- Robinson Nuñez
- Santiago Suarez
- Juan Rangel
- Oscar Andres Sanchez

---

## 📑 Contenido
1. [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
    - [Estructura Hexagonal](#-clean---hexagonal-structure)
2. [Documentación API](#-api-endpoints)
3. [Datos de Entrada y Salida](#input--output-data)
4. [Integración con Microservicios](#-integracion-con-microservicios)
5. [Tecnologías](#-technologies)
6. [Estrategia de Branches](#-branch-strategy--structure)
7. [Arquitectura y Diseño del Sistema](#-system-architecture--design)
8. [Getting Started](#-getting-started)
9. [Testing](#-testing)
10. [Variables de Entorno y Secretos](#-environment-variables)

---

## 🏢 Arquitectura del Proyecto

El módulo **Lamentaciones** maneja una arquitectura **hexagonal / clean** que desacopla la lógica de negocio del resto de la aplicación:

- **🧠 Domain (Core):** Contiene la lógica de negocio y reglas principales.  
- **🎯 Ports (Interfaces):** Interfaces que definen las acciones que el dominio puede realizar.  
- **🔌 Adapters (Infrastructure):** Implementaciones concretas de los ports para conectarse con tecnologías específicas.

**Beneficios:**

- ✔️ **Separation of Concerns:** Lógicas separadas de infraestructura.  
- ✔️ **Maintainability:** Fácil de actualizar componentes.  
- ✔️ **Scalability:** Componentes evolucionan independientemente.  
- ✔️ **Testability:** El dominio puede probarse sin base de datos ni servidor.

---

## 📂 Clean - Hexagonal Structure
```
:📂 LAMENTACIONES-USER-MANAGEMENT
┣ :📂 src/
┃ ┣ :📂 main/
┃ ┃ ┣ :📂 java/
┃ ┃ ┃ ┗ :📂 com/clubfight/LAMENTACIONES-USER-MANAGEMENT
┃ ┃ ┃ ┣ 📄 LamentacionesUserManagementApplication.java
┃ ┃ ┃ ┣ :📂 domain/
┃ ┃ ┃ ┃ ┣ :📂 model/
┃ ┃ ┃ ┃ ┗ :📂 enums/
┃ ┃ ┃ ┣ :📂 application/
┃ ┃ ┃ ┃ ┣ :📂 ports/
┃ ┃ ┃ ┃ ┣ :📂 in/
┃ ┃ ┃ ┃ ┗ :📂 out/
┃ ┃ ┃ ┃ ┣ :📂 mappers/
┃ ┃ ┃ ┃ ┣ :📂 exceptions/
┃ ┃ ┃ ┃ ┣ :📂 events/
┃ ┃ ┃ ┃ ┗ :📂 command/
┃ ┃ ┃ ┃ ┗ :📂 service/
┃ ┃ ┃ ┣ :📂 infrastructure/
┃ ┃ ┃ ┃ ┣ :📂 config/
┃ ┃ ┃ ┃ ┣ :📂 controller/
┃ ┃ ┃ ┃ ┣ :📂 dto/
┃ ┃ ┃ ┃ ┣ :📂 request/
┃ ┃ ┃ ┃ ┗ :📂 response/
┃ ┃ ┃ ┃ ┣ :📂 persistence/
┃ ┃ ┃ ┃ ┣ :📂 repositories/
┃ ┃ ┗ :📂 resources/
┃ ┃ ┗ 📄 application.properties
┣ :📂 test/
┃ ┗ :📂 java/
┣ :📂 docs/
┃ ┣ diagramaClases.jpg
┃ ┣ diagramaDatos.jpg
┃ ┗ diagramaDespliegue.png
┣ 📄 pom.xml
┣ 📄 mvnw / mvnw.cmd
┗ 📄 README.md
```
---

## 📡 API Endpoints

- La documentación completa se encuentra en **Swagger UI**:  
  `http://localhost:8080/swagger-ui.html` (cuando se ejecute localmente).

---

## Input & Output Data

- **Autenticación Google:** recibe `idToken` y devuelve `AuthResponse` con `accessToken`, `refreshToken`, `email`, `username`, `userId`.  

---

## 🛠 Integración con Microservicios

- Comunicación con otros microservicios mediante **RabbitMQ**.  
- Persistencia en **MongoDB**.  
- JWT para autenticación.  
- Integración con **Google OAuth2** para login social.

---

## 🧰 Technologies

### Backend & Core
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

### Database
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-%23DC382D.svg?style=for-the-badge&logo=redis&logoColor=white)

### DevOps & Infrastructure
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
![Azure](https://img.shields.io/badge/Microsoft_Azure-%230072C6.svg?style=for-the-badge&logo=microsoft-azure&logoColor=white)
![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)

### CI/CD & Quality Assurance
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-green?style=for-the-badge)

### Documentation & Testing
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

### Design 
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)
![Miro](https://img.shields.io/badge/Miro-%2316CE9E.svg?style=for-the-badge&logo=miro&logoColor=white)
![Lucidchart](https://img.shields.io/badge/Lucidchart-%23FF6C37.svg?style=for-the-badge&logo=lucidchart&logoColor=white)

### Comunication & Project Management
![Azure DevOps](https://img.shields.io/badge/Azure_DevOps-%23007ACC.svg?style=for-the-badge&logo=azure-devops&logoColor=white)


---

## 🔀 Branch Strategy & Structure

- `main`: Producción estable.  
- `develop`: Últimas características integradas.  
- `feature/<nombre>`: Nuevas funcionalidades.  
- `hotfix/<nombre>`: Correcciones rápidas.  

---

## 🏗 System Architecture & Design

- Hexagonal / Clean Architecture.  
- Domain → Ports → Adapters.  
- Independencia de infraestructura para test y escalabilidad.  

---

## ⚡ Getting Started

### 1️⃣ Clonar el proyecto

```bash
git clone https://github.com/JuanCaballero9778/LAMENTACIONES-USER-MANAGEMENT.git
cd LAMENTACIONES-USER-MANAGEMENT
```
--- 
## Diagrama de Contexto — Fight Club Online

### Descripción General

Este diagrama de contexto muestra el sistema **Fight Club Online** como una caja negra
central, identificando todos los actores externos que interactúan con él y la naturaleza
de cada relación.

[📄 Ver documentación (PDF)](docs/ContexD.pdf)

---

### Sistema Central

**Fight Club** — Plataforma web de juego de lucha multijugador en tiempo real,
accesible desde el navegador sin instalación.

---

### Actores Externos

### 👤 Visitante
- Accede al juego sin estar registrado.
- Puede explorar información, ver personajes, consultar rankings o registrarse para jugar.
- **Interacción:** Envía solicitudes de consulta y registro hacia el sistema.

### 🎮 Jugador
- Usuario registrado o invitado activo dentro del juego.
- Puede seleccionar personajes, controlar su luchador y participar en peleas.
- **Interacción:** Envía acciones de juego (movimientos, ataques) y recibe
  el estado sincronizado de la pelea en tiempo real.

### 👁️ Espectador
- Observa las peleas en tiempo real sin participar directamente en el combate.
- Puede eventualmente unirse como jugador.
- **Interacción:** Recibe el estado del juego en modo solo lectura.

### 🛠️ Administrador
- Gestiona y supervisa la plataforma desde un panel de control.
- Puede monitorear el juego, revisar estadísticas y gestionar actualizaciones o contenido.
- **Interacción:** Envía configuraciones y comandos de administración al sistema;
  recibe reportes y métricas.

---

## Diagrama de Casos de Uso
[📄 Ver documentación (PDF)](docs/UserCaseD.pdf)

### Actores del Sistema

| Actor         | Descripción                                                                 |
|---------------|-----------------------------------------------------------------------------|
| Visitante      | Accede sin cuenta registrada; funcionalidad limitada                       |
| Jugador        | Usuario registrado con acceso completo al juego                            |
| Espectador     | Observa partidas en curso; puede tomar control temporal si se le cede      |
| Administrador  | Gestiona usuarios, partidas, moderación y configuración del sistema        |

---

### Casos de Uso por Actor

---

### 👤 Visitante

| Caso de Uso                  | Descripción                                              |
|------------------------------|----------------------------------------------------------|
| Ingresar como invitado        | Accede al juego sin crear una cuenta                    |
| Borrar registro de invitado   | Elimina la sesión temporal de invitado                  |
| Configurar preferencias       | Ajusta opciones básicas de la sesión                    |
| Abandonar partida             | Sale de una partida en curso                            |
| Participar en combate         | Se une a una pelea activa                               |
| Seleccionar personaje         | Elige el luchador antes del combate                     |
| Ejecutar movimientos          | Controla los movimientos del personaje                  |
| Ejecutar habilidades          | Usa habilidades especiales del personaje                |
| Ver estado de la partida      | Consulta barras de vida, ronda y marcador               |

> El visitante tiene acceso restringido: no conserva historial, estadísticas ni progreso al finalizar la sesión.

---

### 🎮 Jugador

#### Gestión de Usuarios y Accesos

| Caso de Uso          | Descripción                                         |
|----------------------|-----------------------------------------------------|
| Registrarse           | Crea una cuenta con correo o Google OAuth          |
| Iniciar sesión        | Accede con sus credenciales                        |
| Cerrar sesión         | Finaliza la sesión activa                          |
| Recuperar contraseña  | Restablece el acceso a su cuenta                  |
| Editar perfil         | Modifica datos personales y preferencias           |

#### Experiencia de Lobby y Matchmaking

| Caso de Uso                    | Descripción                                              |
|--------------------------------|----------------------------------------------------------|
| Configurar preferencias         | Ajusta opciones de sonido, controles, etc.              |
| Crear partida                   | Abre una sala pública o privada con código              |
| Configurar partida              | Define reglas antes de iniciar el combate               |
| Abandonar partida               | Sale de la sala o búsqueda de matchmaking               |
| Invitar amigos                  | Envía invitación directa a una sala privada             |
| Aceptar / rechazar invitaciones | Gestiona invitaciones entrantes a salas                 |
| Agregar amigos                  | Envía solicitud de amistad a otro jugador               |
| Eliminar amigos                 | Remueve un contacto de su lista                         |
| Ver lista de amigos             | Consulta sus contactos y su estado en línea             |
| Aceptar / rechazar amigos       | Gestiona solicitudes de amistad recibidas               |
| Desbloquear personajes          | Accede a nuevos luchadores con puntos ganados           |

#### Sistema de Combate y Partidas Activas

| Caso de Uso                    | Descripción                                              |
|--------------------------------|----------------------------------------------------------|
| Participar en combate           | Entra activamente a una pelea                           |
| Seleccionar personaje           | Elige su luchador antes del combate                     |
| Ejecutar movimientos            | Controla desplazamiento y acciones físicas              |
| Ejecutar habilidades            | Usa ataques especiales del personaje                    |
| Ver estado de la partida        | Monitorea barras de vida y ronda actual                 |
| Activar / desactivar micrófono  | Controla su participación en el chat de voz             |
| Hablar en chat de voz           | Se comunica con otros jugadores por audio               |
| Recuperar control               | Retoma el control de su personaje cedido temporalmente  |
| Interrumpir ayuda               | Cancela la solicitud de asistencia al espectador        |

#### Supervisión y Control

| Caso de Uso        | Descripción                                        |
|--------------------|----------------------------------------------------|
| Reportar usuarios   | Envía reporte de conducta sobre otro jugador      |
| Denunciar usuarios  | Genera una denuncia formal dentro del sistema     |

> Funciones adicionales relacionadas con el espectador
> (escuchar, silenciar, solicitar ayuda, transferir control)
> se activan durante una partida activa.

---

### 👁️ Espectador

#### Observación de Partidas

| Caso de Uso                | Descripción                                              |
|----------------------------|----------------------------------------------------------|
| Ver partida en tiempo real  | Observa el combate en curso sin participar              |
| Ver estado de los jugadores | Consulta barras de vida y estadísticas en vivo          |
| Cambiar perspectiva         | Alterna la vista de cámara dentro de la partida         |
| Escuchar chat de voz        | Oye la comunicación de audio de los jugadores           |
| Hablar en chat de voz       | Participa en el canal de voz de la sala                 |
| Silenciar jugadores         | Silencia el audio de uno o varios participantes         |
| Ejecutar movimientos        | Disponible solo al tomar control temporal               |
| Ejecutar habilidades        | Disponible solo al tomar control temporal               |

#### Control Temporal (si el jugador solicita ayuda)

| Caso de Uso                        | Descripción                                          |
|------------------------------------|------------------------------------------------------|
| Recibir solicitud de ayuda          | Le llega una petición del jugador activo            |
| Aceptar / rechazar solicitud        | Decide si asume el control del personaje            |
| Tomar control del jugador temp.     | Controla el luchador mientras el jugador descansa   |
| Combatir temporalmente              | Ejecuta acciones en nombre del jugador              |
| Devolver control al jugador         | Regresa el mando al jugador original                |
| Seleccionar personaje               | Solo si se le permite antes de tomar el control     |
| Ver estado de la partida            | Monitorea el combate mientras tiene el control      |
| Ver estadísticas                    | Consulta métricas del combate en curso              |

---

### 🛠️ Administrador

#### Gestión de Usuarios

| Caso de Uso        | Descripción                                     |
|--------------------|-------------------------------------------------|
| Ver usuarios        | Lista todos los jugadores registrados          |
| Editar usuarios     | Modifica datos de cuentas                      |
| Eliminar usuarios   | Borra cuentas del sistema                      |
| Sancionar usuarios  | Aplica advertencias o restricciones            |

#### Gestión de Partidas

| Caso de Uso              | Descripción                                     |
|--------------------------|-------------------------------------------------|
| Unir a partidas           | Se une a una sala activa para supervisar       |
| Supervisar partidas       | Monitorea partidas en curso en tiempo real     |
| Reiniciar partidas        | Fuerza el reinicio de una sala con problemas   |
| Configurar reglas de juego| Define parámetros globales del combate         |

#### Moderación de Canales de Comunicación

| Caso de Uso              | Descripción                                      |
|--------------------------|--------------------------------------------------|
| Supervisar canales de voz | Escucha canales de audio para detectar abusos  |
| Bloquear chat de voz      | Silencia o restringe el canal de voz de una sala|
| Monitorear actividad      | Revisa logs y actividad general del sistema     |

#### Supervisión y Control del Sistema

| Caso de Uso                   | Descripción                                         |
|-------------------------------|-----------------------------------------------------|
| Banear cuentas                 | Aplica baneo permanente a un usuario               |
| Desbanear cuentas              | Revierte un baneo previo                           |
| Ver reportes                   | Consulta reportes pendientes de revisión           |
| Revisar reportes               | Analiza el detalle de reportes recibidos           |
| Suspender cuentas              | Aplica suspensión temporal a un usuario            |
| Configurar parámetros del sistema | Ajusta configuraciones globales de la plataforma|
| Gestionar roles                | Asigna o revoca roles (moderador, admin, etc.)     |
| Ver estadísticas del sistema   | Consulta métricas globales de uso y rendimiento    |

---
