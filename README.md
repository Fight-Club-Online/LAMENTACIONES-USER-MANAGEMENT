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